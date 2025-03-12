package com.mnh.blescanner.deviceoperation

import com.mnh.blescanner.deviceoperation.usecase.DeviceOperationUseCase
import com.napco.utils.DataState
import com.napco.utils.DeviceOperationScreen
import com.napco.utils.Utility.Companion.hexStringToByteArray
import com.napco.utils.model.Characteristic
import com.napco.utils.model.DeviceDetails
import com.napco.utils.model.DeviceInfo
import com.napco.utils.model.Service
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class DeviceOperationViewModelTest {
    private var mockDeviceOperationUseCase: DeviceOperationUseCase = mock()
    private lateinit var deviceOperationViewModel: DeviceOperationViewModel

    @Before
    fun setup() {
        deviceOperationViewModel = DeviceOperationViewModel(mockDeviceOperationUseCase)
    }

    @Test
    fun `test device operation screen writeCharacteristic is called successfully`() {
        val deviceOperationScreen = DeviceOperationScreen(
            "AB:CD:ED:FG",
            "240d5183-819a-4627-9ca9-1aa24df29f18",
            "Heart Rate",
            "240d5183-819a-4627-9ca9-1aa24df29f18", listOf("Readable", "Writable")
        )

        val writtenString = "d1 c1"
        val hexString = writtenString.replace("\\s".toRegex(), "")
        val byteArray = hexStringToByteArray(hexString)

        assertEquals(byteArray.size, 2)
        assertEquals(byteArray[0], 0xD1.toByte())
        assertEquals(byteArray[1], 0xC1.toByte())

        deviceOperationViewModel.writeCharacteristic(deviceOperationScreen, "d1c1")

        Mockito.verify(mockDeviceOperationUseCase).writeCharacteristic(
            UUID.fromString(deviceOperationScreen.serviceUUID),
            UUID.fromString(deviceOperationScreen.characteristicUUID),
            byteArray
        )
    }

    @Test
    fun `test device operation screen readCharacteristic is called successfully`() {
        val deviceOperationScreen = DeviceOperationScreen(
            "AB:CD:ED:FG",
            "240d5183-819a-4627-9ca9-1aa24df29f18",
            "Heart Rate",
            "240d5183-819a-4627-9ca9-1aa24df29f18", listOf("Readable", "Writable")
        )

        deviceOperationViewModel.readCharacteristic(deviceOperationScreen)

        Mockito.verify(mockDeviceOperationUseCase).readCharacteristic(
            UUID.fromString(deviceOperationScreen.serviceUUID),
            UUID.fromString(deviceOperationScreen.characteristicUUID),
        )
    }

    @Test
    fun `test device operation screen enabledNotification is called successfully`() {
        val deviceOperationScreen = DeviceOperationScreen(
            "AB:CD:ED:FG",
            "240d5183-819a-4627-9ca9-1aa24df29f18",
            "Heart Rate",
            "240d5183-819a-4627-9ca9-1aa24df29f18", listOf("Readable", "Writable")
        )

        deviceOperationViewModel.enableNotification(deviceOperationScreen)

        Mockito.verify(mockDeviceOperationUseCase).enableNotification(
            UUID.fromString(deviceOperationScreen.serviceUUID),
            UUID.fromString(deviceOperationScreen.characteristicUUID),
        )
    }

    @Test
    fun `bleConnectionResult should emit loading state`(): Unit = runTest {
        val fakeDeviceOperationRepository = FakeDeviceOperationRepo()
        val deviceOperationUseCase = DeviceOperationUseCase(fakeDeviceOperationRepository)

        val viewModel = DeviceOperationViewModel(deviceOperationUseCase)

        val expectedData: DataState<DeviceDetails> = DataState.loading()

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.connectionState.collect {
                if (it is DataState.Loading) {
                    assertEquals(expectedData, it)
                }
            }
        }

        fakeDeviceOperationRepository.emit(expectedData)
    }

    @Test
    fun `bleConnectionResult should emit success state`(): Unit = runTest {
        val generalInfo = DeviceInfo(name = "Your Device name", address = "Device mac address")
        val services: HashMap<Service, List<Characteristic>> = HashMap()
        val expectedDevice = DeviceDetails(generalInfo, services)

        val fakeDeviceOperationRepository = FakeDeviceOperationRepo()
        val deviceOperationUseCase = DeviceOperationUseCase(fakeDeviceOperationRepository)

        val viewModel = DeviceOperationViewModel(deviceOperationUseCase)

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.connectionState.collect {
                if (it is DataState.Success) {
                    assertEquals(expectedDevice, it.data)
                }
            }
        }

        fakeDeviceOperationRepository.emit(DataState.success(expectedDevice))
    }

    @Test
    fun `bleConnectionResult emit error state`(): Unit = runTest {
        val fakeDeviceOperationRepository = FakeDeviceOperationRepo()
        val deviceOperationUseCase = DeviceOperationUseCase(fakeDeviceOperationRepository)

        val viewModel = DeviceOperationViewModel(deviceOperationUseCase)
        val expectedThrowable = Throwable("Error: Disconnected")
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.connectionState.collect {
                if (it is DataState.Error) {
                    assertEquals(expectedThrowable, it.error)
                    assertEquals("Disconnected", it.errorMessage)
                }
            }
        }

        fakeDeviceOperationRepository.emit(DataState.error("Disconnected", expectedThrowable))
    }

}
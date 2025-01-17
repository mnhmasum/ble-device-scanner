package com.mnh.blescanner.deviceoperation

import com.mnh.blescanner.deviceoperation.usecase.DeviceOperationUseCase
import com.napco.utils.DeviceOperationScreen
import com.napco.utils.Utility.Companion.hexStringToByteArray
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import java.util.UUID

class DeviceOperationViewModelTest {
    private var mockDeviceOperationUseCase: DeviceOperationUseCase = mock()
    private lateinit var deviceOperationViewModel: DeviceOperationViewModel

    @Before
    fun setup() {
        deviceOperationViewModel = DeviceOperationViewModel(mockDeviceOperationUseCase)
    }

    @Test
    fun `test device operation screen writeCharacteristic`() {
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
    fun `test device operation screen readCharacteristic`() {
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


}
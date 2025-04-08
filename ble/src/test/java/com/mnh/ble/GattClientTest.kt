package com.mnh.ble

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothProfile
import android.content.Context
import com.mnh.ble.bluetooth.bleconnection.BLEGattClient
import com.napco.utils.DataState
import com.napco.utils.ServerResponseState
import com.napco.utils.model.Characteristic
import com.napco.utils.model.DeviceDetails
import com.napco.utils.model.DeviceInfo
import com.napco.utils.model.Service
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import org.mockito.Mockito.mock
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GattClientTest {
    private var mockContext: Context = mock()

    private var mockBluetoothAdapter: BluetoothAdapter = mock()

    private lateinit var bleGattClient: BLEGattClient

    private val mockScope = CoroutineScope(Dispatchers.Unconfined)

    @Before
    fun setUp() {
        bleGattClient = BLEGattClient(mockContext, mockBluetoothAdapter, mockScope)
    }

    @Test
    fun `test device connect call successfully`() {
        val bluetoothLEDevice = mock(BluetoothDevice::class.java)

        Mockito.`when`(mockBluetoothAdapter.getRemoteDevice(anyString()))
            .thenReturn(bluetoothLEDevice)

        bleGattClient.connect(anyString())

        Mockito.verify(bluetoothLEDevice).connectGatt(any(), Mockito.eq(false), any())
    }

    @Test
    fun `test on connection state change connected`() {
        val status = BluetoothGatt.GATT_SUCCESS
        val newState = BluetoothProfile.STATE_CONNECTED
        val mockBluetoothGatt = mock(BluetoothGatt::class.java)
        bleGattClient.onConnectionStateChange(mockBluetoothGatt, status, newState)
        Mockito.verify(mockBluetoothGatt).discoverServices()
    }

    @Test
    fun `test on connection state change loading`(): Unit = runTest {
        val fakeBLEGattClient = FakeBLEGattClient(mockContext, mockBluetoothAdapter, mockScope)

        var isLoading = false

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            fakeBLEGattClient.connectionState.collect {
                if (it is DataState.Loading) {
                    isLoading = true
                }
            }
        }

        fakeBLEGattClient.emit(DataState.loading())

        assertTrue(isLoading)
    }

    @Test
    fun `test on connection state change disconnected`(): Unit = runTest {
        val fakeBLEGattClient = FakeBLEGattClient(mockContext, mockBluetoothAdapter, mockScope)

        var isDisconnected = false

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            fakeBLEGattClient.connectionState.collect {
                if (it is DataState.Error) {
                    isDisconnected = true
                }
            }
        }

        fakeBLEGattClient.emit(DataState.error("Disconnected", Throwable("Error: Disconnected")))

        assertTrue(isDisconnected)
    }


    @Test
    fun `test onServicesDiscovered success`(): Unit = runTest {
        val fakeBLEGattClient = FakeBLEGattClient(mockContext, mockBluetoothAdapter, mockScope)
        val services: Map<Service, List<Characteristic>> = HashMap()
        val device = DeviceInfo("abc", "address")
        val expectedResult = DeviceDetails(deviceInfo = device, services = services)

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            fakeBLEGattClient.connectionState.collect {
                if (it is DataState.Success) {
                    assertEquals(expectedResult, it.data)
                }
            }
        }

        fakeBLEGattClient.emit(DataState.success(expectedResult))

    }


     @Test
     fun `test on characteristic read`(): Unit = runTest {
         val fakeBLEGattClient = FakeBLEGattClient(mockContext, mockBluetoothAdapter, mockScope)

         val bytes = ByteArray(3)
         bytes[0] = 0x01
         bytes[1] = 0x02
         bytes[2] = 0x03

         backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
             fakeBLEGattClient.serverResponse.collect {
                 if (it is ServerResponseState.ReadSuccess) {
                     assertEquals(bytes, it.data)
                 }
             }
         }

         fakeBLEGattClient.emit(ServerResponseState.readSuccess(bytes))

     }

    /*
     @Test
     fun `test writeCharacteristic success response`(): Unit = runBlocking {
         val mockBluetoothGatt = mock(BluetoothGatt::class.java)
         val characteristics = mock(BluetoothGattCharacteristic::class.java)

         val bytes = ByteArray(3)
         bytes[0] = 0x01
         bytes[1] = 0x02
         bytes[2] = 0x03

         Mockito.`when`(characteristics.value).thenReturn(bytes)

         val job = launch {
             bleConnectionManager.deviceResponse().take(1).collect {
                 if(it is ServerResponseState.WriteSuccess) {
                     assertEquals(bytes, it.data)
                 }
             }

         }

         bleConnectionManager.onCharacteristicWrite(
             mockBluetoothGatt,
             characteristics,
             BluetoothGatt.GATT_SUCCESS
         )

         job.join()
         job.cancel()

     }


     @Test
     fun `test characteristic change response`(): Unit = runBlocking {
         val mockBluetoothGatt = mock(BluetoothGatt::class.java)
         val characteristics = mock(BluetoothGattCharacteristic::class.java)

         val bytes = ByteArray(3).apply {
             this[0] = 0x01
             this[1] = 0x02
             this[2] = 0x03
         }

         Mockito.`when`(characteristics.value).thenReturn(bytes)

         val job = launch {
             bleConnectionManager.deviceResponse().take(1).collect {
                 if (it is ServerResponseState.NotifySuccess) {
                     assertEquals(bytes, it.data)
                 }
             }

         }

         bleConnectionManager.onCharacteristicChanged(
             mockBluetoothGatt,
             characteristics
         )

         job.join()
         job.cancel()
     }


     @Test
     fun `test characteristic read response`(): Unit = runBlocking {
         val mockBluetoothGatt = mock(BluetoothGatt::class.java)
         val characteristics = mock(BluetoothGattCharacteristic::class.java)

         val bytes = ByteArray(3).apply {
             this[0] = 0x01
             this[1] = 0x02
             this[2] = 0x03
         }

         Mockito.`when`(characteristics.value).thenReturn(bytes)

         val job = launch {
             bleConnectionManager.deviceResponse().take(1).collect {
                 when (it) {
                     is ServerResponseState.Loading -> {}
                     is ServerResponseState.NotifySuccess -> {}
                     is ServerResponseState.ReadSuccess -> {
                         assertEquals(bytes, it.data)
                     }

                     is ServerResponseState.WriteSuccess -> {}
                 }
             }

         }

         bleConnectionManager.onCharacteristicRead(
             mockBluetoothGatt,
             characteristics,
             bytes,
             BluetoothGatt.GATT_SUCCESS
         )

         job.join()
         job.cancel()
     }*/
}

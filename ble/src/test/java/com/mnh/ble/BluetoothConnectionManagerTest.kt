package com.mnh.ble

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothProfile
import android.content.Context
import com.mnh.ble.bluetooth.bleconnection.BleConnectionManagerImpl
import com.napco.utils.Utility
import com.napco.utils.model.Characteristic
import com.napco.utils.model.Service
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.Before
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import org.mockito.Mockito.mock
import kotlin.test.Test


class BluetoothConnectionManagerTest {

    private var mockContext: Context = mock()

    private var mockBluetoothAdapter: BluetoothAdapter = mock()

    private lateinit var bleConnectionManager: BleConnectionManagerImpl

    private val dispatcher = TestCoroutineDispatcher()
    //private val mockScope = TestCoroutineScope(dispatcher)

    private val mockScope = CoroutineScope(Dispatchers.Unconfined)


    @Before
    fun setUp() {
        bleConnectionManager =
            BleConnectionManagerImpl(mockContext, mockScope, mockBluetoothAdapter)
    }

    @Test
    fun testConnectCallConnectGattWithBLEDevice() {
        val bluetoothLEDevice = mock(BluetoothDevice::class.java)

        Mockito.`when`(mockBluetoothAdapter.getRemoteDevice(anyString()))
            .thenReturn(bluetoothLEDevice)

        bleConnectionManager.connect(anyString())

        Mockito.verify(bluetoothLEDevice).connectGatt(any(), Mockito.eq(false), any())
    }

    @Test
    fun testOnConnectionStateChangeConnectionSuccess() {
        val status = BluetoothGatt.GATT_SUCCESS
        val newState = BluetoothProfile.STATE_CONNECTED
        val mockBluetoothGatt = mock(BluetoothGatt::class.java)

        bleConnectionManager.onConnectionStateChange(mockBluetoothGatt, status, newState)

        Mockito.verify(mockBluetoothGatt).discoverServices()
    }

    /* @Test
     fun `test onServicesDiscovered success`(): Unit = runBlockingTest{
         val status = BluetoothGatt.GATT_SUCCESS

         val mockBluetoothGatt = mock(BluetoothGatt::class.java)

         val mockDevice = mock(BluetoothDevice::class.java)

         Mockito.`when`(mockBluetoothGatt.device).thenReturn(mockDevice)

         Mockito.`when`(mockBluetoothGatt.device.name).thenReturn("abc")
         Mockito.`when`(mockBluetoothGatt.device.address).thenReturn("address")
         Mockito.`when`(mockBluetoothGatt.device.bondState).thenReturn(0)


         var actualDeviceList: DataState<DeviceDetails>? = null


         val mockConnectionResult: MutableSharedFlow<DataState<DeviceDetails>> = mock()
         bleConnectionManager.gattConnectionResult = mockConnectionResult


         bleConnectionManager.onServicesDiscovered(mockBluetoothGatt, status)


         val y:MutableSharedFlow<String> = mock()
         bleConnectionManager.gc = y


         val expectedMessage = "Hello"

         val messages = y


 //            y.take(1).collect{
 //                println("sfsdfdsfdsf")
 //                println(it)
 //            }

         val u = bleConnectionManager.gc.asSharedFlow().toList()

         println("sfsdfdsfdsf")
         println(u)


         assertEquals(expectedMessage, messages.take(1))

         //launch {
         *//*bleConnectionManager.bleGattConnectionResult().take(1).toList().first()


        val serviceCharacteristicsMap = extractServicesWithCharacteristics(mockBluetoothGatt.services)

        val deviceInfo = DeviceInfo(
            name = mockBluetoothGatt.device.name,
            address = mockBluetoothGatt.device.address,
            generalInfo = "${mockBluetoothGatt.device.bondState}"
        )

        val details = DeviceDetails(deviceInfo = deviceInfo, services = serviceCharacteristicsMap)

        val expectedDeviceList = DataState.success(details)

        assertEquals(expectedDeviceList, actualDeviceList)*//*

        //coVerify { bleConnectionManager["emitAttributes"](mockBluetoothGatt) }
    }*/

    @Test
    fun xyz(): Unit = runBlocking {
        val status = BluetoothGatt.GATT_SUCCESS

        val mockBluetoothGatt = mock(BluetoothGatt::class.java)

        bleConnectionManager.onServicesDiscovered(mockBluetoothGatt, status)


       // val y: Channel<String> = Channel(Channel.BUFFERED)
        bleConnectionManager.gc = Channel(Channel.BUFFERED)


        val expectedMessage = "Hello"

        launch {
            bleConnectionManager.gc.receiveAsFlow().collect {
                println("sfsdfdsfdsf")
                println(it)
            }
        }.join()



    }

    private fun extractServicesWithCharacteristics(serviceList: List<BluetoothGattService>): Map<Service, List<Characteristic>> =
        serviceList.associate { service ->
            val characteristics = service.characteristics.map { bleCharacteristic ->
                Utility.extractCharacteristicInfo(bleCharacteristic)
            }
            val serviceReadableTitleName = Utility.getServiceName(service.uuid)
            val newService = Service(serviceReadableTitleName, service.uuid.toString())
            newService to characteristics
        }

}

package com.mnh.ble

import android.bluetooth.BluetoothAdapter
import android.content.Context
import com.mnh.ble.bluetooth.bleconnection.BLEGattClient
import com.mnh.ble.bluetooth.bleconnection.BleConnectionManagerImpl
import com.napco.utils.DataState
import com.napco.utils.ServerResponseState
import com.napco.utils.model.DeviceDetails
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import org.junit.Before
import org.mockito.Mockito.mock


class BLEConnectionManagerTest {
    private var mockContext: Context = mock()

    private var mockBluetoothAdapter: BluetoothAdapter = mock()

    private var bleGattClient: BLEGattClient = mock()

    private lateinit var bleConnectionManager: BleConnectionManagerImpl

    private val mockScope = CoroutineScope(Dispatchers.Unconfined)

    private val gattConnectionResult: MutableSharedFlow<DataState<DeviceDetails>> =
        MutableSharedFlow(replay = 1)

    private val gattServerResponse: MutableSharedFlow<ServerResponseState<ByteArray>> =
        MutableSharedFlow(replay = 1)

    @Before
    fun setUp() {
        bleConnectionManager = BleConnectionManagerImpl(bleGattClient)
    }

   /* @Test
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

    @Test
    fun `test onServicesDiscovered success`(): Unit = runBlocking {
        val status = BluetoothGatt.GATT_SUCCESS

        val mockBluetoothGatt = mock(BluetoothGatt::class.java)

        val mockDevice = mock(BluetoothDevice::class.java)

        Mockito.`when`(mockBluetoothGatt.device).thenReturn(mockDevice)

        Mockito.`when`(mockBluetoothGatt.device.name).thenReturn("abc")
        Mockito.`when`(mockBluetoothGatt.device.address).thenReturn("address")
        Mockito.`when`(mockBluetoothGatt.device.bondState).thenReturn(0)

        val job = launch {

            val result = bleConnectionManager.connectionState().take(1).toList()

            println(result.toString())

            val expected =
                "[Success(data=DeviceDetails(deviceInfo=DeviceInfo(name=abc, address=address, generalInfo=0), services={}))]"

            assertEquals(expected, result.toString())

        }

        bleConnectionManager.onServicesDiscovered(mockBluetoothGatt, status)

        job.join()
    }

    @Test
    fun `test writeCharacteristic`(): Unit = runBlocking {
        val mockBluetoothGatt = mock(BluetoothGatt::class.java)
        val characteristics = mock(BluetoothGattCharacteristic::class.java)

        val bytes = ByteArray(3)
        bytes[0] = 0x01
        bytes[1] = 0x02
        bytes[2] = 0x03

        bleConnectionManager.setBluetoothGatt(mockBluetoothGatt)

        bleConnectionManager.writeCharacteristic(characteristics, bytes)

        if (Build.VERSION.SDK_INT >= 33) {
            Mockito.verify(mockBluetoothGatt).writeCharacteristic(
                characteristics,
                bytes,
                BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
            )
        } else {
            Mockito.verify(mockBluetoothGatt).writeCharacteristic(characteristics)
        }

    }

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

package com.mnh.ble

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothProfile
import android.content.Context
import com.mnh.ble.bluetooth.bleconnection.BleConnectionManagerImpl
import com.napco.utils.DataState
import com.napco.utils.model.DeviceDetails
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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

    private val mockScope = CoroutineScope(Dispatchers.Unconfined)

    private val gattConnectionResult: MutableSharedFlow<DataState<DeviceDetails>> =
        MutableSharedFlow(replay = 1)

    @Before
    fun setUp() {
        bleConnectionManager =
            BleConnectionManagerImpl(
                mockContext,
                scope = mockScope,
                mockBluetoothAdapter,
                gattConnectionResult = gattConnectionResult
            )
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

            val result = bleConnectionManager.bleGattConnectionResult().take(1).toList()

            println(result.toString())

            val expected = "[Success(data=DeviceDetails(deviceInfo=DeviceInfo(name=abc, address=address, generalInfo=0), services={}))]"

            assertEquals(expected, result.toString())

        }

        bleConnectionManager.onServicesDiscovered(mockBluetoothGatt, status)

        job.join()
    }


}

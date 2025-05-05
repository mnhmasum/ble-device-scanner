package com.mnh.ble

import com.mnh.ble.bluetooth.bleconnection.BLEGattClient
import com.mnh.ble.bluetooth.bleconnection.BleConnectionManagerImpl
import com.napco.utils.DataState
import com.napco.utils.ServerResponseState
import com.napco.utils.model.DeviceDetails
import kotlinx.coroutines.flow.MutableSharedFlow
import org.junit.Before
import org.mockito.Mockito
import org.mockito.Mockito.mock
import kotlin.test.Test


class BLEConnectionManagerTest {
    private var bleGattClient: BLEGattClient = mock()

    private lateinit var bleConnectionManager: BleConnectionManagerImpl

    private val gattConnectionResult: MutableSharedFlow<DataState<DeviceDetails>> =
        MutableSharedFlow(replay = 1)

    private val gattServerResponse: MutableSharedFlow<ServerResponseState<ByteArray>> =
        MutableSharedFlow(replay = 1)

    @Before
    fun setUp() {
        bleConnectionManager = BleConnectionManagerImpl(bleGattClient)
    }

    @Test
    fun testConnectCallConnectGattWithBLEDevice() {
        bleConnectionManager.connect("")
        Mockito.verify(bleGattClient).connect("")
    }
}

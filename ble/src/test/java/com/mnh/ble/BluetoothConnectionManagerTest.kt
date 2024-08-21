package com.mnh.ble

import android.bluetooth.BluetoothGatt
import android.content.Context
import com.mnh.ble.bluetooth.bleconnection.BleConnectionManagerImpl
import io.mockk.mockk
import org.junit.Before
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify
import kotlin.test.Test


class BluetoothConnectionManagerTest {
    private lateinit var bleConnectionManagerImpl: BleConnectionManagerImpl
    private var bluetoothGatt: BluetoothGatt = mock()

    private val context: Context = mockk()

    @Before
    fun setUp() {
        bleConnectionManagerImpl = BleConnectionManagerImpl(context)

    }

    @Test
    fun testBLEConnection() {
        bleConnectionManagerImpl.connect("abcd")
        verify(bluetoothGatt).disconnect()
    }


}
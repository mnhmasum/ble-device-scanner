package com.mnh.ble.connector

import android.bluetooth.BluetoothDevice
import kotlinx.coroutines.flow.Flow

interface BleConnector {
    fun connect(device: BluetoothDevice)
    fun bleGattConnectionResult(): Flow<String?>
}
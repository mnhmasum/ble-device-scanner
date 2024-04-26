package com.mnh.ble.connector

import android.bluetooth.BluetoothDevice
import com.mnh.ble.model.Gatt
import com.napco.utils.DataState
import kotlinx.coroutines.flow.Flow

interface BleConnector {
    fun connect(device: BluetoothDevice)
    fun disconnect()
    fun bleGattConnectionResult(): Flow<DataState<Gatt>>
}
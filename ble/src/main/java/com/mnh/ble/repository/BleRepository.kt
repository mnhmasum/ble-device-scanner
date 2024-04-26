package com.mnh.ble.repository

import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanResult
import com.mnh.ble.model.Gatt
import com.napco.utils.DataState
import kotlinx.coroutines.flow.Flow

interface BleRepository {
    fun fetchBleDeviceList(): Flow<List<ScanResult>>
    fun connect(device: BluetoothDevice)
    fun disconnect()
    fun getGattConnectionResult(): Flow<DataState<Gatt>>
}
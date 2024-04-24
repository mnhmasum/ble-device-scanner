package com.mnh.ble.repository

import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanResult
import kotlinx.coroutines.flow.Flow

interface BleRepository {
    fun fetchBleDevice(): Flow<ScanResult>
    fun fetchBleDeviceList(): Flow<List<ScanResult>>
    fun connect(device: BluetoothDevice)

    fun getGattConnectionResult(): Flow<String?>
}
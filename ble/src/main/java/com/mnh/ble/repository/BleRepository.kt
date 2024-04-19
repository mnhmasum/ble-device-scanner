package com.mnh.ble.repository

import android.bluetooth.le.ScanResult
import kotlinx.coroutines.flow.Flow

interface BleRepository {
    fun fetchBleDevice(): Flow<ScanResult>
    fun fetchBleDeviceList(): Flow<List<ScanResult>>
}
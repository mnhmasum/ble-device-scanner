package com.mnh.blescanner.devicelist.repository

import android.bluetooth.le.ScanResult
import kotlinx.coroutines.flow.Flow

interface DeviceListRepository {
    fun getScannedDeviceList(): Flow<List<ScanResult>>
    fun startScanning()
    fun stopScanning()

}
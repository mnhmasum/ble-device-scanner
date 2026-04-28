package com.mnh.blescanner.devicediscovery.repository

import android.bluetooth.le.ScanResult
import kotlinx.coroutines.flow.Flow

interface DeviceDiscoveryRepository {
    fun getScannedDeviceList(): Flow<List<ScanResult>>
    fun startScanning()
    fun stopScanning()
}
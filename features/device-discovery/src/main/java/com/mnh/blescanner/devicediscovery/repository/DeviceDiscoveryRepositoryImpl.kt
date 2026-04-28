package com.mnh.blescanner.devicediscovery.repository

import android.annotation.SuppressLint
import android.bluetooth.le.ScanResult
import com.mnh.ble.bluetooth.blescanner.BleScanner
import kotlinx.coroutines.flow.Flow

@SuppressLint("MissingPermission")
class DeviceDiscoveryRepositoryImpl(private val bleSource: BleScanner) :
    DeviceDiscoveryRepository {

    override fun getScannedDeviceList(): Flow<List<ScanResult>> {
        return bleSource.scanResults
    }

    override fun startScanning() {
        bleSource.startScanning()
    }

    override fun stopScanning() {
        bleSource.stopScanning()
    }
}

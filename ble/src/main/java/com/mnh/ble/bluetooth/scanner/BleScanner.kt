package com.mnh.ble.bluetooth.scanner

import android.bluetooth.le.ScanResult
import kotlinx.coroutines.flow.Flow

interface BleScanner {
    val scanResults: Flow<List<ScanResult>>
    fun startScanning()
    fun stopScanning()

}
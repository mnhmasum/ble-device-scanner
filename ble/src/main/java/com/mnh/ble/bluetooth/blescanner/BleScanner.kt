package com.mnh.ble.bluetooth.blescanner

import android.bluetooth.le.ScanResult
import kotlinx.coroutines.flow.Flow

interface BleScanner {
    val scanResults: Flow<List<ScanResult>>
    fun startScanning()
    fun stopScanning()

}
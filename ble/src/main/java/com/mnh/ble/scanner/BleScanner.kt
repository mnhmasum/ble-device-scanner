package com.mnh.ble.scanner

import android.bluetooth.le.ScanResult
import kotlinx.coroutines.flow.Flow

interface BleScanner {
    fun startScanningWithList(): Flow<List<ScanResult>>
    fun stopScanning()

}
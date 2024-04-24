package com.mnh.ble.scanner

import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanResult
import kotlinx.coroutines.flow.Flow

interface BleScanner {
    fun startScanning(): Flow<ScanResult>
    fun startScanningWithList(): Flow<List<ScanResult>>
    fun stopScanning()
    fun connect(device: BluetoothDevice)
    fun bleGattConnectionResult(): Flow<String?>

}
package com.mnh.ble.repository

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanResult
import com.mnh.ble.scanner.BleScanner
import kotlinx.coroutines.flow.Flow

@SuppressLint("MissingPermission")
class BleRepositoryImp(private val bleSource: BleScanner) : BleRepository {

    override fun fetchBleDevice(): Flow<ScanResult> {
        return bleSource.startScanning()
    }

    override fun fetchBleDeviceList(): Flow<List<ScanResult>> {
        return bleSource.startScanningWithList()
    }

    override fun connect(device: BluetoothDevice) {
        bleSource.connect(device)
    }

    override fun getGattConnectionResult(): Flow<String?> {
        return bleSource.bleGattConnectionResult()
    }


}

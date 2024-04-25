package com.mnh.ble.scanner

import android.annotation.SuppressLint
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow


@SuppressLint("MissingPermission")
class BleScannerImp(private val bluetoothLeScanner: BluetoothLeScanner) : BleScanner {
    private var scanCallBack: ScanCallback? = null

    override fun startScanningWithList(): Flow<List<ScanResult>> = callbackFlow {
        val listOfScanResult = HashMap<String, ScanResult>()

        scanCallBack = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                listOfScanResult[result.device.address] = result
                trySend(listOfScanResult.values.toList())
            }

            override fun onScanFailed(errorCode: Int) {
                close(IllegalStateException("BLE Scan failed with error code: $errorCode"))
            }
        }

        val settings = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_POWER).build()

        bluetoothLeScanner.startScan(null, settings, scanCallBack)

        awaitClose {
            bluetoothLeScanner.stopScan(scanCallBack)
        }
    }

    override fun stopScanning() {
        if (scanCallBack != null) {
            bluetoothLeScanner.stopScan(scanCallBack)
            scanCallBack = null
        }
    }

}

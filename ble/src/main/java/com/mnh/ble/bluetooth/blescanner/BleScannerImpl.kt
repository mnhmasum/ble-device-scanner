package com.mnh.ble.bluetooth.blescanner

import android.annotation.SuppressLint
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow


@SuppressLint("MissingPermission")
class BleScannerImpl(
    private val bluetoothLeScanner: BluetoothLeScanner,
    private val deviceList: MutableMap<String, ScanResult> = mutableMapOf(),
    private val channel: Channel<List<ScanResult>> = Channel(Channel.BUFFERED),
    private val settings: ScanSettings = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_POWER).build(),
) : BleScanner,
    ScanCallback() {

    override val scanResults: Flow<List<ScanResult>> = channel.receiveAsFlow()

    override fun onScanResult(callbackType: Int, result: ScanResult?) {
        if (result != null) {
            deviceList[result.device.address] = result
            trySend(deviceList.values.toList())
        }
    }

    override fun onScanFailed(errorCode: Int) {
    }

    private fun trySend(results: List<ScanResult>) {
        channel.trySend(results).isSuccess
    }

    override fun startScanning() {
        bluetoothLeScanner.startScan(null, settings, this)
    }

    override fun stopScanning() {
        bluetoothLeScanner.stopScan(this)
    }

}

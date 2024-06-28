package com.mnh.ble.scanner

import android.annotation.SuppressLint
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.util.Log
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow


@SuppressLint("MissingPermission")
class BleScannerImpl(
    private val bluetoothLeScanner: BluetoothLeScanner,
    private val deviceList: MutableMap<String, ScanResult> = mutableMapOf(),
    private val channel: Channel<List<ScanResult>> = Channel(Channel.BUFFERED),
    private val logger: (String) -> Unit = { message -> Log.d("BLE_Connector", message) },
    private val close: (Throwable) -> Unit = { throwable -> channel.close(throwable) },
) : BleScanner,
    ScanCallback() {

    override val scanResults: Flow<List<ScanResult>> = channel.receiveAsFlow()

    override fun onScanResult(callbackType: Int, result: ScanResult?) {
        if (result != null) {
            deviceList[result.device.address] = result
            trySend(deviceList.values.toList())
            logger("onScanResult: ${deviceList.values.toList()}")
        }
    }

    override fun onScanFailed(errorCode: Int) {
        //close(IllegalStateException("BLE Scan failed with error code: $errorCode"))
    }

    private fun trySend(results: List<ScanResult>) {
        channel.trySend(results).isSuccess
    }

    override fun startScanning() {
        val settings = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
            .build()
        bluetoothLeScanner.startScan(null, settings, this)
    }

    override fun stopScanning() {
        bluetoothLeScanner.stopScan(this)
    }

}

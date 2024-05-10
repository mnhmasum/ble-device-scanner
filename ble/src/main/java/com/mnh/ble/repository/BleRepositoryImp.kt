package com.mnh.ble.repository

import android.annotation.SuppressLint
import android.bluetooth.le.ScanResult
import com.mnh.ble.connector.BleConnector
import com.mnh.ble.scanner.BleScanner
import kotlinx.coroutines.flow.Flow

@SuppressLint("MissingPermission")
class BleRepositoryImp(private val bleSource: BleScanner, private val bleConnector: BleConnector) :
    BleRepository {

    override fun getScannedDeviceList(): Flow<List<ScanResult>> {
        return bleSource.startScanningWithList()
    }

    override fun stopScanning() {
        bleSource.stopScanning()
    }


    /* override fun connect(device: BluetoothDevice) {
         bleConnector.connect(device)
     }

     override fun disconnect() {
         bleConnector.disconnect()
     }

     override fun getGattConnectionResult(): Flow<DataState<DeviceInfo>> {
         return bleConnector.bleGattConnectionResult()
     }*/


}

package com.mnh.ble.repository

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanResult
import com.mnh.ble.connector.BleConnector
import com.mnh.ble.model.Gatt
import com.mnh.ble.scanner.BleScanner
import com.napco.utils.DataState
import kotlinx.coroutines.flow.Flow

@SuppressLint("MissingPermission")
class BleRepositoryImp(private val bleSource: BleScanner, private val bleConnector: BleConnector) :
    BleRepository {

    override fun fetchBleDeviceList(): Flow<List<ScanResult>> {
        return bleSource.startScanningWithList()
    }

    override fun connect(device: BluetoothDevice) {
        bleConnector.connect(device)
    }

    override fun disconnect() {
        bleConnector.disconnect()
    }

    override fun getGattConnectionResult(): Flow<DataState<Gatt>> {
        return bleConnector.bleGattConnectionResult()
    }


}

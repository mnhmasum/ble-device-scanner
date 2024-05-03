package com.mnh.features.details.repository

import android.bluetooth.BluetoothDevice
import com.mnh.ble.connector.BleConnector
import com.mnh.ble.model.DeviceInfo
import com.napco.utils.DataState
import kotlinx.coroutines.flow.Flow

class PeripheralDetailsRepositoryImp(private val bleConnector: BleConnector) : PeripheralDetailsRepository {
    override fun connect(device: BluetoothDevice) {
        bleConnector.connect(device)
    }

    override fun disconnect() {
        bleConnector.disconnect()
    }

    override fun getGattConnectionResult(): Flow<DataState<DeviceInfo>> {
        return bleConnector.bleGattConnectionResult()
    }
}
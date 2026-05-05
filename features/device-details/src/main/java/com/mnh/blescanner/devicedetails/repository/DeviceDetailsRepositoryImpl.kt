package com.mnh.blescanner.devicedetails.repository

import com.mnh.ble.bluetooth.bleconnection.BleConnectionManager
import com.mnh.utils.DataState
import com.mnh.utils.model.BleDevice
import kotlinx.coroutines.flow.Flow

class DeviceDetailsRepositoryImpl(private val bleConnectionManager: BleConnectionManager) :
    DeviceDetailsRepository {
    override fun connect(address: String) {
        bleConnectionManager.connect(address)
    }

    override fun disconnect() {
        bleConnectionManager.disconnect()
    }

    override fun getGattConnectionResult(): Flow<DataState<BleDevice>> {
        return bleConnectionManager.connectionState()
    }
}
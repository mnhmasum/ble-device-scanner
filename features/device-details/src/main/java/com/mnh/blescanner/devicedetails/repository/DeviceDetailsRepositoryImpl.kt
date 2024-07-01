package com.mnh.blescanner.devicedetails.repository

import com.mnh.ble.bluetooth.bleconnection.BleConnectionManager
import com.napco.utils.DataState
import com.napco.utils.model.DeviceDetails
import kotlinx.coroutines.flow.Flow

class DeviceDetailsRepositoryImpl(private val bleConnectionManager: BleConnectionManager) :
    DeviceDetailsRepository {
    override fun connect(address: String) {
        bleConnectionManager.connect(address)
    }

    override fun disconnect() {
        bleConnectionManager.disconnect()
    }

    override fun getGattConnectionResult(): Flow<DataState<DeviceDetails>> {
        return bleConnectionManager.bleGattConnectionResult()
    }
}
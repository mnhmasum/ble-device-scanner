package com.mnh.blescanner.devicedetails.repository

import com.mnh.ble.connector.BleConnector
import com.napco.utils.DataState
import com.napco.utils.model.DeviceDetails
import kotlinx.coroutines.flow.Flow

class DeviceDetailsRepositoryImp(private val bleConnector: BleConnector) :
    DeviceDetailsRepository {
    override fun connect(address: String) {
        bleConnector.connect(address)
    }

    override fun disconnect() {
        bleConnector.disconnect()
    }

    override fun getGattConnectionResult(): Flow<DataState<DeviceDetails>> {
        return bleConnector.bleGattConnectionResult()
    }
}
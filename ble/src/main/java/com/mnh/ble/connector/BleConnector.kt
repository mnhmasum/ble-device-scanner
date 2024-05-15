package com.mnh.ble.connector

import com.mnh.ble.model.ServiceInfo
import com.napco.utils.DataState
import kotlinx.coroutines.flow.Flow

interface BleConnector {
    fun connect(address: String)
    fun disconnect()
    fun bleGattConnectionResult(): Flow<DataState<ServiceInfo>>
}
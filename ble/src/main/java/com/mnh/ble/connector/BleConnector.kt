package com.mnh.ble.connector

import com.mnh.ble.model.ServiceInfo
import com.napco.utils.DataState
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface BleConnector {
    fun connect(address: String)
    fun enableNotification(service: UUID, characteristic: UUID)
    fun readCharacteristic(service: UUID, characteristic: UUID)
    fun disconnect()
    fun bleGattConnectionResult(): Flow<DataState<ServiceInfo>>
}
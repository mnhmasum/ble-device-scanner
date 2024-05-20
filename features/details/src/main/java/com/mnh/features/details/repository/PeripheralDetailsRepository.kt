package com.mnh.features.details.repository

import com.mnh.ble.model.ServiceInfo
import com.napco.utils.DataState
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface PeripheralDetailsRepository {
    fun connect(address: String)
    fun disconnect()
    fun getGattConnectionResult(): Flow<DataState<ServiceInfo>>
    fun enableNotification(service: UUID, characteristic: UUID)
    fun readCharacteristic(service: UUID, characteristic: UUID)

}
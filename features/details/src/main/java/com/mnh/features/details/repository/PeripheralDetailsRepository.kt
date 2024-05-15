package com.mnh.features.details.repository

import com.mnh.ble.model.ServiceInfo
import com.napco.utils.DataState
import kotlinx.coroutines.flow.Flow

interface PeripheralDetailsRepository {
    fun connect(address: String)
    fun disconnect()
    fun getGattConnectionResult(): Flow<DataState<ServiceInfo>>
}
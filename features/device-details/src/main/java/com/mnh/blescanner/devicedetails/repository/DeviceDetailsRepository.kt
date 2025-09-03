package com.mnh.blescanner.devicedetails.repository

import com.napco.utils.DataState
import com.napco.utils.model.BleDevice
import kotlinx.coroutines.flow.Flow

interface DeviceDetailsRepository {
    fun connect(address: String)
    fun disconnect()
    fun getGattConnectionResult(): Flow<DataState<BleDevice>>
}
package com.mnh.blescanner.devicedetails.repository

import com.mnh.blescanner.utils.DataState
import com.mnh.blescanner.utils.model.BleDevice
import kotlinx.coroutines.flow.Flow

interface DeviceDetailsRepository {
    fun connect(address: String)
    fun disconnect()
    fun getGattConnectionResult(): Flow<DataState<BleDevice>>
}
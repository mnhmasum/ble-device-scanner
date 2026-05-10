package com.mnh.blescanner.devicedetails.repository

import com.mnh.bledevicescanner.utils.DataState
import com.mnh.bledevicescanner.utils.model.BleDevice
import kotlinx.coroutines.flow.Flow

interface DeviceDetailsRepository {
    fun connect(address: String)
    fun disconnect()
    fun getGattConnectionResult(): Flow<DataState<BleDevice>>
}
package com.mnh.features.details.repository

import android.bluetooth.BluetoothDevice
import com.mnh.ble.model.DeviceInfo
import com.napco.utils.DataState
import kotlinx.coroutines.flow.Flow

interface PeripheralDetailsRepository {
    fun connect(device: BluetoothDevice)
    fun disconnect()
    fun getGattConnectionResult(): Flow<DataState<DeviceInfo>>
}
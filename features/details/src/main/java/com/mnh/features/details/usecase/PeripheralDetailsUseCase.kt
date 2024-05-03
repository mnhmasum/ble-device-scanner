package com.mnh.features.details.usecase

import android.bluetooth.BluetoothDevice
import com.mnh.ble.model.DeviceInfo
import com.mnh.features.details.repository.PeripheralDetailsRepository
import com.napco.utils.DataState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PeripheralDetailsUseCase @Inject constructor(private val peripheralDetailsRepository: PeripheralDetailsRepository) {
    fun connect(device: BluetoothDevice) {
        return peripheralDetailsRepository.connect(device)
    }

    fun disconnect() {
        peripheralDetailsRepository.disconnect()
    }

    fun bleGattConnectionResult(): Flow<DataState<DeviceInfo>> {
        return peripheralDetailsRepository.getGattConnectionResult()
    }

}
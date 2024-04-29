package com.mnh.ble.usecase

import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanResult
import com.mnh.ble.model.DeviceInfo
import com.mnh.ble.repository.BleRepository
import com.napco.utils.DataState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BleUseCase @Inject constructor(private val bleRepository: BleRepository) {

    fun getBleDeviceList(): Flow<List<ScanResult>> {
        return bleRepository.fetchBleDeviceList()
            .flowOn(Dispatchers.IO)
            .map { bleDeviceList ->
                bleDeviceList.sortedByDescending { it.device.address }
            }
    }

    fun connect(device: BluetoothDevice) {
        return bleRepository.connect(device)
    }

    fun disconnect() {
        bleRepository.disconnect()
    }

    fun bleGattConnectionResult(): Flow<DataState<DeviceInfo>> {
        return bleRepository.getGattConnectionResult()
    }

}
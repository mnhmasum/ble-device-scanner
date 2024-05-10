package com.mnh.ble.usecase

import android.annotation.SuppressLint
import android.bluetooth.le.ScanResult
import com.mnh.ble.repository.BleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BleUseCase @Inject constructor(private val bleRepository: BleRepository) {

    @SuppressLint("MissingPermission")
    fun getBleDeviceList(): Flow<List<ScanResult>> {
        return bleRepository.getScannedDeviceList()
            .flowOn(Dispatchers.IO)
            .map { bleDeviceList ->
                bleDeviceList.filterNot { it.device.name == null }
            }
    }

    fun stopScanning() {
        bleRepository.stopScanning()
    }

    /* fun connect(device: BluetoothDevice) {
         return bleRepository.connect(device)
     }

     fun disconnect() {
         bleRepository.disconnect()
     }

     fun bleGattConnectionResult(): Flow<DataState<DeviceInfo>> {
         return bleRepository.getGattConnectionResult()
     }*/

}
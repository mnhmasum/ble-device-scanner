package com.mnh.blescanner.devicelist.usecase

import android.annotation.SuppressLint
import android.bluetooth.le.ScanResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class BleUseCase @Inject constructor(private val deviceListRepository: com.mnh.blescanner.devicelist.repository.DeviceListRepository) {
    @SuppressLint("MissingPermission")
    fun getBleDeviceList(): Flow<List<ScanResult>> {
        return deviceListRepository.getScannedDeviceList()
            .distinctUntilChanged { oldItem, newItem ->
                oldItem == newItem
            }
            /*.map { bleDeviceList ->
                bleDeviceList.filterNot { scanResult ->
                    scanResult.device.name == null
                }
            }*/
            .flowOn(Dispatchers.IO)
    }

    suspend fun startScanning() {
        deviceListRepository.startScanning()
    }

    fun stopScanning() {
        deviceListRepository.stopScanning()
    }

}
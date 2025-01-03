package com.mnh.blescanner.devicelist.usecase

import android.annotation.SuppressLint
import android.bluetooth.le.ScanResult
import com.mnh.blescanner.devicelist.repository.DeviceListRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class BleUseCase @Inject constructor(private val deviceListRepository: DeviceListRepository) {
    @SuppressLint("MissingPermission")
    fun getBleDeviceList(): Flow<List<ScanResult>> {
        return deviceListRepository.getScannedDeviceList()
            .distinctUntilChanged { oldItem, newItem ->
                oldItem == newItem
            }
            .flowOn(Dispatchers.IO)
    }

    suspend fun startScanning() {
        deviceListRepository.startScanning()
    }

    fun stopScanning() {
        deviceListRepository.stopScanning()
    }

}
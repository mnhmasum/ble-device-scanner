package com.mnh.ble.usecase

import android.annotation.SuppressLint
import android.bluetooth.le.ScanResult
import com.mnh.ble.repository.BleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BleUseCase @Inject constructor(private val bleRepository: BleRepository) {
    @SuppressLint("MissingPermission")
    fun getBleDeviceList(): Flow<List<ScanResult>> {
        return bleRepository.getScannedDeviceList()
            .distinctUntilChanged { oldItem, newItem ->
                oldItem == newItem
            }
            .map { bleDeviceList ->
                bleDeviceList.filterNot { scanResult ->
                    scanResult.device.name == null
                }
            }
            .flowOn(Dispatchers.IO)
    }

    suspend fun startScanning() {
        bleRepository.startScanning()
    }

    suspend fun stopScanning() {
        bleRepository.stopScanning()
    }

}
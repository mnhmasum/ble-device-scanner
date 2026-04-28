package com.mnh.blescanner.devicediscovery.usecase

import android.annotation.SuppressLint
import android.bluetooth.le.ScanResult
import com.mnh.blescanner.devicediscovery.repository.DeviceDiscoveryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ObserveDiscoveredDevicesUseCase @Inject constructor(private val deviceDiscoveryRepository: DeviceDiscoveryRepository) {
    @SuppressLint("MissingPermission")
    fun getBleDeviceList(): Flow<List<ScanResult>> {
        return deviceDiscoveryRepository.getScannedDeviceList()
            .distinctUntilChanged { oldItem, newItem ->
                oldItem == newItem
            }
            .flowOn(Dispatchers.IO)
    }

    suspend fun startScanning() {
        deviceDiscoveryRepository.startScanning()
    }

    fun stopScanning() {
        deviceDiscoveryRepository.stopScanning()
    }

}
package com.mnh.ble.usecase

import android.bluetooth.le.ScanResult
import com.mnh.ble.repository.BleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BleUseCase @Inject constructor(private val bleRepository: BleRepository) {
    fun getBleDevice(): Flow<ScanResult> {
        return bleRepository.fetchBleDevice()
            .flowOn(Dispatchers.IO)
    }

    fun getBleDeviceList(): Flow<List<ScanResult>> {
        return bleRepository.fetchBleDeviceList()
            .flowOn(Dispatchers.IO)
            .map { bleDeviceList ->
                bleDeviceList.sortedByDescending { it.device.address }
            }
    }

}
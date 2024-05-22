package com.mnh.ble.usecase

import android.annotation.SuppressLint
import android.bluetooth.le.ScanResult
import com.mnh.ble.repository.BleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class BleUseCase @Inject constructor(private val bleRepository: BleRepository) {
    @OptIn(FlowPreview::class)
    @SuppressLint("MissingPermission")
    fun getBleDeviceList(): Flow<List<ScanResult>> {
        return bleRepository.getScannedDeviceList()
            .debounce(300)
            .flowOn(Dispatchers.IO)
//            .map { bleDeviceList ->
//                bleDeviceList.filterNot { scanResult ->
//                    scanResult.device.name == null
//                }
//            }
            .flowOn(Dispatchers.Main)

    }

    fun stopScanning() {
        bleRepository.stopScanning()
    }

}
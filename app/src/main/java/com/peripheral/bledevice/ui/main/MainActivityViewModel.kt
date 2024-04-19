package com.peripheral.bledevice.ui.main

import android.bluetooth.le.ScanResult
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lightnotebook.data.database.entity.DeviceEntity
import com.lightnotebook.data.usecase.DeviceLocalDataUseCase
import com.mnh.ble.usecase.BleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val deviceLocalDataUseCase: DeviceLocalDataUseCase,
    private val bleUseCase: BleUseCase
) :
    ViewModel() {

    val locks: Flow<List<DeviceEntity>> = deviceLocalDataUseCase.getDeviceList()
    val bleDevice: Flow<ScanResult> = bleUseCase.getBleDevice()
    val bleDeviceList: Flow<List<ScanResult>> = bleUseCase.getBleDeviceList()

    var deviceName by mutableStateOf("C845FF")
        private set

    fun updateLockBroadcastId(input: String) {
        deviceName = input
    }

    fun insert(name: String) {
        val lock = DeviceEntity(1, name)
        viewModelScope.launch(Dispatchers.IO) {
            deviceLocalDataUseCase.insert(lock)
        }

    }

    fun saveLocks(
        toString: String = "478312",
        toString1: String = "337800489559774425",
    ) {
        val lock = DeviceEntity(1, "1")
        viewModelScope.launch(Dispatchers.IO) {
            deviceLocalDataUseCase.insert(lock)
        }
    }

}
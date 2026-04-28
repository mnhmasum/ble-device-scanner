package com.mnh.blescanner.devicediscovery

import android.bluetooth.le.ScanResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnh.blescanner.devicediscovery.usecase.ObserveDiscoveredDevicesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeviceListViewModel @Inject constructor(private val observeDiscoveredDevicesUseCase: ObserveDiscoveredDevicesUseCase) :
    ViewModel() {
    val scannedDeviceList: Flow<List<ScanResult>> =
        observeDiscoveredDevicesUseCase.getBleDeviceList()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun startScanning() {
        viewModelScope.launch(Dispatchers.IO) {
            observeDiscoveredDevicesUseCase.startScanning()
        }
    }

    fun stopScanning() {
        viewModelScope.launch(Dispatchers.IO) {
            observeDiscoveredDevicesUseCase.stopScanning()
        }
    }
}
package com.mnh.features.home

import android.bluetooth.le.ScanResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnh.ble.usecase.BleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val bleUseCase: BleUseCase) :
    ViewModel() {
    val scannedDeviceList: Flow<List<ScanResult>> =
        bleUseCase.getBleDeviceList()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun startScanning() {
        bleUseCase.startScanning()
    }

    fun stopScanning() {
        bleUseCase.stopScanning()
    }

}
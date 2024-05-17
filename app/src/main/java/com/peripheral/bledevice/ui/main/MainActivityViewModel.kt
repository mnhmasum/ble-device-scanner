package com.peripheral.bledevice.ui.main

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
class MainActivityViewModel @Inject constructor(bleUseCase: BleUseCase) :
    ViewModel() {
    val scannedDeviceList: Flow<List<ScanResult>> =
        bleUseCase.getBleDeviceList().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
}
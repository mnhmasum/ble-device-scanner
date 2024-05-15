package com.peripheral.bledevice.ui.main

import android.bluetooth.le.ScanResult
import androidx.lifecycle.ViewModel
import com.mnh.ble.usecase.BleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(bleUseCase: BleUseCase) :
    ViewModel() {
    val scannedDeviceList: Flow<List<ScanResult>> = bleUseCase.getBleDeviceList()
}
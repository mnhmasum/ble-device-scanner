package com.mnh.features.details

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.ViewModel
import com.mnh.ble.model.DeviceInfo
import com.mnh.features.details.usecase.PeripheralDetailsUseCase
import com.napco.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(private val detailsUseCase: PeripheralDetailsUseCase) :
    ViewModel() {
    val bleGattState: Flow<DataState<DeviceInfo>> = detailsUseCase.bleGattConnectionResult()

    fun connect(device: BluetoothDevice) {
        detailsUseCase.connect(device)
    }

    fun disconnect(device: BluetoothDevice) {
        detailsUseCase.disconnect()
    }

}
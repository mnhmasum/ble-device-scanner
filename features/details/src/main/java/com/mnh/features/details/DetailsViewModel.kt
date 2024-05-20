package com.mnh.features.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnh.ble.model.ServiceInfo
import com.mnh.features.details.usecase.PeripheralDetailsUseCase
import com.napco.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(private val detailsUseCase: PeripheralDetailsUseCase) :
    ViewModel() {
    val bleConnectionResult: Flow<DataState<ServiceInfo>> = detailsUseCase.bleGattConnectionResult()
        .stateIn(viewModelScope, SharingStarted.Lazily, DataState.loading())

    fun connect(address: String) {
        detailsUseCase.connect(address)
    }

    fun disconnect() {
        detailsUseCase.disconnect()
    }

    fun enableNotification(service: String, characteristic: String) {
        val serviceUUID = UUID.fromString(service)
        val characteristicUUID = UUID.fromString(characteristic)
        detailsUseCase.enableNotification(serviceUUID, characteristicUUID)
    }

    fun readCharacteristic(service: String, characteristic: String) {
        val serviceUUID = UUID.fromString(service)
        val characteristicUUID = UUID.fromString(characteristic)
        detailsUseCase.readCharacteristic(serviceUUID, characteristicUUID)
    }

}
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
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), DataState.loading())

    fun connect(address: String) {
        detailsUseCase.connect(address)
    }

    fun disconnect() {
        detailsUseCase.disconnect()
    }

    private fun getUUIDs(serviceUUIDString: String, characteristicUUIDString: String): Pair<UUID, UUID> {
        val serviceUUID = UUID.fromString(serviceUUIDString)
        val characteristicUUID = UUID.fromString(characteristicUUIDString)
        return serviceUUID to characteristicUUID
    }

    fun enableNotification(serviceUUIDString: String, characteristicUUIDString: String) {
        val (serviceUUID, characteristicUUID) = getUUIDs(serviceUUIDString, characteristicUUIDString)
        detailsUseCase.enableNotification(serviceUUID, characteristicUUID)
    }

    fun enableIndication(serviceUUIDString: String, characteristicUUIDString: String) {
        val (serviceUUID, characteristicUUID) = getUUIDs(serviceUUIDString, characteristicUUIDString)
        detailsUseCase.enableIndication(serviceUUID, characteristicUUID)
    }

    fun readCharacteristic(serviceUUIDString: String, characteristicUUIDString: String) {
        val (serviceUUID, characteristicUUID) = getUUIDs(serviceUUIDString, characteristicUUIDString)
        detailsUseCase.readCharacteristic(serviceUUID, characteristicUUID)
    }

    fun writeCharacteristic(serviceUUIDString: String, characteristicUUIDString: String) {
        val (serviceUUID, characteristicUUID) = getUUIDs(serviceUUIDString, characteristicUUIDString)
        detailsUseCase.writeCharacteristic(serviceUUID, characteristicUUID)
    }

    fun writeCharacteristicWithNoResponse(serviceUUIDString: String, characteristicUUIDString: String) {
        val (serviceUUID, characteristicUUID) = getUUIDs(serviceUUIDString, characteristicUUIDString)
        detailsUseCase.writeCharacteristicWithNoResponse(serviceUUID, characteristicUUID)
    }

}
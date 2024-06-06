package com.mnh.blescanner.devicedetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.napco.utils.model.DeviceDetails
import com.mnh.blescanner.devicedetails.usecase.DeviceDetailsUseCase
import com.napco.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(private val detailsUseCase: DeviceDetailsUseCase) :
    ViewModel() {

    val bleConnectionResult: Flow<DataState<DeviceDetails>> = detailsUseCase.bleGattConnectionResult()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), DataState.loading())

    val gattServerResponse: Flow<List<ByteArray>> = detailsUseCase.gattServerResponse()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun connect(address: String) {
        viewModelScope.launch(Dispatchers.IO) {
            detailsUseCase.connect(address)
        }
    }

    fun disconnect() {
        viewModelScope.launch(Dispatchers.IO) {
            detailsUseCase.disconnect()
        }
    }

    private fun getUUIDs(
        serviceUUIDString: String,
        characteristicUUIDString: String,
    ): Pair<UUID, UUID> {
        val serviceUUID = UUID.fromString(serviceUUIDString)
        val characteristicUUID = UUID.fromString(characteristicUUIDString)
        return serviceUUID to characteristicUUID
    }

    private fun toUUID(serviceUUIDString: String): UUID {
        return UUID.fromString(serviceUUIDString)
    }

    fun enableNotification(serviceUUIDString: String?, characteristicUUIDString: String?) {
        if (serviceUUIDString == null || characteristicUUIDString == null) return
        val serviceUUID = toUUID(serviceUUIDString)
        val characteristicUUID = toUUID(characteristicUUIDString)
        detailsUseCase.enableNotification(serviceUUID, characteristicUUID)
    }

    fun enableIndication(serviceUUIDString: String?, characteristicUUIDString: String?) {
        if (serviceUUIDString == null || characteristicUUIDString == null) return
        val serviceUUID = toUUID(serviceUUIDString)
        val characteristicUUID = toUUID(characteristicUUIDString)
        detailsUseCase.enableIndication(serviceUUID, characteristicUUID)
    }

    fun readCharacteristic(serviceUUIDString: String?, characteristicUUIDString: String?) {
        if (serviceUUIDString == null || characteristicUUIDString == null) return
        val serviceUUID = toUUID(serviceUUIDString)
        val characteristicUUID = toUUID(characteristicUUIDString)
        detailsUseCase.readCharacteristic(serviceUUID, characteristicUUID)
    }

    fun writeCharacteristic(serviceUUIDString: String?, characteristicUUIDString: String?) {
        if (serviceUUIDString == null || characteristicUUIDString == null) return
        val serviceUUID = toUUID(serviceUUIDString)
        val characteristicUUID = toUUID(characteristicUUIDString)
        detailsUseCase.writeCharacteristic(serviceUUID, characteristicUUID)
    }

    fun writeCharacteristicWithNoResponse(
        serviceUUIDString: String?,
        characteristicUUIDString: String?,
    ) {
        if (serviceUUIDString == null || characteristicUUIDString == null) return
        val serviceUUID = toUUID(serviceUUIDString)
        val characteristicUUID = toUUID(characteristicUUIDString)
        detailsUseCase.writeCharacteristicWithNoResponse(serviceUUID, characteristicUUID)
    }

}
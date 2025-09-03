package com.mnh.blescanner.deviceoperation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnh.blescanner.deviceoperation.usecase.DeviceOperationUseCase
import com.napco.utils.DataState
import com.napco.utils.DeviceOperationScreen
import com.napco.utils.ServerResponseState
import com.napco.utils.Utility.Companion.hexStringToByteArray
import com.napco.utils.model.BleDevice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class DeviceOperationViewModel @Inject constructor(private val detailsUseCase: DeviceOperationUseCase) :
    ViewModel() {

    val connectionState: Flow<DataState<BleDevice>> = detailsUseCase.bleGattConnectionResult()

    val serverResponseState: Flow<ServerResponseState<ByteArray>> = detailsUseCase.gattServerResponse()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ServerResponseState.loading())

    private fun toUUID(serviceUUIDString: String): UUID {
        return UUID.fromString(serviceUUIDString)
    }

    fun enableNotification(deviceDetailsScreen: DeviceOperationScreen) {
        val serviceUUID = toUUID(deviceDetailsScreen.serviceUUID)
        val characteristicUUID = toUUID(deviceDetailsScreen.characteristicUUID)
        detailsUseCase.enableNotification(serviceUUID, characteristicUUID)
    }

    fun enableIndication(deviceDetailsScreen: DeviceOperationScreen) {
        val serviceUUID = toUUID(deviceDetailsScreen.serviceUUID)
        val characteristicUUID = toUUID(deviceDetailsScreen.characteristicUUID)
        detailsUseCase.enableIndication(serviceUUID, characteristicUUID)
    }

    fun readCharacteristic(deviceDetailsScreen: DeviceOperationScreen) {
        val serviceUUID = toUUID(deviceDetailsScreen.serviceUUID)
        val characteristicUUID = toUUID(deviceDetailsScreen.characteristicUUID)
        detailsUseCase.readCharacteristic(serviceUUID, characteristicUUID)
    }

    fun writeCharacteristic(deviceDetailsScreen: DeviceOperationScreen, string: String) {
        val serviceUUID = toUUID(deviceDetailsScreen.serviceUUID)
        val characteristicUUID = toUUID(deviceDetailsScreen.characteristicUUID)
        val hexString = string.replace("\\s".toRegex(), "")
        val byteArray = hexStringToByteArray(hexString)
        detailsUseCase.writeCharacteristic(serviceUUID, characteristicUUID, byteArray)
    }

    fun writeCharacteristicWithNoResponse(deviceDetailsScreen: DeviceOperationScreen, string: String) {
        val serviceUUID = toUUID(deviceDetailsScreen.serviceUUID)
        val characteristicUUID = toUUID(deviceDetailsScreen.characteristicUUID)
        val hexString = string.replace("\\s".toRegex(), "")
        val byteArray = hexStringToByteArray(hexString)
        detailsUseCase.writeCharacteristicWithNoResponse(serviceUUID, characteristicUUID, byteArray)
    }

}
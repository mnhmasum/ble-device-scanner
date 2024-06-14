package com.mnh.blescanner.deviceoperation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnh.blescanner.deviceoperation.usecase.DeviceOperationUseCase
import com.napco.utils.DataState
import com.napco.utils.DeviceOperationScreen
import com.napco.utils.ServerResponseState
import com.napco.utils.model.DeviceDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class DeviceOperationViewModel @Inject constructor(private val detailsUseCase: DeviceOperationUseCase) :
    ViewModel() {

    val bleConnectionResult: Flow<DataState<DeviceDetails>> =
        detailsUseCase.bleGattConnectionResult()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), DataState.loading())

    val gattServerResponse: Flow<ServerResponseState<List<ByteArray>>> = detailsUseCase.gattServerResponse()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ServerResponseState.loading())

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

    private fun hexStringToByteArray(s: String): ByteArray {
        val len = s.length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = ((Character.digit(s[i], 16) shl 4) + Character.digit(s[i + 1], 16)).toByte()
            i += 2
        }
        return data
    }

}
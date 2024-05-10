package com.mnh.features.details

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

    fun connect(address: String):Flow<DataState<DeviceInfo>> {
        detailsUseCase.connect(address)
        return detailsUseCase.bleGattConnectionResult()
    }

    fun disconnect(address: String) {
        detailsUseCase.disconnect()
    }

}
package com.mnh.features.details

import androidx.lifecycle.ViewModel
import com.mnh.ble.model.ServiceInfo
import com.mnh.features.details.usecase.PeripheralDetailsUseCase
import com.napco.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(private val detailsUseCase: PeripheralDetailsUseCase) :
    ViewModel() {
    val bleConnectionResult: Flow<DataState<ServiceInfo>> = detailsUseCase.bleGattConnectionResult()

    fun connect(address: String) {
        detailsUseCase.connect(address)
    }

    fun disconnect() {
        detailsUseCase.disconnect()
    }

}
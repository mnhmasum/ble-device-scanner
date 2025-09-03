package com.mnh.blescanner.devicedetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnh.blescanner.devicedetails.usecase.DeviceDetailsUseCase
import com.napco.utils.DataState
import com.napco.utils.model.BleDevice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(private val detailsUseCase: DeviceDetailsUseCase) : ViewModel() {
    val bleConnectionResult: Flow<DataState<BleDevice>> = detailsUseCase.bleGattConnectionResult()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), DataState.loading())

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

}
package com.peripheral.bledevice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peripheral.bledevice.data.model.MyData
import com.peripheral.bledevice.data.repository.MyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModelOld @Inject constructor(private val myRepository: MyRepository) : ViewModel() {

    val myData: Flow<MyData> = myRepository.getMyData().flowOn(Dispatchers.IO)
    val myData1: Flow<MyData> by lazy {
        myRepository.getMyData().flowOn(Dispatchers.IO)
    }

    val myDataState = MutableStateFlow(MyData(""))

    init {
        viewModelScope.launch(Dispatchers.IO) {
            myRepository.getMyData().collect { myData ->
                myDataState.value = myData
            }
        }
    }

}
package com.peripheral.bledevice.data.repository

import com.peripheral.bledevice.data.model.MyData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MyRepository @Inject constructor() {
    fun getMyData(): Flow<MyData> {
        return flow {
            emit(MyData("Hello, World!"))
        }
    }
}
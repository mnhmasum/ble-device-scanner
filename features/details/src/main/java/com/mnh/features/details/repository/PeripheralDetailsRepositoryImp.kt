package com.mnh.features.details.repository

import com.mnh.ble.connector.BleConnector
import com.mnh.ble.model.ServiceInfo
import com.napco.utils.DataState
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class PeripheralDetailsRepositoryImp(private val bleConnector: BleConnector) :
    PeripheralDetailsRepository {
    override fun connect(address: String) {
        bleConnector.connect(address)
    }

    override fun disconnect() {
        bleConnector.disconnect()
    }

    override fun getGattConnectionResult(): Flow<DataState<ServiceInfo>> {
        return bleConnector.bleGattConnectionResult()
    }

    override fun enableNotification(serviceUUID: UUID, characteristicUUID: UUID) {
        bleConnector.enableNotification(serviceUUID, characteristicUUID)
    }

    override fun enableIndication(serviceUUID: UUID, characteristicUUID: UUID) {
        bleConnector.enableIndication(serviceUUID, characteristicUUID)
    }

    override fun readCharacteristic(service: UUID, characteristic: UUID) {
        bleConnector.readCharacteristic(service, characteristic)
    }

    override fun writeCharacteristic(
        serviceUUID: UUID,
        characteristicUUID: UUID,
        bytes: ByteArray,
    ) {
        bleConnector.writeCharacteristic(serviceUUID, characteristicUUID, bytes)
    }

    override fun writeCharacteristicWithNoResponse(
        serviceUUID: UUID,
        characteristicUUID: UUID,
        bytes: ByteArray,
    ) {
        bleConnector.writeCharacteristicWithNoResponse(serviceUUID, characteristicUUID, bytes)
    }
}
package com.mnh.blescanner.deviceoperation.respository

import com.mnh.ble.bluetooth.bleconnection.BleConnectionManager
import com.napco.utils.DataState
import com.napco.utils.ServerResponseState
import com.napco.utils.model.DeviceDetails
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class DeviceOperationRepositoryImpl(private val bleConnectionManager: BleConnectionManager) :
    DeviceOperationRepository {
    override fun connect(address: String) {
        bleConnectionManager.connect(address)
    }

    override fun disconnect() {
        bleConnectionManager.disconnect()
    }

    override fun getGattConnectionResult(): Flow<DataState<DeviceDetails>> {
        return bleConnectionManager.bleGattConnectionResult()
    }

    override fun gattServerResponse(): Flow<ServerResponseState<ByteArray>> {
        return bleConnectionManager.gattServerResponse()
    }

    override fun enableNotification(serviceUUID: UUID, characteristicUUID: UUID) {
        bleConnectionManager.enableNotification(serviceUUID, characteristicUUID)
    }

    override fun enableIndication(serviceUUID: UUID, characteristicUUID: UUID) {
        bleConnectionManager.enableIndication(serviceUUID, characteristicUUID)
    }

    override fun readCharacteristic(service: UUID, characteristic: UUID) {
        bleConnectionManager.readCharacteristic(service, characteristic)
    }

    override fun writeCharacteristic(
        serviceUUID: UUID,
        characteristicUUID: UUID,
        bytes: ByteArray,
    ) {
        val service = bleConnectionManager.getBluetoothGatt()?.getService(serviceUUID)
        val characteristic = service?.getCharacteristic(characteristicUUID)
        if (characteristic != null) {
            bleConnectionManager.writeCharacteristic(characteristic, bytes)
        }
    }

    override fun writeCharacteristicWithNoResponse(
        serviceUUID: UUID,
        characteristicUUID: UUID,
        bytes: ByteArray,
    ) {
        bleConnectionManager.writeCharacteristicWithNoResponse(
            serviceUUID,
            characteristicUUID,
            bytes
        )
    }
}
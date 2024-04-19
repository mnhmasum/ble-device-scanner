package com.peripheral.bledevice.data.model

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic

data class BleInfo(var gatt: BluetoothGatt, val characteristic: BluetoothGattCharacteristic)

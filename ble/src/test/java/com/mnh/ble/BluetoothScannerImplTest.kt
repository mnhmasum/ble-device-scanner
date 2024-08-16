package com.mnh.ble

import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanSettings
import com.mnh.ble.bluetooth.blescanner.BleScannerImpl
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify


class BluetoothScannerImplTest {
    private lateinit var bleScannerImpl: BleScannerImpl
    private val bluetoothLeScanner: BluetoothLeScanner = mock()
    private val scanSettingsMock: ScanSettings = mock()

    @Before
    fun setUp() {
        bleScannerImpl =
            BleScannerImpl(bluetoothLeScanner = bluetoothLeScanner, settings = scanSettingsMock)
    }

    @Test
    fun testStartScanning() {
        bleScannerImpl.startScanning()
        verify(bluetoothLeScanner).startScan(null, scanSettingsMock, bleScannerImpl)
    }

    @Test
    fun testStopScanning() {
        bleScannerImpl.stopScanning()

        // Verify that stopScan is called with the ScanCallback instance
        verify(bluetoothLeScanner).stopScan(bleScannerImpl)
    }

    /*
    @Test
    fun testOnScanResult() = runBlocking {

    }*/

}
package com.mnh.ble

import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import com.mnh.ble.bluetooth.blescanner.BleScannerImpl
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify


class BluetoothScannerImplTest {
    private lateinit var bleScannerImpl: BleScannerImpl
    private val bluetoothLeScanner: BluetoothLeScanner = mock()
    private val scanSettingsMock: ScanSettings = mock()
    private val scanResult: ScanResult = mockk()

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

    @Test
    fun testOnScanResult(): Unit = runBlocking {
        val deviceAddress = "00:11:22:33:44:55"
        every { scanResult.device.address } returns deviceAddress
        every { scanResult.device } returns mockk()
        // Trigger onScanResult
        bleScannerImpl.onScanResult(1, scanResult)

        // Verify deviceList contains result
        /*val expectedDeviceList = listOf(scanResult)
        val actualDeviceList = mutableListOf<List<ScanResult>>()

        // Collect results from channel
        launch {
            bleScannerImpl.scanResults.take(1).collect {
                actualDeviceList.add(it)
            }
        }.join()

        assertEquals(expectedDeviceList, actualDeviceList.first())*/
        //verify { logger("onScanResult: $expectedDeviceList") }
    }

}
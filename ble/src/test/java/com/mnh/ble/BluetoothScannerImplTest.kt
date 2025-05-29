package com.mnh.ble

import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import com.mnh.ble.bluetooth.blescanner.BleScannerImpl
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import kotlin.test.assertEquals


class BluetoothScannerImplTest {
    private lateinit var bleScannerImpl: BleScannerImpl
    private val bluetoothLeScanner: BluetoothLeScanner = mock()
    private val scanSettingsMock: ScanSettings = mock()
    private val scanResult: ScanResult = mockk()

    @Before
    fun setUp() {
        bleScannerImpl = BleScannerImpl(bluetoothLeScanner = bluetoothLeScanner, settings = scanSettingsMock)
    }

    @Test
    fun testStartScanning() {
        bleScannerImpl.startScanning()
        verify(bluetoothLeScanner).startScan(null, scanSettingsMock, bleScannerImpl)
    }

    @Test
    fun testStopScanning() {
        bleScannerImpl.stopScanning()
        verify(bluetoothLeScanner).stopScan(bleScannerImpl)
    }


    @Test
    fun testOnScanResult() {
        val deviceAddress = "00:1B:2D:D1:32:11"

        every { scanResult.device.address } returns deviceAddress

        bleScannerImpl.onScanResult(1, scanResult)

        assertEquals(scanResult.device.address, deviceAddress)

        val deviceList: MutableMap<String, ScanResult> = mutableMapOf()

        deviceList[deviceAddress] = scanResult

        assertEquals(deviceList[deviceAddress], scanResult)

    }

    @Test
    fun testOnScanResultWithCoroutine(): Unit = runBlocking {
        val deviceAddress = "00:1A:7D:DA:71:13"

        every { scanResult.device.address } returns deviceAddress

        bleScannerImpl.onScanResult(1, scanResult)

        val expectedDeviceList = listOf(scanResult)

        val actualDeviceList = mutableListOf<List<ScanResult>>()

        launch {
            bleScannerImpl.scanResults.take(1).collect {
                actualDeviceList.add(it)
            }
        }.join()

        assertEquals(expectedDeviceList, actualDeviceList.first())

    }


}
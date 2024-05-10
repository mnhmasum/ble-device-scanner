package com.mnh.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanResult
import android.content.Intent
import android.content.SharedPreferences
import android.os.Binder
import android.os.IBinder
import com.mnh.ble.repository.BleRepository
import com.mnh.service.model.LockRSSI
import com.mnh.service.utils.Utility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
@SuppressLint("MissingPermission")
class BluetoothScanService : Service() {
    var deviceName: String = ""

    private val _lockRSSI = MutableStateFlow(LockRSSI())
    var lockRSSI: StateFlow<LockRSSI> = _lockRSSI

    private var job: Job? = null

    private val binder = MyBinder()

    private val scope = CoroutineScope(Dispatchers.Main)

    @Inject
    lateinit var bleScanServiceRepository: BleRepository

    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var notificationChannel: NotificationChannel

    @Inject
    lateinit var notification: Notification

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    inner class MyBinder : Binder() {
        fun getService(): BluetoothScanService = this@BluetoothScanService
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        observeBluetoothScanningResult()
    }

    private fun observeBluetoothScanningResult() {
        job = scope.launch {
            bleScanServiceRepository.getScannedDeviceList().collectLatest { scanResult ->
                /*val device = scanResult.device
                updateLockRSSiInfo(scanResult)
                Log.d(TAG, "In Service : Device: ${device.name} RSS: ${device.address}")*/
            }
        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP_SERVICE) {
            stopBluetoothScan()
            removeNotification()
            return START_NOT_STICKY
        }

        startForegroundService()

        return START_STICKY
    }

    private fun removeNotification() {
        stopForeground(STOP_FOREGROUND_REMOVE);
        stopSelf();
    }

    override fun onDestroy() {
        job?.cancel()
        stopBluetoothScan()
        super.onDestroy()
    }

    private fun createNotificationChannel() {
        notificationManager.createNotificationChannel(notificationChannel)
    }

    private fun startForegroundService() {
        createNotificationChannel()
        startForeground(FOREGROUND_SERVICE_NOTIFICATION_ID, notification)
    }

    private fun updateLockRSSiInfo(result: ScanResult?) {
        val rssiInfoText = "${result?.rssi} dB, Last Updated: ${Utility.getCurrentTime()}"
        val currentLock = _lockRSSI.value
        val device: BluetoothDevice? = result?.device

        val isDeviceFound = device?.name == deviceName
        _lockRSSI.value = if (isDeviceFound) currentLock.copy(lock1 = rssiInfoText) else currentLock
    }

    private fun stopBluetoothScan() {
        //bleScanServiceRepository.stopScan()
    }

    companion object {
        private val TAG = "BluetoothScanService"
        private const val FOREGROUND_SERVICE_NOTIFICATION_ID = 4321
        const val ACTION_STOP_SERVICE = "com.napco.blelock.STOP_SERVICE"
    }
}

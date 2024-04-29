package com.peripheral.bledevice.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanResult
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.lightnotebook.data.database.entity.DeviceEntity
import com.mnh.service.BluetoothScanService
import com.mnh.service.model.LockRSSI
import com.napco.utils.PermissionManager.Companion.permissionManager
import com.peripheral.bledevice.ui.navigation.Navigation
import com.peripheral.bledevice.ui.navigation.Screen
import com.peripheral.bledevice.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var bluetoothScanService: BluetoothScanService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupPermissions()
    }

    private fun setupPermissions() {
        permissionManager {
            context = this@MainActivity
            permissionsToRequest = arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_FINE_LOCATION
            )

            onPermissionGranted {
                setContent {
                    Navigation()
                }
            }

            onPermissionDenied {
                showMessage()
            }

            doRequest()
        }


    }

    private fun showMessage() {
        showToast("Some permissions are denied")
    }


    private fun showToast(message: String) {
        Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
    }

    private fun startBluetoothScanForegroundService() {
        val bleScanServiceConnectionCallback = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder = service as? BluetoothScanService.MyBinder
                bluetoothScanService = binder?.getService()

                setContent {
                    MainContentWithService(service = bluetoothScanService!!)
                }

            }

            override fun onServiceDisconnected(name: ComponentName?) {
                showToast("Service disconnected")
            }
        }

        fun startBleScanningService(bleScanServiceConnectionCallback: ServiceConnection) {
            val serviceIntent = Intent(this, BluetoothScanService::class.java)
            startForegroundService(serviceIntent)
            bindService(serviceIntent, bleScanServiceConnectionCallback, BIND_AUTO_CREATE)
        }

        startBleScanningService(bleScanServiceConnectionCallback)
    }

}


@Composable
fun MainContent(
    navController: NavController,
    bleDeviceList: List<ScanResult>?,
    viewModel: MainActivityViewModel
) {
    MainContentBody(
        viewModel,
        deviceList = bleDeviceList,
        onClick = {
            viewModel.connect(it)
            navController.navigate(Screen.Details.route)
        },
    )
}

@Composable
fun MainContentBody(
    viewModel: MainActivityViewModel,
    deviceList: List<ScanResult>?,
    onClick: (device: BluetoothDevice) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        /*Button(onClick = { onClick(null) }) {
            Text("Save")
        }*/
        ListView(viewModel, scanResults = deviceList, onClick)
    }
}

@Composable
fun ListView(
    viewModel: MainActivityViewModel,
    scanResults: List<ScanResult>?,
    onClick: (device: BluetoothDevice) -> Unit
) {
    scanResults?.let {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            items(scanResults) { scanResult ->
                ListItem(viewModel, scanResult = scanResult, onClick)
            }
        }
    }

}

@SuppressLint("MissingPermission")
@Composable
fun ListItem(
    viewModel: MainActivityViewModel,
    scanResult: ScanResult,
    onClick: (device: BluetoothDevice) -> Unit
) {
    val device = scanResult.device
    val rssi = scanResult.rssi

    Column {
        Text(
            modifier = Modifier.padding(vertical = 8.dp),
            text = device.name ?: "Unknown"
        )

        Text(
            modifier = Modifier.padding(vertical = 4.dp),
            text = device.address
        )

        Text(
            modifier = Modifier.padding(vertical = 4.dp),
            text = "RSSI $rssi"
        )

        Button(onClick = { onClick(device) }) {
            Text("Connect")
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun MainContentWithService(
    service: BluetoothScanService, viewModel: MainActivityViewModel = viewModel()
) {
    val locks by viewModel.locks.collectAsState(initial = emptyList())
    val lockInfo by service.lockRSSI.collectAsState(initial = LockRSSI(""))

    service.deviceName = viewModel.deviceName

    MainContentBodyWithService(list = locks, lockInfo, viewModel) {
        viewModel.insert(viewModel.deviceName)
    }
}


@Composable
fun MainContentBodyWithService(
    list: List<DeviceEntity>,
    lockStatus: LockRSSI,
    viewModel: MainActivityViewModel = viewModel(),
    onClick: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "Name")

        TextField(value = viewModel.deviceName,
            onValueChange = { viewModel.updateLockBroadcastId(it) })

        Spacer(modifier = Modifier.height(30.dp))

        Button(onClick = onClick) {
            Text("Save")
        }

        Text(text = lockStatus.lock1)

    }
}

@Preview(showBackground = true)
@Composable
fun MainPreview() {
    AppTheme {
        MainContentBodyWithService(list = emptyList(), LockRSSI(""), onClick = { })
    }
}

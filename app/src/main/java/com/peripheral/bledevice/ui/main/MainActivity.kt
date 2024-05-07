package com.peripheral.bledevice.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.le.ScanResult
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.lightnotebook.data.database.entity.DeviceEntity
import com.mnh.service.model.LockRSSI
import com.napco.utils.PermissionManager.Companion.permissionManager
import com.peripheral.bledevice.ui.navigation.Navigation
import com.peripheral.bledevice.ui.navigation.Screen
import com.peripheral.bledevice.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

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


}


@Composable
fun MainContent(
    navController: NavController,
    bleDeviceList: List<ScanResult>?,
    viewModel: MainActivityViewModel,
) {
    MainContentBody(
        deviceList = bleDeviceList,
        onClick = {
            navController.navigate("${Screen.Details.route}/$it")
        },
    )
}

@Composable
fun MainContentBody(
    deviceList: List<ScanResult>?,
    onClick: (index: Int) -> Unit,
) {
    Column(modifier = Modifier.padding(16.dp)) {
        ListView(scanResults = deviceList, onClick)
    }
}

@Composable
fun ListView(
    scanResults: List<ScanResult>?,
    onClick: (device: Int) -> Unit,
) {

    val bleDeviceList = scanResults ?: emptyList()

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        itemsIndexed(bleDeviceList) { index, scanResult ->
            ListItem(index, scanResult = scanResult, onClick)
        }
    }

}

@SuppressLint("MissingPermission")
@Composable
fun ListItem(
    index: Int,
    scanResult: ScanResult,
    onClick: (index: Int) -> Unit,
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

        Button(onClick = { onClick(index) }) {
            Text("Connect")
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun MainContentBodyWithService(
    list: List<DeviceEntity>,
    lockStatus: LockRSSI,
    viewModel: MainActivityViewModel = viewModel(),
    onClick: () -> Unit,
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

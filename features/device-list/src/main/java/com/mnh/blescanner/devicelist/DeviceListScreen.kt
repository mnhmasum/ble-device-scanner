package com.mnh.blescanner.devicelist

import android.annotation.SuppressLint
import android.bluetooth.le.ScanResult
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mnh.bledevicescanner.core.Screen
import com.mnh.bledevicescanner.core.theme.AppTheme

@Composable
fun DeviceListScreen(navController: NavController) {
    Log.d("MyComposable", "Main ")
    val homeViewModel: HomeViewModel = hiltViewModel()

    val bleScannedDeviceList by homeViewModel.scannedDeviceList.collectAsStateWithLifecycle(
        emptyList()
    )

    val onClick: (Int) -> Unit = remember(bleScannedDeviceList) {
        { index ->
            val deviceAddress: String = bleScannedDeviceList[index].device?.address ?: ""
            navController.navigate("${Screen.Details.route}/$deviceAddress")
        }
    }

    LaunchedEffect(Unit) {
        homeViewModel.startScanning()
    }

    DisposableEffect(Unit) {
        onDispose {
            homeViewModel.stopScanning()
        }
    }

    MainContentBody(deviceList = bleScannedDeviceList, onClick = {
        onClick(it)
    })
}

@Composable
fun MainContentBody(
    deviceList: List<ScanResult>?,
    onClick: (index: Int) -> Unit,
) {
    Column(modifier = Modifier.padding(16.dp)) {
        DeviceList(scanResults = deviceList, onClick)
    }
}

@Composable
fun DeviceList(
    scanResults: List<ScanResult>?,
    onClick: (device: Int) -> Unit,
) {
    val bleDeviceList = scanResults ?: emptyList()

    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(bleDeviceList.size,
            key = { index -> bleDeviceList[index].device?.address ?: index }) { index ->
            DeviceItem(index, bleDeviceList[index], onClick = { onClick(index) })
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
fun DeviceItem(
    index: Int,
    scanResult: ScanResult,
    onClick: (index: Int) -> Unit,
) {
    val device = scanResult.device
    val rssi = scanResult.rssi

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 8.dp)
            .background(
                color = MaterialTheme.colorScheme.secondary,
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        Column(modifier = Modifier.padding(all = 16.dp)) {
            Text(
                modifier = Modifier.padding(bottom = 8.dp), text = device.name ?: "Unknown"
            )

            Text(
                modifier = Modifier.padding(vertical = 4.dp), text = device.address
            )

            Text(
                modifier = Modifier.padding(vertical = 4.dp), text = "RSSI $rssi"
            )

            Button(onClick = { onClick(index) }) {
                Text("Connect")
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun MainPreview() {
    AppTheme {
        MainContentBody(deviceList = emptyList(), onClick = { })
    }
}

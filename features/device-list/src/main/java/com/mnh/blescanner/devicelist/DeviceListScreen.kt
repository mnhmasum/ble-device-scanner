package com.mnh.blescanner.devicelist

import android.annotation.SuppressLint
import android.bluetooth.le.ScanResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mnh.bledevicescanner.core.theme.AppTheme
import com.napco.utils.DeviceDetailsScreen

@Composable
fun DeviceListScreen(navController: NavController) {
    val deviceListViewModel: DeviceListViewModel = hiltViewModel()

    val bleScannedDeviceList by deviceListViewModel.scannedDeviceList.collectAsStateWithLifecycle(
        emptyList()
    )

    val onClickConnect: (Int) -> Unit = remember(bleScannedDeviceList) {
        { selectedIndex ->
            val deviceAddress: String = bleScannedDeviceList[selectedIndex].device?.address ?: ""
            navController.navigate(DeviceDetailsScreen(deviceAddress))
        }
    }

    LaunchedEffect(Unit) {
        deviceListViewModel.startScanning()
    }

    DisposableEffect(Unit) {
        onDispose {
            deviceListViewModel.stopScanning()
        }
    }

    MainContentBody(deviceList = bleScannedDeviceList, onClickConnect = {
        onClickConnect(it)
    })
}

@Composable
fun MainContentBody(
    deviceList: List<ScanResult>?,
    onClickConnect: (index: Int) -> Unit,
) {
    Column(modifier = Modifier.padding(16.dp)) {
        DeviceList(scanResults = deviceList, onClickConnect)
    }
}

@Composable
fun DeviceList(
    scanResults: List<ScanResult>?,
    onClickConnect: (listIndex: Int) -> Unit,
) {
    val bleDeviceList = scanResults ?: emptyList()

    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(bleDeviceList.size,
            key = { index -> bleDeviceList[index].device?.address ?: index }) { itemIndex ->
            DeviceItem(itemIndex,
                bleDeviceList[itemIndex],
                onClickConnect = { onClickConnect(itemIndex) })
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
fun DeviceItem(
    index: Int,
    scanResult: ScanResult,
    onClickConnect: (index: Int) -> Unit,
) {
    val device = scanResult.device
    val rssi = scanResult.rssi

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 8.dp)
            .background(
                color = MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(8.dp)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .padding(all = 16.dp)
                .weight(1f)
        ) {
            Text(
                text = device.name ?: "Unknown", style = TextStyle(
                    fontSize = 18.sp, fontWeight = FontWeight.SemiBold
                )
            )
            Text(
                modifier = Modifier.padding(vertical = 2.dp),
                text = device.address,
                style = TextStyle(fontSize = 12.sp, color = Color.Gray)
            )
            Text(
                modifier = Modifier.padding(top = 8.dp), text = "RSSI $rssi"
            )
        }

        Button(
            onClick = { onClickConnect(index) },
            shape = RoundedCornerShape(16),
        ) {
            Text("Connect")
        }
    }

}

@Preview(showBackground = true)
@Composable
fun MainPreview() {
    AppTheme {
        MainContentBody(deviceList = emptyList(), onClickConnect = { })
    }
}

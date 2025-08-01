package com.mnh.blescanner.devicelist

import android.annotation.SuppressLint
import android.bluetooth.le.ScanResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
fun DeviceListScreen(navController: NavController) {
    val deviceListViewModel: DeviceListViewModel = hiltViewModel()

    val bleScannedDeviceList by deviceListViewModel.scannedDeviceList.collectAsStateWithLifecycle(
        emptyList()
    )

    val onClickConnect: (Int) -> Unit = remember(bleScannedDeviceList) {
        { selectedIndex ->
            val deviceName: String = bleScannedDeviceList[selectedIndex].device?.name ?: "Unknown"
            val deviceAddress: String = bleScannedDeviceList[selectedIndex].device?.address.orEmpty()
            navController.navigate(DeviceDetailsScreen(deviceName, deviceAddress))
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

    Scaffold(topBar = {
        TopAppBar(title = { Text("BLE Device Scanner") })
    }) { paddingValues ->

        MainContentBody(paddingValues, deviceList = bleScannedDeviceList, onClickConnect = {
            onClickConnect(it)
        })
    }


}

@Composable
fun MainContentBody(
    contentPadding: PaddingValues,
    deviceList: List<ScanResult>?,
    onClickConnect: (index: Int) -> Unit,
) {
    if (deviceList.isNullOrEmpty()) {
        Loader()
    } else {
        Column {
            DeviceList(
                contentPadding,
                scanResults = deviceList,
                onClickConnect
            )
        }
    }

}

@Composable
fun DeviceList(
    contentPadding: PaddingValues,
    scanResults: List<ScanResult>?,
    onClickConnect: (listIndex: Int) -> Unit,
) {
    val bleDeviceList = scanResults ?: emptyList()
    LazyColumn(contentPadding = contentPadding) {
        items(
            bleDeviceList.size, key = { index -> bleDeviceList[index].device?.address ?: index }) { itemIndex ->
            DeviceItem(
                itemIndex, bleDeviceList[itemIndex], onClickConnect = { onClickConnect(itemIndex) })
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
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .background(
                color = MaterialTheme.colorScheme.background, shape = RoundedCornerShape(8.dp)
            ), verticalAlignment = Alignment.CenterVertically
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
                modifier = Modifier.padding(vertical = 2.dp), text = device.address, style = TextStyle(
                    fontSize = 12.sp, color = Color.DarkGray
                )
            )
            Text(
                modifier = Modifier.padding(top = 8.dp), text = "RSSI $rssi"
            )
        }

        Button(
            onClick = { onClickConnect(index) }, shape = RoundedCornerShape(16), modifier = Modifier.padding(end = 8.dp)
        ) {
            Text("Connect")
        }
    }

}

@Composable
fun Loader() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Preview(showBackground = true)
@Composable
fun MainPreview() {
    AppTheme {
        MainContentBody(PaddingValues(16.dp), deviceList = emptyList(), onClickConnect = { })
    }
}

package com.mnh.blescanner.devicedetails

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mnh.ble.utils.Utility
import com.mnh.bledevicescanner.core.Screen

@Composable
fun DeviceOperationScreen(
    navController: NavController,
    screenDeviceOperation: Screen.DeviceOperation,
) {
    val detailsViewModel: DetailsViewModel = hiltViewModel()
    val gattServerResponse by detailsViewModel.gattServerResponse.collectAsStateWithLifecycle(
        initialValue = emptyList()
    )

    BackHandler {
        navController.navigateUp()
    }

    Properties(screenDeviceOperation, { gattServerResponse }, onClickRead = {
        detailsViewModel.readCharacteristic(
            screenDeviceOperation.service, screenDeviceOperation.characteristic
        )
    }, onClickNotification = {
        detailsViewModel.enableNotification(
            screenDeviceOperation.service, screenDeviceOperation.characteristic
        )
    }, onClickWrite = {
        detailsViewModel.writeCharacteristic(
            screenDeviceOperation.service, screenDeviceOperation.characteristic
        )
    })

}

@Composable
fun Properties(
    serviceOperationDetails: Screen.DeviceOperation,
    gattServerResponse: () -> List<ByteArray>,
    onClickRead: () -> Unit,
    onClickNotification: () -> Unit,
    onClickWrite: () -> Unit,
) {
    Column(modifier = Modifier.padding(all = 16.dp)) {

        OperationTitle("PROPERTIES")
        Spacer(modifier = Modifier.height(16.dp))

        RowItem("Device Name", serviceOperationDetails.deviceName)
        Spacer(modifier = Modifier.height(16.dp))

        RowItem("Device Address", "2343535")
        Spacer(modifier = Modifier.height(16.dp))

        RowItem("Device Address", "2343535")
        Spacer(modifier = Modifier.height(16.dp))

        RowItem("Device Address", "2343535")
        Spacer(modifier = Modifier.height(16.dp))

        ReadAndNotifyIndicationOperation(
            serviceOperationDetails,
            onClickRead,
            onClickNotification,
            gattServerResponse
        )

        WriteOperation(serviceOperationDetails, onClickWrite)

        OperationTitle("DESCRIPTORS")
        BasicText(text = "Not implemented yet")

    }
}

@Composable
private fun WriteOperation(
    serviceOperationDetails: Screen.DeviceOperation,
    onClickWrite: () -> Unit,
) {
    val isWritable = serviceOperationDetails.properties.any { it.contains("Writable") }
    if (!isWritable) {
        return
    }

    OperationTitle("Write Operation")

    Row {
        Button(onClick = onClickWrite) {
            Text(text = "WRITE")
        }
        Spacer(modifier = Modifier.width(16.dp))
        Button(onClick = onClickWrite) {
            Text(text = "WRITE WITH NO RESPONSE")
        }
    }

    Spacer(modifier = Modifier.height(20.dp))
}

@Composable
private fun OperationTitle(title: String) {
    BasicText(
        text = title, style = TextStyle(
            fontWeight = FontWeight.Bold, fontSize = 16.sp
        )
    )
    Divider(
        color = Color.Gray, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
private fun ReadAndNotifyIndicationOperation(
    serviceOperationDetails: Screen.DeviceOperation,
    onClickRead: () -> Unit,
    onClickNotification: () -> Unit,
    gattServerResponse: () -> List<ByteArray>,
) {
    val isReadable = serviceOperationDetails.properties.any { it.contains("Readable") }
    val isNotifyAble = serviceOperationDetails.properties.any {
        it.contains("Notify") || it.contains("Indication")
    }

    if (!isNotifyAble && !isReadable) {
        return
    }

    BasicText(text = "READ/INDICATED VALUES")

    Divider(
        color = Color.Gray, thickness = 0.5.dp,
        modifier = Modifier.padding(vertical = 4.dp)
    )

    ActionButtonsRow(
        isReadable = isReadable,
        isNotifyAble = isNotifyAble,
        onClickRead = onClickRead,
        onClickNotification = onClickNotification
    )

    BasicText(text = "Response:")
    ResponseList(gattServerResponse = gattServerResponse)
    Spacer(modifier = Modifier.height(20.dp))
}

@Composable
fun ResponseList(gattServerResponse: () -> List<ByteArray>) {
    gattServerResponse.invoke().forEach {
        Text(text = Utility.bytesToHexString(it))
    }
}

@Composable
fun ActionButton(
    isVisible: Boolean,
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
) {
    if (isVisible) {
        Button(onClick = onClick, modifier = modifier) {
            Text(text = text)
        }
    }
}


@Composable
fun ActionButtonsRow(
    isReadable: Boolean,
    isNotifyAble: Boolean,
    onClickRead: () -> Unit,
    onClickNotification: () -> Unit,
) {
    Row {
        ActionButton(
            isVisible = isReadable,
            onClick = onClickRead,
            text = "READ",
            modifier = Modifier.padding(end = 20.dp)
        )
        ActionButton(
            isVisible = isNotifyAble,
            onClick = onClickNotification,
            text = "SUBSCRIBE"
        )
    }
}

@Composable
private fun RowItem(title: String, value: String?) {
    BasicText(text = title)
    if (value != null) {
        BasicText(text = value)
    }
}






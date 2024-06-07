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
import com.napco.utils.DeviceOperationScreen
import com.napco.utils.Utility

@Composable
fun DeviceOperationScreen(
    navController: NavController,
    deviceOperationScreen: DeviceOperationScreen,
) {
    val detailsViewModel: DetailsViewModel = hiltViewModel()
    val gattServerResponse by detailsViewModel.gattServerResponse.collectAsStateWithLifecycle(
        initialValue = emptyList()
    )

    BackHandler {
        navController.navigateUp()
    }

    Properties(
        deviceOperationScreen,
        { gattServerResponse },
        onClickRead = {
            detailsViewModel.readCharacteristic(deviceOperationScreen)
        },
        onClickWrite = {
            detailsViewModel.writeCharacteristic(deviceOperationScreen)
        },
        onClickWriteWithoutResponse = {
            detailsViewModel.writeCharacteristicWithNoResponse(deviceOperationScreen)
        },
        onClickNotification = {
            detailsViewModel.enableNotification(deviceOperationScreen)
        },
        onClickIndication = {
            detailsViewModel.enableIndication(deviceOperationScreen)
        })

}

@Composable
fun Properties(
    deviceOperationScreen: DeviceOperationScreen,
    gattServerResponse: () -> List<ByteArray>,
    onClickRead: () -> Unit,
    onClickWrite: () -> Unit,
    onClickWriteWithoutResponse: () -> Unit,
    onClickNotification: () -> Unit,
    onClickIndication: () -> Unit,
) {
    Column(modifier = Modifier.padding(all = 16.dp)) {

        OperationTitle("PROPERTIES")

        Spacer(modifier = Modifier.height(16.dp))

        RowItem("Device Address", deviceOperationScreen.deviceAddress)

        RowItem("Characteristic Name", deviceOperationScreen.characteristicName)

        ReadAndNotifyIndicationOperation(
            deviceOperationScreen,
            gattServerResponse,
            onClickRead,
            onClickNotification,
            onClickIndication
        )

        WriteOperation(deviceOperationScreen, onClickWrite, onClickWriteWithoutResponse)

        OperationTitle("DESCRIPTORS")
        BasicText(text = "Not implemented yet")

    }
}

@Composable
private fun WriteOperation(
    deviceOperationScreen: DeviceOperationScreen,
    onClickWrite: () -> Unit,
    onClickWriteWithoutResponse: () -> Unit,
) {
    val isWritable = deviceOperationScreen.properties.any {
        it.contains("Writable")
    }

    if (!isWritable) {
        return
    }

    OperationTitle("WRITE")

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
    deviceOperationScreen: DeviceOperationScreen,
    gattServerResponse: () -> List<ByteArray>,
    onClickRead: () -> Unit,
    onClickNotification: () -> Unit,
    onClickIndication: () -> Unit,
) {
    val properties = deviceOperationScreen.properties
    val isReadable = properties.any { it.contains("Readable") }
    val isNotifyAble = properties.any { it.contains("Notify") }
    val isSupportIndication = properties.any { it.contains("Indication") }

    if (isNotifyAble || isSupportIndication || isReadable) {
        OperationTitle("READ/INDICATED VALUES")

        Row {
            if (isNotifyAble) {
                Button(onClick = onClickNotification, modifier = Modifier.padding(end = 20.dp)) {
                    Text(text = "Notify")
                }
            }

            if (isSupportIndication) {
                Button(onClick = onClickIndication, modifier = Modifier.padding(end = 20.dp)) {
                    Text(text = "Indication")
                }
            }

            if (isReadable) {
                Button(onClick = onClickRead, modifier = Modifier.padding(end = 20.dp)) {
                    Text(text = "Read")
                }
            }

        }

        BasicText(text = "Response:")
        ResponseList(gattServerResponse = gattServerResponse)
        Spacer(modifier = Modifier.height(20.dp))

    }

}

@Composable
fun ResponseList(gattServerResponse: () -> List<ByteArray>) {
    gattServerResponse.invoke().forEach {
        Text(text = Utility.bytesToHexString(it))
    }
}

@Composable
private fun RowItem(title: String, value: String) {
    BasicText(text = title)
    BasicText(text = value)
    Spacer(modifier = Modifier.height(16.dp))
}






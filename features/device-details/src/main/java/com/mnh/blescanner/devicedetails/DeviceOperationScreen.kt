package com.mnh.blescanner.devicedetails

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.napco.utils.Constants
import com.napco.utils.DeviceOperationScreen
import com.napco.utils.ServerResponseState
import com.napco.utils.Utility

@Composable
fun DeviceOperationScreen(
    navController: NavController,
    deviceOperationScreen: DeviceOperationScreen,
) {
    val detailsViewModel: DetailsViewModel = hiltViewModel()
    val serverResponse by detailsViewModel.gattServerResponse.collectAsStateWithLifecycle(
        initialValue = ServerResponseState.loading()
    )

    BackHandler {
        navController.navigateUp()
    }

    Properties(
        deviceOperationScreen,
        serverResponse,
        onClickRead = {
            detailsViewModel.readCharacteristic(deviceOperationScreen)
        },
        onClickWrite = {
            detailsViewModel.writeCharacteristic(deviceOperationScreen, it)
        },
        onClickWriteWithoutResponse = {
            detailsViewModel.writeCharacteristicWithNoResponse(deviceOperationScreen, it)
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
    serverResponse: ServerResponseState<List<ByteArray>>,
    onClickRead: () -> Unit,
    onClickWrite: (String) -> Unit,
    onClickWriteWithoutResponse: (String) -> Unit,
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
            serverResponse,
            onClickRead,
            onClickNotification,
            onClickIndication
        )

        WriteOperation(
            deviceOperationScreen,
            serverResponse,
            onClickWrite,
            onClickWriteWithoutResponse
        )

        OperationTitle("DESCRIPTORS")
        BasicText(text = "Not implemented yet")

    }
}

@Composable
private fun WriteOperation(
    deviceOperationScreen: DeviceOperationScreen,
    gattServerResponse: ServerResponseState<List<ByteArray>>,
    onClickWrite: (String) -> Unit,
    onClickWriteWithoutResponse: (String) -> Unit,
) {

    var text by remember { mutableStateOf(TextFieldValue("")) }

    val isWritable = deviceOperationScreen.properties.any {
        it == Constants.CharType.WRITABLE.type
    }

    val isWritableNoResponse = deviceOperationScreen.properties.any {
        it.contains(Constants.CharType.WRITABLE_NO_RESPONSE.type)
    }

    if (!isWritable && !isWritableNoResponse) {
        return
    }

    OperationTitle("WRITE")

    OutlinedTextField(
        value = text,
        onValueChange = {
            text = it
        },
        placeholder = { Text("ex: D1 D2 D3") },
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(8.dp))

    Row {
        if (isWritable) {
            Button(onClick = { onClickWrite(text.text) }) {
                Text(text = "WRITE")
            }

            Spacer(modifier = Modifier.width(16.dp))
        }


        if (isWritableNoResponse) {
            Button(onClick = { onClickWriteWithoutResponse(text.text) }) {
                Text(text = "WRITE WITHOUT RESPONSE")
            }
        }
    }

    Spacer(modifier = Modifier.height(20.dp))

    if (!isWritable) {
        return
    }

    when (gattServerResponse) {
        is ServerResponseState.WriteSuccess -> gattServerResponse.data.forEach {
            Text(text = Utility.bytesToHexString(it))
        }

        else -> {}
    }

}

@Composable
private fun OperationTitle(title: String) {
    BasicText(
        text = title,
        style = TextStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    )
    Divider(
        color = Color.Gray, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
private fun ReadAndNotifyIndicationOperation(
    deviceOperationScreen: DeviceOperationScreen,
    gattServerResponse: ServerResponseState<List<ByteArray>>,
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

        ResponseView(gattServerResponse)

    }

}

@Composable
private fun ResponseView(gattServerResponse: ServerResponseState<List<ByteArray>>) {
    BasicText(text = "Response:")
    ResponseList(gattServerResponse = gattServerResponse)
    Spacer(modifier = Modifier.height(20.dp))
}

@Composable
fun ResponseList(gattServerResponse: ServerResponseState<List<ByteArray>>) {

    when (gattServerResponse) {

        is ServerResponseState.NotifySuccess -> {
            gattServerResponse.data.forEach {
                Text(text = Utility.bytesToHexString(it))
            }
        }

        is ServerResponseState.ReadSuccess -> gattServerResponse.data.forEach {
            Text(text = Utility.bytesToHexString(it))
        }

        else -> {}

    }

}

@Composable
private fun RowItem(title: String, value: String) {
    BasicText(text = title)
    BasicText(text = value)
    Spacer(modifier = Modifier.height(16.dp))
}

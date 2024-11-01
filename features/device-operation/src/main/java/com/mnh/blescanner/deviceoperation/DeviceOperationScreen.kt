package com.mnh.blescanner.deviceoperation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
    val detailsViewModel: DeviceOperationViewModel = hiltViewModel()
    val serverResponse by detailsViewModel.gattServerResponse.collectAsStateWithLifecycle(
        initialValue = ServerResponseState.loading()
    )

    BackHandler {
        navController.navigateUp()
    }

    Scaffold(topBar = {
        TopBar(
            deviceName = "Device Operation",
            onNavigationIconClick = { navController.navigateUp() })
    }) { paddingValues ->
        DeviceOperationContent(
            paddingValues = paddingValues,
            deviceOperationScreen = deviceOperationScreen,
            serverResponse = serverResponse,
            detailsViewModel = detailsViewModel
        )
    }

}

@Composable
private fun DeviceOperationContent(
    paddingValues: PaddingValues,
    deviceOperationScreen: DeviceOperationScreen,
    serverResponse: ServerResponseState<ByteArray>,
    detailsViewModel: DeviceOperationViewModel,
) {
    val contentPadding = Modifier.padding(
        start = 16.dp,
        end = 16.dp,
        top = paddingValues.calculateTopPadding(),
        bottom = paddingValues.calculateBottomPadding()
    )

    Box(modifier = contentPadding) {
        Properties(deviceOperationScreen = deviceOperationScreen,
            serverResponse = serverResponse,
            onClickRead = { detailsViewModel.readCharacteristic(deviceOperationScreen) },
            onClickWrite = { detailsViewModel.writeCharacteristic(deviceOperationScreen, it) },
            onClickWriteWithoutResponse = {
                detailsViewModel.writeCharacteristicWithNoResponse(
                    deviceOperationScreen, it
                )
            },
            onClickNotification = { detailsViewModel.enableNotification(deviceOperationScreen) },
            onClickIndication = { detailsViewModel.enableIndication(deviceOperationScreen) })
    }
}

@Composable
fun Properties(
    deviceOperationScreen: DeviceOperationScreen,
    serverResponse: ServerResponseState<ByteArray>,
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
            deviceOperationScreen, serverResponse, onClickWrite, onClickWriteWithoutResponse
        )

        OperationTitle("DESCRIPTORS")
        BasicText(text = "Not implemented yet")

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(deviceName: String, onNavigationIconClick: () -> Unit) {
    TopAppBar(title = { Text(deviceName) }, navigationIcon = {
        IconButton(onClick = onNavigationIconClick) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
        }
    })
}


@Composable
private fun WriteOperation(
    deviceOperationScreen: DeviceOperationScreen,
    gattServerResponse: ServerResponseState<ByteArray>,
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

    OutlinedTextField(value = text, onValueChange = {
        text = it
    }, placeholder = { Text("ex: D1 D2 D3") }, modifier = Modifier.fillMaxWidth()
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
        is ServerResponseState.WriteSuccess -> {
            Text(text = Utility.bytesToHexString(gattServerResponse.data))
        }

        else -> {}
    }

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
    gattServerResponse: ServerResponseState<ByteArray>,
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

        val buttonPaddingEnd = Modifier.padding(end = 20.dp)

        Row {
            if (isNotifyAble) {
                Button(onClick = onClickNotification, modifier = buttonPaddingEnd) {
                    Text(text = "Notify")
                }
            }

            if (isSupportIndication) {
                Button(onClick = onClickIndication, modifier = buttonPaddingEnd) {
                    Text(text = "Indication")
                }
            }

            if (isReadable) {
                Button(onClick = onClickRead, modifier = buttonPaddingEnd) {
                    Text(text = "Read")
                }
            }

        }

        ResponseView(gattServerResponse)

    }

}

@Composable
private fun ResponseView(gattServerResponse: ServerResponseState<ByteArray>) {
    BasicText(text = "Response:")
    ResponseList(gattServerResponse = gattServerResponse)
    Spacer(modifier = Modifier.height(20.dp))
}

@Composable
fun ResponseList(gattServerResponse: ServerResponseState<ByteArray>) {

    when (gattServerResponse) {

        is ServerResponseState.Loading -> {
            Text(text = "(Click Read/Notify Button to get response)")
        }

        is ServerResponseState.NotifySuccess -> {
            Text(text = Utility.bytesToHexString(gattServerResponse.data))
        }

        is ServerResponseState.ReadSuccess -> {
            Text(text = Utility.bytesToHexString(gattServerResponse.data))
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

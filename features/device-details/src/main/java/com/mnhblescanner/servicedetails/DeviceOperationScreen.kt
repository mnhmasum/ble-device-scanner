package com.mnhblescanner.servicedetails

import android.util.Log
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

@Composable
fun DeviceOperationScreen(navController: NavController, service: String, characteristic: String) {
    val detailsViewModel: DetailsViewModel = hiltViewModel()
    val gattServerResponse by detailsViewModel.gattServerResponse.collectAsStateWithLifecycle(
        initialValue = emptyList()
    )

    BackHandler {
        navController.navigateUp()
    }

    Properties(
        { gattServerResponse },
        onClickRead = {
            detailsViewModel.readCharacteristic(service, characteristic)
        },
        onClickNotification = {
            detailsViewModel.enableNotification(service, characteristic)
        },
        onClickWrite = {
            detailsViewModel.writeCharacteristic(service, characteristic)
        }
    )

}

@Composable
fun Properties(
    gattServerResponse: () -> List<ByteArray>,
    onClickRead: () -> Unit,
    onClickNotification: () -> Unit,
    onClickWrite: () -> Unit,
) {
    Column(modifier = Modifier.padding(all = 16.dp)) {

        OperationTitle("PROPERTIES")
        Spacer(modifier = Modifier.height(16.dp))

        RowItem()
        Spacer(modifier = Modifier.height(16.dp))

        RowItem()
        Spacer(modifier = Modifier.height(16.dp))

        RowItem()
        Spacer(modifier = Modifier.height(16.dp))

        RowItem()
        Spacer(modifier = Modifier.height(16.dp))

        ReadAndNotifyIndicationOperation(
            onClickRead,
            onClickNotification,
            gattServerResponse
        )

        WriteOperation(onClickWrite)

        OperationTitle("DESCRIPTORS")
        BasicText(text = "Not implemented yet")

    }
}

@Composable
private fun WriteOperation(onClickWrite: () -> Unit) {
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
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    )
    Divider(
        color = Color.Gray,
        thickness = 0.5.dp,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
private fun ReadAndNotifyIndicationOperation(
    onClickRead: () -> Unit,
    onClickNotification: () -> Unit,
    gattServerResponse: () -> List<ByteArray>,
) {
    BasicText(text = "READ/INDICATED VALUES")

    Divider(
        color = Color.Gray,
        thickness = 0.5.dp,
        modifier = Modifier.padding(vertical = 4.dp)
    )

    Row {
        Button(onClick = onClickRead) {
            Text(text = "READ")
        }
        Spacer(modifier = Modifier.width(16.dp))
        Button(onClick = onClickNotification) {
            Text(text = "SUBSCRIBE")
        }
    }

    BasicText(text = "Response:")
    gattServerResponse.invoke().forEach {
        Text(text = Utility.bytesToHexString(it))
    }
    Spacer(modifier = Modifier.height(20.dp))
}

@Composable
private fun RowItem() {
    BasicText(text = "Device Address")
    BasicText(text = "D6:55....")
}




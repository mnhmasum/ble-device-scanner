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
    val response by detailsViewModel.gattServerResponse.collectAsStateWithLifecycle(initialValue = emptyList())

    BackHandler {
        navController.navigateUp()
    }

    Properties(
        onClickRead = {
            detailsViewModel.readCharacteristic(service, characteristic)
            Log.d("onclick Read", "DeviceOperationScreen: ")
        },
        onClickNotification = {
            Log.d("onclick subscribe", "DeviceOperationScreen: ")
            detailsViewModel.enableNotification(service, characteristic)
        },
        onClickWrite = {
            detailsViewModel.writeCharacteristic(service, characteristic)
        },
        { response }
    )

}

@Composable
fun Properties(
    onClickRead: () -> Unit,
    onClickNotification: () -> Unit,
    onClickWrite: () -> Unit,
    result: () -> List<ByteArray>,
) {
    Column(modifier = Modifier.padding(all = 16.dp)) {
        BasicText(
            text = "PROPERTIES",
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)
        )

        Divider(
            color = Color.Gray,
            thickness = 0.5.dp,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))
        RowItem()
        Spacer(modifier = Modifier.height(16.dp))

        RowItem()
        Spacer(modifier = Modifier.height(16.dp))

        RowItem()
        Spacer(modifier = Modifier.height(16.dp))

        RowItem()
        Spacer(modifier = Modifier.height(16.dp))

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

            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = onClickWrite) {
                Text(text = "WRITE")
            }

            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = onClickWrite) {
                Text(text = "WRITE WITH NO RESPONSE")
            }
        }

        BasicText(text = "Response:")

        result.invoke().forEach {
            Text(text = Utility.bytesToHexString(it))
        }

        BasicText(text = "DESCRIPTORS")
        Divider(
            color = Color.Gray,
            thickness = 0.5.dp,
            modifier = Modifier.padding(vertical = 4.dp)
        )

    }
}

@Composable
private fun RowItem() {
    BasicText(text = "Device Address")
    BasicText(text = "D6:55....")
}




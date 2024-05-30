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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun DeviceOperationScreen(navController: NavController) {
    Log.d("DeviceOperationScreen", "DeviceOperation")

    Properties()

    BackHandler {
        navController.navigateUp()
    }

}

@Composable
fun Properties() {
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
        BasicText(text = "Device Address")
        BasicText(text = "D6:55....")
        Spacer(modifier = Modifier.height(16.dp))

        BasicText(text = "Service UUID")
        BasicText(text = "D6:55....")
        Spacer(modifier = Modifier.height(16.dp))

        BasicText(text = "Characteristic UUID")
        BasicText(text = "D6:55....")
        Spacer(modifier = Modifier.height(16.dp))

        BasicText(text = "Characteristic Name")
        BasicText(text = "D6:55....")
        Spacer(modifier = Modifier.height(16.dp))

        BasicText(text = "READ/INDICATED VALUES")

        Divider(
            color = Color.Gray,
            thickness = 0.5.dp,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        Row {
            Button(onClick = {}) {
                Text(text = "READ")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = {}) {
                Text(text = "SUBSCRIBE")
            }
        }

        BasicText(text = "Response:")

        BasicText(text = "DESCRIPTORS")
        Divider(
            color = Color.Gray,
            thickness = 0.5.dp,
            modifier = Modifier.padding(vertical = 4.dp)
        )

    }
}




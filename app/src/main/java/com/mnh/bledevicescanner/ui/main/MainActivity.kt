package com.mnh.bledevicescanner.ui.main

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.mnh.bledevicescanner.core.theme.AppTheme
import com.mnh.bledevicescanner.ui.navigation.Navigation
import com.napco.utils.PermissionManager.Companion.permissionManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupPermissions()
    }

    private fun setupPermissions() {
        permissionManager {
            context = this@MainActivity
            permissionsToRequest = arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_FINE_LOCATION
            )

            onPermissionGranted {
                setContent {
                    AppTheme {
                        Navigation()
                    }
                }
            }

            onPermissionDenied {
                showMessage()
            }

            doRequest()
        }
    }

    private fun showMessage() {
        toast()
    }

    private fun toast() {
        Toast.makeText(
            this@MainActivity,
            "Some required permissions are denied",
            Toast.LENGTH_SHORT
        ).show()
    }

}
package com.napco.utils

import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

class PermissionManager(var context: ComponentActivity?, var permissionsToRequest: Array<String>?) {
    constructor(builder: Builder) : this(builder.context, builder.permissionsToRequest)

    private var onPermissionGranted: (() -> Unit)? = null
    private var onPermissionDenied: (() -> Unit)? = null

    fun onPermissionGranted(action: () -> Unit) {
        onPermissionGranted = action
    }

    fun onPermissionDenied(action: () -> Unit) {
        onPermissionDenied = action
    }

    fun doRequest() {
        requestPermissions()
    }

    companion object {
        inline fun ComponentActivity.permissionManager(block: PermissionManager.() -> Unit) {
            val permissionManager = Builder(this).build()
            permissionManager.block()
        }
    }

    class Builder(component: ComponentActivity) {
        var context: ComponentActivity? = component
        var permissionsToRequest: Array<String>? = null
        fun build() = PermissionManager(this)
    }

    private val requestPermissionLauncher =
        context?.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissionsResult ->
            if (permissionsResult.all { it.value }) {
                onPermissionGranted?.invoke()
            } else {
                onPermissionDenied?.invoke()
            }
        }

    private fun requestPermissions() {
        val permissionsNotGranted = permissionsToRequest?.filter { permission ->
            ContextCompat.checkSelfPermission(
                context!!,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        }?.toTypedArray()

        if (permissionsNotGranted?.isNotEmpty() == true) {
            requestPermissionLauncher?.launch(permissionsNotGranted)
        } else {
            this.onPermissionGranted?.invoke()
        }
    }

}

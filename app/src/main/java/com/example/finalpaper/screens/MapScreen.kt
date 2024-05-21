package com.example.finalpaper.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.finalpaper.permissions.AccessFineLocationPermissionTextProvider
import com.example.finalpaper.permissions.CameraPermissionTextProvider
import com.example.finalpaper.permissions.PermissionDialog
import com.example.finalpaper.permissions.PermissionsViewModel
import com.example.finalpaper.permissions.RecordAudioPermissionTextProvider
import com.example.finalpaper.permissions.openAppSettings
import com.google.maps.android.compose.GoogleMap

@Composable
fun MapScreen() {
    val viewModel: PermissionsViewModel = viewModel()
    val dialogQueue = viewModel.visiblePermissionDialogQueue
    val context = LocalContext.current

    var isMapLoaded by remember { mutableStateOf(false) }

    val locationPermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.onPermissionResult(
            permission = Manifest.permission.ACCESS_FINE_LOCATION,
            isGranted = isGranted
        )
    }

    LaunchedEffect(key1 = Unit) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionResultLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    dialogQueue.reversed().forEach { permission ->
        PermissionDialog(
            permissionTextProvider = when (permission) {
                Manifest.permission.CAMERA -> CameraPermissionTextProvider()
                Manifest.permission.ACCESS_FINE_LOCATION -> AccessFineLocationPermissionTextProvider()
                Manifest.permission.RECORD_AUDIO -> RecordAudioPermissionTextProvider()
                else -> return@forEach
            },
            isPermanentlyDeclined = !ActivityCompat.shouldShowRequestPermissionRationale(
                context as ComponentActivity,
                permission
            ),
            onDismiss = viewModel::dismissDialog,
            onOkClick = {
                viewModel.dismissDialog()
                locationPermissionResultLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            },
            onGoToAppSettingsClick = { openAppSettings(context) }
        )
    }

    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                onMapLoaded = { isMapLoaded = true }
            )

        }
    }
}
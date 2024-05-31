package com.example.finalpaper.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.core.app.ActivityCompat

@Composable
fun HandleDialogs(
    dialogQueue: SnapshotStateList<String>,
    viewModel: PermissionsViewModel,
    context: Context,
    permissionResultLauncher: ManagedActivityResultLauncher<String, Boolean>? = null,
    multiplePermissionResultLauncher: ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>>? = null
) {
    dialogQueue.reversed().forEach { permission ->
        if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
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
                    permissionResultLauncher?.launch(permission)
                    multiplePermissionResultLauncher?.launch(arrayOf(permission))
                },
                onGoToAppSettingsClick = { openAppSettings(context) }
            )
        }
    }
}

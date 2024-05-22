package com.example.finalpaper.screens

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.finalpaper.DefaultLocationClient
import com.example.finalpaper.permissions.AccessFineLocationPermissionTextProvider
import com.example.finalpaper.permissions.CameraPermissionTextProvider
import com.example.finalpaper.permissions.PermissionDialog
import com.example.finalpaper.permissions.PermissionsViewModel
import com.example.finalpaper.permissions.RecordAudioPermissionTextProvider
import com.example.finalpaper.permissions.openAppSettings
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun MapScreen(navController: NavHostController) {
    val viewModel: PermissionsViewModel = viewModel()
    val dialogQueue = viewModel.visiblePermissionDialogQueue
    val context = LocalContext.current
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    var isMapLoaded by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(true) }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val locationClient = remember { DefaultLocationClient(context, fusedLocationClient) }

    val locationPermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.onPermissionResult(
            permission = Manifest.permission.ACCESS_FINE_LOCATION,
            isGranted = isGranted
        )
        if (isGranted) {
            startLocationUpdates(locationClient) { location ->
                currentLocation = LatLng(location.latitude, location.longitude)
                loading = false
            }
        }
    }

    LaunchedEffect(key1 = Unit) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionResultLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            startLocationUpdates(locationClient) { location ->
                currentLocation = LatLng(location.latitude, location.longitude)
                loading = false
            }
        }
    }
    LaunchedEffect(key1 = currentLocation) {
        if (currentLocation != null) {
            loading = false
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
            if (loading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Loading current location...")
                }
                startLocationUpdates(locationClient) { location ->
                    currentLocation = LatLng(location.latitude, location.longitude)
                    loading = false
                }
            } else if (currentLocation != null) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    onMapLoaded = { isMapLoaded = true },
                    cameraPositionState = rememberCameraPositionState {
                        position = CameraPosition.fromLatLngZoom(currentLocation!!, 15f)
                    }
                ) {
                    Marker(
                        state = MarkerState(position = currentLocation!!),
                        title = "Current Location"
                    )
                }
            }
        }
    }
}

fun startLocationUpdates(locationClient: DefaultLocationClient, onLocationReceived: (Location) -> Unit) {
    val scope = CoroutineScope(Dispatchers.Main)
    scope.launch {
        locationClient.getLocationUpdates(1000L).collect { location ->
            onLocationReceived(location)
        }
    }
}
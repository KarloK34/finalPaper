package com.example.finalpaper.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.finalpaper.MainActivity
import com.example.finalpaper.components.MapButtonsRow
import com.example.finalpaper.locationUtilities.DefaultLocationClient
import com.example.finalpaper.R
import com.example.finalpaper.voiceRecordingRoom.VoiceRecordingViewModel
import com.example.finalpaper.locationUtilities.AnimationQueue
import com.example.finalpaper.locationUtilities.bitmapFromVector
import com.example.finalpaper.locationUtilities.startLocationUpdates
import com.example.finalpaper.permissions.HandleDialogs
import com.example.finalpaper.permissions.PermissionsViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MapScreen(navController: NavHostController) {
    val viewModel: PermissionsViewModel = viewModel()
    val dialogQueue = viewModel.visiblePermissionDialogQueue
    val context = LocalContext.current

    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    var isMapLoaded by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(true) }
    var permissionsGranted by remember { mutableStateOf(false) }

    val dao = remember { MainActivity.DatabaseProvider.getDatabase(context).voiceRecordingDao() }
    val voiceRecordingViewModel = remember { VoiceRecordingViewModel(dao, context) }
    val state by voiceRecordingViewModel.state.collectAsState()
    val voiceRecordings = state.voiceRecordings

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val locationClient = remember { DefaultLocationClient(context, fusedLocationClient) }
    val permissionsToRequest = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.RECORD_AUDIO
    )

    val multiplePermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { perms ->
            permissionsToRequest.forEach { permission ->
                viewModel.onPermissionResult(
                    permission = permission,
                    isGranted = perms[permission] == true
                )
            }
            permissionsGranted = perms.values.all { it }
        }
    )
    LaunchedEffect(Unit) {
        if (permissionsToRequest.any {
                ActivityCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
            }) {
            multiplePermissionResultLauncher.launch(permissionsToRequest)
        } else {
            permissionsGranted = true
        }
    }

    if (permissionsGranted) {
        LaunchedEffect(Unit) {
            startLocationUpdates(locationClient) { location ->
                currentLocation = LatLng(location.latitude, location.longitude)
                loading = false
            }
        }
    }

    HandleDialogs(
        dialogQueue = dialogQueue,
        viewModel = viewModel,
        context = context,
        multiplePermissionResultLauncher = multiplePermissionResultLauncher
    )

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
                val coroutineScope = rememberCoroutineScope()
                val markerState = remember { MarkerState(position = currentLocation!!) }
                val animationQueue = remember { AnimationQueue(markerState, coroutineScope) }
                val cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(currentLocation!!, 16f)
                }

                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    onMapLoaded = { isMapLoaded = true },
                    cameraPositionState = cameraPositionState
                ) {
                    Marker(
                        state = markerState,
                        title = "Current Location",
                        icon = bitmapFromVector(context, R.drawable.baseline_circle_24)
                    )
                    voiceRecordings.forEach { recording ->
                        val recordingMarkerState = MarkerState(position = LatLng(recording.latitude, recording.longitude))
                        Marker(
                            state = recordingMarkerState,
                            title = "Click to hear Voice Recording",
                            snippet = "Recorded at ${getFormattedTime(recording.timestamp)}",
                            onInfoWindowClick = {
                                voiceRecordingViewModel.playAudio(recording.fileName)
                            }
                        )
                    }
                }
                FloatingActionButton(onClick = { cameraPositionState.position = CameraPosition.fromLatLngZoom(currentLocation!!, 16f) }) {
                    Icon(Icons.Default.LocationOn, contentDescription = "Current Location")
                }
                LaunchedEffect(Unit) {
                    locationClient.getLocationUpdates(3000L).collect { location ->
                        val newLatLng = LatLng(location.latitude, location.longitude)
                        animationQueue.addToQueue(newLatLng)
                    }
                }
                Column(modifier = Modifier.align(Alignment.BottomStart)) {
                    MapButtonsRow(
                        state = state,
                        voiceRecordingViewModel = voiceRecordingViewModel,
                        currentLocation = currentLocation,
                        context = context,
                        dao = dao
                    )
                }
            }
        }
    }
}

fun getFormattedTime(timeInMillis: Long): String {
    val date = Date(timeInMillis)
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return format.format(date)
}

fun startNavigationToLocation(context: Context, destination: LatLng) {
    val uri = "google.navigation:q=${destination.latitude},${destination.longitude}"
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
    intent.setPackage("com.google.android.apps.maps")
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    } else {
        Toast.makeText(context, "Google Maps app not installed", Toast.LENGTH_SHORT).show()
    }
}



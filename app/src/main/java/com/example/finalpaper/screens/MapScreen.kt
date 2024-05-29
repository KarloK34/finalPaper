package com.example.finalpaper.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.location.Location
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.finalpaper.MainActivity
import com.example.finalpaper.locationUtilities.DefaultLocationClient
import com.example.finalpaper.R
import com.example.finalpaper.voiceRecordingRoom.VoiceRecording
import com.example.finalpaper.voiceRecordingRoom.VoiceRecordingEvent
import com.example.finalpaper.voiceRecordingRoom.VoiceRecordingViewModel
import com.example.finalpaper.locationUtilities.AnimationQueue
import com.example.finalpaper.permissions.AccessFineLocationPermissionTextProvider
import com.example.finalpaper.permissions.CameraPermissionTextProvider
import com.example.finalpaper.permissions.PermissionDialog
import com.example.finalpaper.permissions.PermissionsViewModel
import com.example.finalpaper.permissions.RecordAudioPermissionTextProvider
import com.example.finalpaper.permissions.openAppSettings
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun MapScreen(navController: NavHostController) {
    val viewModel: PermissionsViewModel = viewModel()
    val dialogQueue = viewModel.visiblePermissionDialogQueue
    val context = LocalContext.current
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    var isMapLoaded by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(true) }

    val dao = remember { MainActivity.DatabaseProvider.getDatabase(context).voiceRecordingDao() }
    val voiceRecordingViewModel = remember { VoiceRecordingViewModel(dao, context) }
    val state by voiceRecordingViewModel.state.collectAsState()
    var nearbyRecordings by remember { mutableStateOf<List<VoiceRecording>>(emptyList()) }

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
                val coroutineScope = rememberCoroutineScope()
                val markerState = remember { MarkerState(position = currentLocation!!) }
                val animationQueue = remember { AnimationQueue(markerState, coroutineScope) }

                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    onMapLoaded = { isMapLoaded = true },
                    cameraPositionState = rememberCameraPositionState {
                        position = CameraPosition.fromLatLngZoom(currentLocation!!, 16f)
                    }
                ) {
                    Marker(
                        state = markerState,
                        title = "Current Location",
                        icon = bitmapFromVector(context, R.drawable.baseline_circle_24)
                    )
                }
                LaunchedEffect(Unit) {
                    locationClient.getLocationUpdates(2000L).collect { location ->
                        val newLatLng = LatLng(location.latitude, location.longitude)
                        animationQueue.addToQueue(newLatLng)
                    }
                }
                FloatingActionButton(
                    onClick = {
                        if (state.isAddingVoiceRecording) {
                            voiceRecordingViewModel.stopRecording()
                            voiceRecordingViewModel.onEvent(
                                VoiceRecordingEvent.SetLatitude(
                                    currentLocation!!.latitude
                                )
                            )
                            voiceRecordingViewModel.onEvent(
                                VoiceRecordingEvent.SetLongitude(
                                    currentLocation!!.longitude
                                )
                            )
                            voiceRecordingViewModel.onEvent(VoiceRecordingEvent.SetTimestamp(System.currentTimeMillis()))
                            voiceRecordingViewModel.onEvent(VoiceRecordingEvent.SaveVoiceRecording)
                        } else {
                            voiceRecordingViewModel.startRecording(context)
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                ) {
                    Icon(
                        imageVector = if (state.isAddingVoiceRecording) Icons.Default.Close else Icons.Default.Add,
                        contentDescription = "Add voice recording"
                    )
                }
                LaunchedEffect(key1 = currentLocation) {
                    if (currentLocation != null) {
                        loading = false
                        val latMin = currentLocation!!.latitude - 0.01
                        val latMax = currentLocation!!.latitude + 0.01
                        val lngMin = currentLocation!!.longitude - 0.01
                        val lngMax = currentLocation!!.longitude + 0.01
                        val recordings = withContext(Dispatchers.IO) {
                            dao.getMessagesNearLocation(latMin, latMax, lngMin, lngMax)
                        }
                        nearbyRecordings = recordings
                    }
                }
                if (nearbyRecordings.isNotEmpty()) {
                    FloatingActionButton(
                        onClick = {
                            voiceRecordingViewModel.playAudio(nearbyRecordings.last().fileName)
                        }, modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                            .offset(y = (-72).dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "Hear voice recording"
                        )
                    }
                }
            }
        }
    }
}

private fun startLocationUpdates(
    locationClient: DefaultLocationClient,
    onLocationReceived: (Location) -> Unit
) {
    val scope = CoroutineScope(Dispatchers.Main)
    scope.launch {
        locationClient.getLocationUpdates(2000L).collect { location ->
            onLocationReceived(location)
        }
    }
}

private fun bitmapFromVector(context: Context, vectorResId: Int): BitmapDescriptor {
    val vectorDrawable: Drawable = ContextCompat.getDrawable(context, vectorResId)!!
    vectorDrawable.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)

    val bitmap: Bitmap =
        Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )

    val canvas = Canvas(bitmap)
    vectorDrawable.draw(canvas)

    return BitmapDescriptorFactory.fromBitmap(bitmap)
}



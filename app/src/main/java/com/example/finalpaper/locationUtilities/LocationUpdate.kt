package com.example.finalpaper.locationUtilities

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

data class LocationUpdate(val latLng: LatLng, val timeSinceLastUpdate: Long)

fun startLocationUpdates(
    locationClient: DefaultLocationClient,
    onLocationReceived: (Location) -> Unit
) {
    val scope = CoroutineScope(Dispatchers.Main)
    scope.launch {
        locationClient.getLocationUpdates(3000L).collect { location ->
            onLocationReceived(location)
        }
    }
}

package com.example.finalpaper.locationUtilities

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

data class LocationUpdate(val latLng: LatLng, val timeSinceLastUpdate: Long)

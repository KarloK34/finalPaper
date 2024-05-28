package com.example.finalpaper.screens

import com.google.android.gms.maps.model.LatLng

data class LocationUpdate(val latLng: LatLng, val timeSinceLastUpdate: Long)
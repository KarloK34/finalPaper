package com.example.finalpaper

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Tasks
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PlaceLikelihood
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class POIRepository(context: Context) {
    private val placesClient: PlacesClient = PlacesClientProvider.getClient(context)

    @SuppressLint("MissingPermission")
    suspend fun getPOIs(location: LatLng, radius: Int): List<PlaceLikelihood> {
        return withContext(Dispatchers.IO) {
            val placeFields = listOf(Place.Field.NAME, Place.Field.LAT_LNG)
            val request = FindCurrentPlaceRequest.newInstance(placeFields)

            try {
                val response = Tasks.await(placesClient.findCurrentPlace(request))
                response.placeLikelihoods.filter {
                    it.place.latLng?.let { latLng ->
                        val distance = FloatArray(1)
                        Location.distanceBetween(
                            location.latitude, location.longitude,
                            latLng.latitude, latLng.longitude, distance
                        )
                        distance[0] <= radius
                    } ?: false
                }
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
}
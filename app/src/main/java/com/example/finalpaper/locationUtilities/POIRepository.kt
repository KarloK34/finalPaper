package com.example.finalpaper.locationUtilities

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.example.finalpaper.retrofit.PlaceResult
import com.example.finalpaper.R
import com.example.finalpaper.retrofit.placesApiService
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Tasks
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PlaceLikelihood
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class POIRepository(context: Context) {
    private val apiKey: String = context.getString(R.string.google_maps_key)
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
    suspend fun getPOIsOfCategory(location: LatLng, radius: Int, category: String): List<PlaceResult> {
        return try {
            val locationStr = "${location.latitude},${location.longitude}"
            val response = placesApiService.getNearbyPlaces(locationStr, radius, mapCategoryToPlaceType(category), apiKey)
            response.results.take(3)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private fun mapCategoryToPlaceType(category: String): String {
        return when (category) {
            "RESTAURANT" -> "restaurant"
            "CAFE'S" -> "cafe"
            "SCHOOL" -> "school"
            else -> "point_of_interest"
        }
    }
}
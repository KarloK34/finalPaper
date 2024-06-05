package com.example.finalpaper.locationUtilities

import android.content.Context
import com.example.finalpaper.R
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient

object PlacesClientProvider {
    private var placesClient: PlacesClient? = null

    fun getClient(context: Context): PlacesClient {
        if (placesClient == null) {
            synchronized(this) {
                if (placesClient == null) {
                    Places.initialize(context.applicationContext, context.getString(R.string.google_maps_key))
                    placesClient = Places.createClient(context.applicationContext)
                }
            }
        }
        return placesClient!!
    }
}
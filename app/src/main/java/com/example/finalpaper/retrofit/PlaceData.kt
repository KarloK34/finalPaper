package com.example.finalpaper.retrofit

import com.google.gson.annotations.SerializedName

data class PlacesResponse(
    @SerializedName("results") val results: List<PlaceResult>
)

data class PlaceResult(
    @SerializedName("name") val name: String,
    @SerializedName("geometry") val geometry: Geometry
)

data class Geometry(
    @SerializedName("location") val location: LatLngLiteral
)

data class LatLngLiteral(
    @SerializedName("lat") val lat: Double,
    @SerializedName("lng") val lng: Double
)
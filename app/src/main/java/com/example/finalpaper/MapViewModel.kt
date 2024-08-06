package com.example.finalpaper

import androidx.lifecycle.ViewModel
import com.example.finalpaper.retrofit.PlaceResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MapViewModel : ViewModel() {
    private val _pois = MutableStateFlow<List<PlaceResult>>(emptyList())
    val pois: StateFlow<List<PlaceResult>> get() = _pois

    fun updatePOIs(newPOIs: List<PlaceResult>) {
        _pois.value = newPOIs
    }
}
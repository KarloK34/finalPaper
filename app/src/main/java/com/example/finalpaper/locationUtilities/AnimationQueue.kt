package com.example.finalpaper.locationUtilities

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MarkerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class AnimationQueue(private val markerState: MarkerState, private val scope: CoroutineScope) {
    private var timeSinceLastUpdate = System.currentTimeMillis()
    private val items = mutableListOf<LocationUpdate>()
    private val mutex = Mutex()

    fun addToQueue(latLng: LatLng) {
        items.add(
            LocationUpdate(
                latLng = latLng,
                timeSinceLastUpdate = System.currentTimeMillis() - timeSinceLastUpdate
            )
        )

        timeSinceLastUpdate = System.currentTimeMillis()

        scope.launch {
            runNextItem()
        }
    }

    private suspend fun runNextItem() {
        mutex.withLock {
            while (items.isNotEmpty()) {
                items.removeAt(0).let {
                    markerState.animateToPosition(
                        endPosition = it.latLng,
                        duration = it.timeSinceLastUpdate,
                    )
                }
            }
        }
    }
}
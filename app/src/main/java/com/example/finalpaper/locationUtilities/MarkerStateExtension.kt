package com.example.finalpaper.locationUtilities

import android.animation.ValueAnimator
import androidx.core.animation.doOnCancel
import androidx.core.animation.doOnEnd
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MarkerState
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

suspend fun MarkerState.animateToPosition(endPosition: LatLng, duration: Long) {
    val startPosition = position

    suspendCoroutine { continuation ->
        ValueAnimator.ofFloat(0f, 1f).apply {
            this.duration = duration
            interpolator = null

            addUpdateListener { animation ->
                position = LatLngInterpolator.Linear.interpolate(
                    animation.animatedFraction,
                    startPosition,
                    endPosition
                )
            }

            doOnEnd {
                continuation.resume(Unit)
            }

            doOnCancel {
                continuation.resume(Unit)
            }

            start()
        }
    }
}
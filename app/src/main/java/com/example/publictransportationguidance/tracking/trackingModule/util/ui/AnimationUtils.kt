package com.example.publictransportationguidance.tracking.trackingModule.util.ui

import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator

object AnimationUtils {

    /* M Osama: animator for blackLine over greyLine */
    fun polylineAnimator(): ValueAnimator {
        val valueAnimator = ValueAnimator.ofInt(0, 100)
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.duration = 4000
        return valueAnimator
    }

    /* M Osama: animator for car movement */
    fun carAnimator(): ValueAnimator {
        val valueAnimator = ValueAnimator.ofFloat(0f, 1f)
        valueAnimator.duration = 3000
        valueAnimator.interpolator = LinearInterpolator()
        return valueAnimator
    }

}
package com.onfido.android.techtask.util

import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator

/**
 * Created by gokhan.alici on 03.03.2019
 */

const val SLOWLY = 1500L
const val QUICKLY = 500L

fun View.appear(duration: Long = QUICKLY) {
    alpha = 0f
    visibility = View.VISIBLE

    animate()
        .alpha(1f)
        .setInterpolator(AccelerateDecelerateInterpolator())
        .setDuration(duration)
        .setListener(null)
}

fun View.disappear(duration: Long = QUICKLY) {
    alpha = 1f
    animate()
        .setInterpolator(AccelerateDecelerateInterpolator())
        .alpha(0f)
        .setDuration(duration)
        .withEndAction {
            visibility = View.GONE
        }
}

fun View.scaleDown(duration: Long = QUICKLY) {
    animate()
        .scaleX(0f)
        .scaleY(0f)
        .setDuration(duration)
        .setInterpolator(AccelerateInterpolator())
        .withEndAction {
            animate()
                .setDuration(0)
                .scaleX(1f)
                .scaleY(1f)
                .start()
        }
}
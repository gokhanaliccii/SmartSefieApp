package com.onfido.android.techtask.widget.camera.util

import android.graphics.Bitmap
import android.graphics.Matrix

/**
 * Created by gokhan.alici on 03.03.2019
 */

internal const val START_X = 0
internal const val START_Y = 0

internal fun modifyBitmap(
    rawBitmap: Bitmap,
    scaleFactor: Float,
    rotationDegree: Float = 0f,
    mirrorBitmap: Boolean = false
): Bitmap {
    val matrix = Matrix()
    matrix.postScale(scaleFactor, scaleFactor)

    if (rotationDegree != 0f) {
        matrix.postRotate(rotationDegree)
    }

    if (mirrorBitmap) {
        matrix.preScale(1f, -1f)
    }

    val resizedBitmap = Bitmap.createBitmap(
        rawBitmap, START_X, START_Y, rawBitmap.width, rawBitmap.height, matrix, false
    )
    rawBitmap.recycle()

    return resizedBitmap
}
package com.onfido.android.techtask.widget.camera

import android.support.annotation.IntDef
import com.onfido.android.techtask.widget.camera.CameraPreviewQuality.Companion.HIGH
import com.onfido.android.techtask.widget.camera.CameraPreviewQuality.Companion.LOW

/**
 * Created by gokhan.alici on 03.03.2019
 */

@Retention(AnnotationRetention.SOURCE)
@IntDef(LOW, HIGH)
annotation class CameraPreviewQuality {

    companion object {
        const val LOW = 0
        const val HIGH = 1
    }
}
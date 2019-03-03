package com.onfido.android.techtask.widget.camera

import android.support.annotation.IntDef
import com.onfido.android.techtask.widget.camera.CameraPosition.Companion.BACK
import com.onfido.android.techtask.widget.camera.CameraPosition.Companion.FRONT

/**
 * Created by gokhan.alici on 03.03.2019
 */

@Retention(AnnotationRetention.SOURCE)
@IntDef(FRONT, BACK)
annotation class CameraPosition {

    companion object {
        const val FRONT = 0
        const val BACK = 1
    }
}
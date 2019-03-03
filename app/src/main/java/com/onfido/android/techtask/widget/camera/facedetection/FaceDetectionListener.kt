package com.onfido.android.techtask.widget.camera.facedetection

/**
 * Created by gokhan.alici on 03.03.2019
 */
interface FaceDetectionListener {

    fun onFaceDetected(faceBounds: FaceBound)
}
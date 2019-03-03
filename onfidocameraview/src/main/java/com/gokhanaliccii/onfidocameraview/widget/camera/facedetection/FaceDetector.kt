package com.onfido.android.techtask.widget.camera.facedetection

import com.gokhanaliccii.onfidocameraview.frame.FrameProcessor

/**
 * Created by gokhan.alici on 03.03.2019
 */
interface FaceDetector : FrameProcessor {

    fun start()

    fun stop()

    fun faceDetectionListener(faceDetectionListener: FaceDetectionListener)
}
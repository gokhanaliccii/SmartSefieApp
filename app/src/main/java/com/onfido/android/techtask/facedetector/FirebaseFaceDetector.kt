package com.onfido.android.techtask.facedetector

import com.onfido.android.techtask.widget.camera.facedetection.FaceDetectionListener
import com.onfido.android.techtask.widget.camera.facedetection.FaceDetector
import com.onfido.camera.frame.FrameInfo

/**
 * Created by gokhan.alici on 03.03.2019
 */
class FirebaseFaceDetector : FaceDetector {

    private var detectionEnabled = false
    private val faceInfoConsumer = FaceInfoProcessor()
    private var faceDetectionListener: FaceDetectionListener? = null

    override fun start() {
        detectionEnabled = true

        if (faceDetectionListener != null) { // re subscribe to last
            faceInfoConsumer.notifyOnFaceDetection(faceDetectionListener!!)
        }
    }

    override fun stop() {
        detectionEnabled = false
        faceInfoConsumer.stop()
    }

    override fun faceDetectionListener(faceDetectionListener: FaceDetectionListener) {
        this.faceDetectionListener = faceDetectionListener
        faceInfoConsumer.notifyOnFaceDetection(faceDetectionListener)
    }

    override fun processFrame(frameInfo: FrameInfo) {
        if (!detectionEnabled) { // Ignore frame until detection enabled!
            return
        }

        faceInfoConsumer.processFrame(frameInfo)
    }
}
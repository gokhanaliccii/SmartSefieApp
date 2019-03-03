package com.onfido.android.techtask.facedetection

import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions.NO_CLASSIFICATIONS
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions.NO_LANDMARKS

/**
 * Created by gokhan.alici on 03.03.2019
 */
object FirebaseFaceDetectorFacade {

    fun buildFaceDetector(): FirebaseVisionFaceDetector {
        val faceDetectorOptions = FirebaseVisionFaceDetectorOptions.Builder()
            .setPerformanceMode(FirebaseVisionFaceDetectorOptions.FAST)
            .setLandmarkMode(NO_LANDMARKS)
            .setClassificationMode(NO_CLASSIFICATIONS)
            .setMinFaceSize(0.15f)
            .enableTracking()
            .build()

        return FirebaseVision.getInstance().getVisionFaceDetector(faceDetectorOptions)
    }
}
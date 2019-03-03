package com.onfido.android.techtask.facedetection

import android.graphics.Rect
import android.util.Log
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.onfido.android.techtask.widget.camera.facedetection.FaceBound
import com.onfido.android.techtask.widget.camera.facedetection.FaceDetectionListener
import com.onfido.android.techtask.widget.camera.facedetection.FaceDetector
import com.onfido.camera.frame.FrameInfo

/**
 * Created by gokhan.alici on 03.03.2019
 */
class FirebaseFaceDetector : FaceDetector {

    private companion object {
        const val TAG = "FirebaseFaceDetector"
    }

    private var detectionEnabled = false
    private var detectionStartedAlready = false
    private var faceDetectionListener: FaceDetectionListener? = null
    private val firebaseFaceDetector by lazy {
        FirebaseFaceDetectorFacade.buildFaceDetector()
    }

    override fun start() {
        detectionEnabled = true
    }

    override fun stop() {
        detectionEnabled = false
    }

    override fun faceDetectionListener(faceDetectionListener: FaceDetectionListener) {
        this.faceDetectionListener = faceDetectionListener
    }

    override fun processFrame(frameInfo: FrameInfo) {
        if (!detectionEnabled) { // Ignore frame until detection enabled!
            return
        }

        firebaseFaceDetector.detectInImage(convertFrameInfoToVisionImage(frameInfo))
            .addOnCompleteListener {
                detectionStartedAlready = false
            }
            .addOnSuccessListener { faceVisions ->
                if (faceVisions.size > 0) {
                    notify(faceVisions)
                }
            }.addOnFailureListener {
                detectionStartedAlready = false
                Log.d(TAG, "exception on FirebaseDetection")
            }
    }

    private fun notify(faceVisions: List<FirebaseVisionFace>) {
        faceDetectionListener?.let {
            it.onFaceDetected(toFaceBound(faceVisions))
        }
    }

    private fun toFaceBound(faceVisions: List<FirebaseVisionFace>): List<FaceBound> {
        return faceVisions.map { vision ->
            vision.boundingBox.toFaceBound()
        }
    }

    private fun convertFrameInfoToVisionImage(frame: FrameInfo): FirebaseVisionImage {
        return FirebaseVisionImage.fromByteArray(
            frame.byteArray,
            buildVisionMetaData(frame)
        )
    }

    private fun buildVisionMetaData(frame: FrameInfo): FirebaseVisionImageMetadata {
        return FirebaseVisionImageMetadata.Builder()
            .setWidth(frame.width)
            .setHeight(frame.height)
            .setRotation(findFirebaseRotation(frame.rotation))
            .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
            .build()
    }

    private fun findFirebaseRotation(rotationCompensation: Int): Int {
        return when (rotationCompensation) {
            0 -> FirebaseVisionImageMetadata.ROTATION_0
            90 -> FirebaseVisionImageMetadata.ROTATION_90
            180 -> FirebaseVisionImageMetadata.ROTATION_180
            270 -> FirebaseVisionImageMetadata.ROTATION_270
            else -> FirebaseVisionImageMetadata.ROTATION_0
        }
    }

    private fun mapToFaceBound(faceRect: Rect): FaceBound =
        FaceBound(faceRect.left, faceRect.top, faceRect.right, faceRect.bottom)

    private fun Rect.toFaceBound(): FaceBound =
        mapToFaceBound(this)
}
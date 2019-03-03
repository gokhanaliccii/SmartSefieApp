package com.onfido.android.techtask.facedetector

import android.graphics.Rect
import android.util.Log
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.onfido.android.techtask.facedetector.VisionImageObservable.createObservable
import com.onfido.android.techtask.widget.camera.facedetection.FaceBound
import com.onfido.android.techtask.widget.camera.facedetection.FaceDetectionListener
import com.onfido.camera.frame.FrameInfo
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

/**
 * Created by gokhan.alici on 03.03.2019
 */
class FaceInfoProcessor {

    private companion object {
        const val TAG = "FaceInfoProcessor"
        val emptyFaceBoundList = listOf<FaceBound>()
    }

    private val frameSubject = PublishSubject.create<FrameInfo>()
    private var disposable: Disposable? = null

    fun processFrame(frameInfo: FrameInfo) {
        frameSubject.onNext(frameInfo)
    }

    fun notifyOnFaceDetection(faceDetectionListener: FaceDetectionListener) {
        disposable = frameSubject
            .subscribeOn(Schedulers.computation())
            .map {
                convertFrameInfoToVisionImage(it)
            }.switchMap {
                createObservable(it)
            }.map {
                toFaceBound(it)
            }
            .filter {
                it.isNotEmpty()
            }
            .onErrorReturnItem(emptyFaceBoundList)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d(TAG, "success")
                faceDetectionListener.onFaceDetected(it)
            }, {
                Log.d(TAG, "error")
            }, {
                Log.d(TAG, "complete")
            })
    }

    fun stop() {
        disposable?.dispose()
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

    private fun Rect.toFaceBound(): FaceBound =
        mapToFaceBound(this)

    private fun mapToFaceBound(faceRect: Rect): FaceBound =
        FaceBound(faceRect.left, faceRect.top, faceRect.right, faceRect.bottom)

    private fun toFaceBound(faceVisions: List<FirebaseVisionFace>): List<FaceBound> {
        return faceVisions.map { vision ->
            vision.boundingBox.toFaceBound()
        }
    }
}
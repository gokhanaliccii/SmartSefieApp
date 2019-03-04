package com.onfido.android.techtask.facedetector

import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import io.reactivex.Observable

/**
 * Created by gokhan.alici on 03.03.2019
 */

object VisionImageObservable {

    private const val TAG = "VisionImageObservable"
    private var hasOngoingProcess = false

    private val firebaseFaceDetector by lazy {
        FirebaseFaceDetectorFacade.buildFaceDetector()
    }

    // If hasOngoingProcess it ignores incoming frame
    fun createObservable(visionImage: FirebaseVisionImage): Observable<List<FirebaseVisionFace>> {
        return Observable.create<List<FirebaseVisionFace>> { emitter ->
            if (!emitter.isDisposed) {
                if (hasOngoingProcess) {
                    emitter.onComplete()
                    return@create
                }

                hasOngoingProcess = true

                firebaseFaceDetector.detectInImage(visionImage)
                    .addOnSuccessListener { faceVisions ->
                        hasOngoingProcess = false
                        emitter.onNext(faceVisions)
                        emitter.onComplete()
                    }.addOnFailureListener {
                        hasOngoingProcess = false
                        emitter.onError(it)
                    }
            }
        }
    }
}
package com.onfido.android.techtask

import android.Manifest
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.onfido.android.techtask.widget.camera.OnfidoCameraView
import com.onfido.android.techtask.widget.camera.frame.FrameInfo
import com.onfido.android.techtask.widget.camera.frame.FrameProcessor

class CameraActivity : AppCompatActivity() {

    companion object {
        const val CAMERA_PERMISSION_REQUEST = 1
        const val TAG = "CameraActivity"
    }

    private lateinit var cameraView: OnfidoCameraView
    private lateinit var button: Button
    private lateinit var imagePreview: ImageView
    private var detection = false

    private val firebaseDetectionOptions by lazy { createFirebaseOptions() }
    private val firebaseFaceDetector by lazy { createFaceVisionDetector() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        cameraView = findViewById(R.id.camera_view)
        button = findViewById(R.id.action_take_picture)
        imagePreview = findViewById(R.id.imageview_picture_preview)

        button.setOnClickListener {
            cameraView.takePicture {
                Log.d(TAG, "picture received! ${it.width} ${it.height}")
                imagePreview.setImageBitmap(it)
            }
        }


        cameraView.addFrameProcessor(object : FrameProcessor {
            override fun processFrame(frameMetaData: FrameInfo) {
                detect(convertFrameToVisionImage2(frameMetaData))
            }
        })
    }

    private fun detect(
        visionImage: FirebaseVisionImage
    ) {

        firebaseFaceDetector.detectInImage(visionImage)
            .addOnCompleteListener {
                detection = false
            }
            .addOnSuccessListener {
                if (it.size > 0) {
                    Log.d(TAG, "onSuccess on FirebaseDetection")
                }
            }.addOnFailureListener {
                detection = false
                Log.d(TAG, "exception on FirebaseDetection")
            }
    }


    private fun createFaceVisionDetector() =
        FirebaseVision.getInstance().getVisionFaceDetector(firebaseDetectionOptions)

    private fun createFirebaseOptions(): FirebaseVisionFaceDetectorOptions {
        return FirebaseVisionFaceDetectorOptions.Builder()
            .setPerformanceMode(FirebaseVisionFaceDetectorOptions.FAST)
            .setLandmarkMode(
                FirebaseVisionFaceDetectorOptions.NO_LANDMARKS
            )
            .setClassificationMode(
                FirebaseVisionFaceDetectorOptions.NO_CLASSIFICATIONS
            )
            .setMinFaceSize(0.15f)
            .enableTracking()
            .build()
    }

    private fun convertFrameToVisionImage2(frame: FrameInfo): FirebaseVisionImage {
        return FirebaseVisionImage.fromByteArray(
            frame.byteArray,
            FirebaseVisionImageMetadata.Builder()
                .setRotation(getFirebaseRotation(frame.rotation))
                .setWidth(frame.width)   // 480x360 is typically sufficient for
                .setHeight(frame.height)  // image recognition
                .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
                .build()
        )
    }

    private fun getFirebaseRotation(rotationCompensation: Int): Int {
        return when (rotationCompensation) {
            0 -> FirebaseVisionImageMetadata.ROTATION_0
            90 -> FirebaseVisionImageMetadata.ROTATION_90
            180 -> FirebaseVisionImageMetadata.ROTATION_180
            270 -> FirebaseVisionImageMetadata.ROTATION_270
            else -> {
                FirebaseVisionImageMetadata.ROTATION_0
            }
        }
    }

    override fun onStart() {
        super.onStart()

        if (hasCameraPermission()) {
            cameraView.start()
        } else {
            requestForCameraPermission()
        }
    }

    override fun onStop() {
        super.onStop()

        if (hasCameraPermission()) {
            cameraView.stop()
        }
    }

    private fun cameraPermissionPermitted() {
        cameraView.start()
    }

    private fun hasCameraPermission(): Boolean =
        ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PERMISSION_GRANTED

    private fun requestForCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // currently we had only camera permission
        if (CAMERA_PERMISSION_REQUEST == requestCode && grantResults.isNotEmpty()) {
            cameraPermissionPermitted()
        }
    }
}
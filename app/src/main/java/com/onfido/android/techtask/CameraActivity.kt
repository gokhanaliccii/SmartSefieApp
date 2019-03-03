package com.onfido.android.techtask

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.os.Vibrator
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import com.onfido.camera.OnfidoCameraView
import com.onfido.android.techtask.facedetector.FirebaseFaceDetector
import com.onfido.android.techtask.util.QUICKLY
import com.onfido.android.techtask.util.SLOWLY
import com.onfido.android.techtask.util.appear
import com.onfido.android.techtask.util.disappear
import com.onfido.android.techtask.util.scaleDown
import com.onfido.android.techtask.util.vibrateWith
import com.onfido.android.techtask.widget.StatefulView
import com.onfido.android.techtask.widget.camera.facedetection.FaceBound
import com.onfido.android.techtask.widget.camera.facedetection.FaceDetectionListener

class CameraActivity : AppCompatActivity() {

    companion object {
        const val CAMERA_PERMISSION_REQUEST = 1
        const val TAG = "CameraActivity"
        const val STATE_CAMERA_VIEW = "camera_view"
        const val STATE_CAMERA_RESULT = "camera_result"
    }

    private lateinit var cameraView: OnfidoCameraView
    private lateinit var takePicture: Button
    private lateinit var capturedPicture: ImageView
    private lateinit var capturedPictureFrame: View
    private lateinit var dismissCapturedView: View
    private lateinit var statefulView: StatefulView
    private val vibrator by lazy { applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator }

    private lateinit var firebaseFaceDetector: FirebaseFaceDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        cameraView = findViewById(R.id.camera_view)
        takePicture = findViewById(R.id.action_take_picture)

        statefulView = findViewById(R.id.stateful_view)
        statefulView.viewReadyCallback {
            val pictureResult = statefulView.getView<View>(STATE_CAMERA_RESULT)

            capturedPicture = pictureResult.findViewById(R.id.imageview_picture_preview)
            capturedPictureFrame = pictureResult.findViewById(R.id.frame_preview)
            dismissCapturedView = pictureResult.findViewById(R.id.dismiss_picture)

            takePicture.setOnClickListener {
                vibrateWith(vibrator)
                capturePicture()
            }

            dismissCapturedView.setOnClickListener {
                statefulView.onBackPressed()
                returnToPreviewScreen()
            }

            firebaseFaceDetector = FirebaseFaceDetector()
            firebaseFaceDetector.faceDetectionListener(object :
                FaceDetectionListener {
                override fun onFaceDetected(faceBounds: List<FaceBound>) {
                    if (STATE_CAMERA_VIEW == statefulView.latestState()) {
                        vibrateWith(vibrator)
                        capturePicture()
                    }
                }
            })

            cameraView.addFrameProcessor(firebaseFaceDetector)
            firebaseFaceDetector.start()
        }
    }

    private fun returnToPreviewScreen() {
        capturedPictureFrame.disappear(QUICKLY)
        takePicture.appear(QUICKLY)
        capturedPicture.scaleDown()
    }

    private fun capturePicture() {
        statefulView.changeState(STATE_CAMERA_RESULT)
        capturedPictureFrame.appear(SLOWLY)
        takePicture.disappear(SLOWLY)

        cameraView.takePicture {
            capturedPicture.setImageBitmap(it)
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

    override fun onBackPressed() {
        val backConsumed = statefulView.onBackPressed()
        if (backConsumed) {
            if (STATE_CAMERA_VIEW == statefulView.latestState()) {
                returnToPreviewScreen()
            }
        } else {
            super.onBackPressed()
        }
    }
}
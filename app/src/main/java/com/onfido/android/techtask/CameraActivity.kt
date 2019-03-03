package com.onfido.android.techtask

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.os.Vibrator
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import com.onfido.android.techtask.facedetector.FirebaseFaceDetector
import com.onfido.android.techtask.util.QUICKLY
import com.onfido.android.techtask.util.SLOWLY
import com.onfido.android.techtask.util.appear
import com.onfido.android.techtask.util.bind
import com.onfido.android.techtask.util.disappear
import com.onfido.android.techtask.util.scaleDown
import com.onfido.android.techtask.util.showCameraPermissonDeniedDialog
import com.onfido.android.techtask.util.vibrateWith
import com.onfido.android.techtask.widget.StatefulView
import com.onfido.android.techtask.widget.camera.facedetection.FaceBound
import com.onfido.android.techtask.widget.camera.facedetection.FaceDetectionListener
import com.onfido.camera.OnfidoCameraView

class CameraActivity : AppCompatActivity() {

    private companion object {
        const val CAMERA_PERMISSION_REQUEST = 1
        const val STATE_CAMERA_VIEW = "camera_view"
        const val STATE_CAMERA_RESULT = "camera_result"
    }

    private val takePicture by bind<Button>(R.id.action_take_picture)
    private val statefulView by bind<StatefulView>(R.id.stateful_view)
    private val cameraView by bind<OnfidoCameraView>(R.id.camera_view)
    private lateinit var capturedFrame: View
    private lateinit var capturedPicture: ImageView
    private lateinit var dismissCapturedPicture: View

    private val faceDetector: FirebaseFaceDetector by lazy { FirebaseFaceDetector() }
    private val vibrator by lazy { applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_camera)

        statefulView.viewReadyCallback {
            val pictureResult = statefulView.getView<View>(STATE_CAMERA_RESULT)
            capturedPicture = pictureResult.findViewById(R.id.image_picture_preview)
            capturedFrame = pictureResult.findViewById(R.id.frame_preview)
            dismissCapturedPicture = pictureResult.findViewById(R.id.dismiss_picture)

            takePicture.setOnClickListener {
                vibrateWith(vibrator)
                capturePicture()
            }

            dismissCapturedPicture.setOnClickListener {
                statefulView.onBackPressed()
                returnToPreviewScreen()
            }

            faceDetector.faceDetectionListener(object :
                FaceDetectionListener {
                override fun onFaceDetected(faceBounds: List<FaceBound>) {
                    if (STATE_CAMERA_VIEW == statefulView.latestState()) {
                        vibrateWith(vibrator)
                        capturePicture()
                    }
                }
            })

            cameraView.addFrameProcessor(faceDetector)
            faceDetector.start()
        }
    }

    private fun returnToPreviewScreen() {
        capturedFrame.disappear(QUICKLY)
        takePicture.appear(QUICKLY)
        capturedPicture.scaleDown()
    }

    private fun capturePicture() {
        statefulView.changeState(STATE_CAMERA_RESULT)
        capturedFrame.appear(SLOWLY)
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
        cameraView.stop()
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
        } else {
            showCameraPermissonDeniedDialog(this)
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
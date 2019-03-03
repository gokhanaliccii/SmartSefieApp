package com.onfido.android.techtask

import android.Manifest
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import com.onfido.android.techtask.facedetection.FirebaseFaceDetector
import com.onfido.android.techtask.util.QUICKLY
import com.onfido.android.techtask.util.SLOWLY
import com.onfido.android.techtask.util.appear
import com.onfido.android.techtask.util.disappear
import com.onfido.android.techtask.util.scaleDown
import com.onfido.android.techtask.widget.camera.OnfidoCameraView
import com.onfido.android.techtask.widget.camera.facedetection.FaceBound
import com.onfido.android.techtask.widget.camera.facedetection.FaceDetectionListener

class CameraActivity : AppCompatActivity() {

    companion object {
        const val CAMERA_PERMISSION_REQUEST = 1
        const val TAG = "CameraActivity"
    }

    private lateinit var cameraView: OnfidoCameraView
    private lateinit var button: Button
    private lateinit var imagePreview: ImageView
    private lateinit var frame: View
    private lateinit var dissmiss: View

    private lateinit var firebaseFaceDetector: FirebaseFaceDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        cameraView = findViewById(R.id.camera_view)
        button = findViewById(R.id.action_take_picture)
        imagePreview = findViewById(R.id.imageview_picture_preview)
        frame = findViewById(R.id.frame_preview)
        dissmiss = findViewById(R.id.dismiss_picture)

        button.setOnClickListener {
            frame.appear(SLOWLY)
            button.disappear(SLOWLY)

            cameraView.takePicture {
                imagePreview.setImageBitmap(it)
            }
        }

        dissmiss.setOnClickListener {
            frame.disappear(QUICKLY)
            button.appear(QUICKLY)
            imagePreview.scaleDown()
        }

        firebaseFaceDetector = FirebaseFaceDetector()
        firebaseFaceDetector.faceDetectionListener(object :
            FaceDetectionListener {
            override fun onFaceDetected(faceBounds: List<FaceBound>) {
                Log.d(TAG, "faces detected ${faceBounds.size}")
            }
        })

        cameraView.addFrameProcessor(firebaseFaceDetector)
        firebaseFaceDetector.start()
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
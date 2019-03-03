package com.onfido.android.techtask

import android.Manifest
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import com.onfido.android.techtask.widget.camera.OnfidoCameraView

class CameraActivity : AppCompatActivity() {

    companion object {
        const val CAMERA_PERMISSION_REQUEST = 1
        const val TAG = "CameraActivity"
    }

    private lateinit var cameraView: OnfidoCameraView
    private lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        cameraView = findViewById(R.id.camera_view)
        button = findViewById(R.id.action_take_picture)

        button.setOnClickListener {
            cameraView.takePicture {
                Log.d(TAG, "picture received! ${it.width} ${it.height}")
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
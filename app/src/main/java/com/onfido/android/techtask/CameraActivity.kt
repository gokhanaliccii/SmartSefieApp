package com.onfido.android.techtask

import android.Manifest
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import io.fotoapparat.Fotoapparat
import io.fotoapparat.log.logcat
import io.fotoapparat.log.loggers
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.selector.front
import io.fotoapparat.view.CameraView

class CameraActivity : AppCompatActivity() {

    companion object {
        const val CAMERA_PERMISSION_REQUEST = 1
    }

    private lateinit var cameraView: CameraView
    private val fotoapparat: Fotoapparat by lazy { createFotoapparat() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        cameraView = findViewById(R.id.camera_view)
    }

    // Ref(Step Two): https://github.com/RedApparat/Fotoapparat
    private fun createFotoapparat(): Fotoapparat {
        return Fotoapparat(
            context = this,
            view = cameraView,
            scaleType = ScaleType.CenterCrop,
            lensPosition = front(),
            logger = loggers(
                logcat()
            )
        )
    }

    override fun onStart() {
        super.onStart()

        if (hasCameraPermission()) {
            fotoapparat.start()
        } else {
            requestForCameraPermission()
        }
    }

    override fun onStop() {
        super.onStop()

        if (hasCameraPermission()) {
            fotoapparat.stop()
        }
    }

    private fun cameraPermissionPermitted() {
        fotoapparat.start()
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
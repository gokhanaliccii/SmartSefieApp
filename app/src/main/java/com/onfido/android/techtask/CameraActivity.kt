package com.onfido.android.techtask

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import io.fotoapparat.Fotoapparat
import io.fotoapparat.log.logcat
import io.fotoapparat.log.loggers
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.selector.front
import io.fotoapparat.view.CameraView

class CameraActivity : AppCompatActivity() {

    private lateinit var cameraView: CameraView
    private val fotoapparat: Fotoapparat by lazy { createFotoapparat() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        cameraView = findViewById(R.id.camera_view)
        createFotoapparat()
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
        fotoapparat.start()
    }

    override fun onStop() {
        super.onStop()
        fotoapparat.stop()
    }
}

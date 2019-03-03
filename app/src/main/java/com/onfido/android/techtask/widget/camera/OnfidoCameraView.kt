package com.onfido.android.techtask.widget.camera

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.support.annotation.RequiresPermission
import android.util.AttributeSet
import android.widget.FrameLayout
import io.fotoapparat.Fotoapparat
import io.fotoapparat.log.logcat
import io.fotoapparat.log.loggers
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.selector.front
import io.fotoapparat.view.CameraView

/**
 * Created by gokhan.alici on 03.03.2019
 */
class OnfidoCameraView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), CameraPreview {

    private val cameraView by lazy { CameraView(context) }
    private val fotoapparat: Fotoapparat by lazy { createFotoapparat() }

    init {
        addView(cameraView)
    }

    @RequiresPermission(Manifest.permission.CAMERA, conditional = true)
    override fun start() {
        fotoapparat.start()
    }

    override fun stop() {
        fotoapparat.stop()
    }

    override fun takePicture(pictureCallback: (Bitmap) -> Unit) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun switchCamera(position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun changePreviewQuality(quality: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    // Ref(Step Two): https://github.com/RedApparat/Fotoapparat
    private fun createFotoapparat(): Fotoapparat {
        return Fotoapparat(
            context = context,
            view = cameraView,
            scaleType = ScaleType.CenterCrop,
            lensPosition = front(),
            logger = loggers(
                logcat()
            )
        )
    }
}
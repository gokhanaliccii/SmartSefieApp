package com.onfido.android.techtask.widget.camera

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.support.annotation.RequiresPermission
import android.util.AttributeSet
import android.widget.FrameLayout
import io.fotoapparat.Fotoapparat
import io.fotoapparat.configuration.CameraConfiguration
import io.fotoapparat.log.logcat
import io.fotoapparat.log.loggers
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.selector.back
import io.fotoapparat.selector.front
import io.fotoapparat.selector.highestResolution
import io.fotoapparat.selector.lowestResolution
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
        fotoapparat.takePicture()
            .toBitmap()
            .whenAvailable {
                it?.let { bitmapPhoto ->
                    pictureCallback(bitmapPhoto.bitmap)
                }
            }
    }

    override fun switchCamera(@CameraPosition position: Int) {
        val lensPosition = when (position) {
            CameraPosition.FRONT -> front()
            CameraPosition.BACK -> back()
            else -> throw IllegalArgumentException("Unknown camera position [$position]")
        }

        fotoapparat.switchTo(lensPosition, CameraConfiguration.default())
    }

    override fun changePreviewQuality(@PreviewQuality quality: Int) {
        val previewResolution = when (quality) {
            PreviewQuality.LOW -> lowestResolution()
            PreviewQuality.HIGH -> highestResolution()
            else -> throw IllegalArgumentException("Unknown preview quality parameter [$quality]")
        }

        CameraConfiguration.builder()
            .previewResolution(previewResolution)
            .build()
            .apply {
                fotoapparat.updateConfiguration(this)
            }
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
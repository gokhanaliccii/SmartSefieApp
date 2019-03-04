package com.onfido.camera

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import android.support.annotation.RequiresPermission
import android.util.AttributeSet
import android.widget.FrameLayout
import com.onfido.android.techtask.widget.camera.CameraPosition
import com.onfido.android.techtask.widget.camera.CameraPreview
import com.onfido.android.techtask.widget.camera.CameraPreviewQuality
import com.onfido.camera.frame.CompositeFrameProcessor
import com.onfido.camera.frame.FrameInfo
import com.onfido.camera.frame.FrameProcessor
import com.onfido.android.techtask.widget.camera.util.modifyBitmap
import io.fotoapparat.Fotoapparat
import io.fotoapparat.characteristic.LensPosition
import io.fotoapparat.configuration.CameraConfiguration
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.preview.Frame
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

    private companion object {
        const val FLIP = -1f
        const val DEFAULT_SCALE_FACTOR = 0.25f
    }

    private val cameraView by lazy { CameraView(context) }
    private val fotoapparat: Fotoapparat by lazy { createFotoapparat() }
    private val compositeFrameProcessor = CompositeFrameProcessor()

    private var mirroring = false
    private var scaleFactor = DEFAULT_SCALE_FACTOR

    init {
        attrs?.let {
            processAttributes(context, attrs)
        }

        addView(cameraView)
    }

    private fun processAttributes(context: Context, attrs: AttributeSet?) {
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.OnfidoCameraView)
        mirroring =
            typedArray.getBoolean(R.styleable.OnfidoCameraView_mirrorTakenPicture, mirroring)
        scaleFactor = typedArray.getFloat(R.styleable.OnfidoCameraView_scaleFactor, scaleFactor)
        typedArray.recycle()
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
            .transform {
                modifyBitmap(it.bitmap, scaleFactor, it.rotationDegrees * FLIP, mirroring)
            }
            .whenAvailable {
                it?.let { bitmapPhoto ->
                    pictureCallback(bitmapPhoto)
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

    override fun changePreviewQuality(@CameraPreviewQuality quality: Int) {
        val previewResolution = when (quality) {
            CameraPreviewQuality.LOW -> lowestResolution()
            CameraPreviewQuality.HIGH -> highestResolution()
            else -> throw IllegalArgumentException("Unknown preview quality parameter [$quality]")
        }

        CameraConfiguration.builder()
            .previewResolution(previewResolution)
            .build()
            .apply {
                fotoapparat.updateConfiguration(this)
            }
    }

    override fun addFrameProcessor(frameProcessor: FrameProcessor) {
        compositeFrameProcessor += frameProcessor
    }

    override fun removeFrameProcessor(frameProcessor: FrameProcessor) {
        compositeFrameProcessor -= frameProcessor
    }

    private fun createFotoapparat(): Fotoapparat {
        return Fotoapparat.with(context)
            .into(cameraView)
            .lensPosition { LensPosition.Front }
            .previewScaleType(ScaleType.CenterCrop)
            .frameProcessor {
                compositeFrameProcessor.processFrame(toFrameInfo(it))
            }
            .build()
    }

    private fun toFrameInfo(frame: Frame): FrameInfo =
        FrameInfo(frame.size.width, frame.size.height, frame.image, frame.rotation)
}
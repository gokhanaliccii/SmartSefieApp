package com.onfido.android.techtask.widget.camera

import android.graphics.Bitmap
import com.onfido.android.techtask.widget.camera.frame.FrameProcessor

/**
 * Created by gokhan.alici on 03.03.2019
 */
interface CameraPreview {

    fun start()

    fun stop()

    fun takePicture(pictureCallback: (Bitmap) -> Unit)

    fun switchCamera(@CameraPosition position: Int)

    fun changePreviewQuality(@PreviewQuality quality: Int)

    fun addFrameProcessor(frameProcessor: FrameProcessor)

    fun removeFrameProcessor(frameProcessor: FrameProcessor)
}
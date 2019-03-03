package com.onfido.camera.frame

/**
 * Created by gokhan.alici on 03.03.2019
 */
internal class CompositeFrameProcessor : FrameProcessor {

    private val frameProcessors = mutableListOf<FrameProcessor>()

    override fun processFrame(frameInfo: FrameInfo) {
        if (frameProcessors.size > 0) {
            frameProcessors.forEach {
                it.processFrame(frameInfo)
            }
        }
    }

    fun clear() {
        frameProcessors.clear()
    }

    private fun addFrameProcessor(frameProcessor: FrameProcessor) {
        frameProcessors += frameProcessor
    }

    private fun removeFrameProcessor(frameProcessor: FrameProcessor) {
        frameProcessors -= frameProcessor
    }

    operator fun plusAssign(frameProcessor: FrameProcessor) {
        addFrameProcessor(frameProcessor)
    }

    operator fun minusAssign(frameProcessor: FrameProcessor) {
        removeFrameProcessor(frameProcessor)
    }
}
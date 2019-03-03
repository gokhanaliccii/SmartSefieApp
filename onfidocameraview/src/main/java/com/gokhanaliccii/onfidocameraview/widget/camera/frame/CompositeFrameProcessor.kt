package com.gokhanaliccii.onfidocameraview.frame

/**
 * Created by gokhan.alici on 03.03.2019
 */
class CompositeFrameProcessor : FrameProcessor {

    private val frameProcessors = mutableListOf<FrameProcessor>()

    override fun processFrame(frameData: FrameInfo) {
        if (frameProcessors.size > 0) {
            frameProcessors.forEach {
                it.processFrame(frameData)
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
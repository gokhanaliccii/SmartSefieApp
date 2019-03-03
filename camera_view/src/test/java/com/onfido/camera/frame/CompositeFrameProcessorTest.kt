package com.onfido.camera.frame

import io.mockk.Called
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

/**
 * Created by gokhan.alici on 03.03.2019
 */
class CompositeFrameProcessorTest {

    @Test
    fun `should add frameProcessors and notify them each processFrame() correctly`() {
        val compositeFrameProcessor = CompositeFrameProcessor()
        val mockFrameProcessor = mockk<FrameProcessor>(relaxed = true)
        val mockFrameInfo = mockk<FrameInfo>(relaxed = true)

        compositeFrameProcessor += mockFrameProcessor

        compositeFrameProcessor.processFrame(mockFrameInfo)

        verify {
            mockFrameProcessor.processFrame(any())
        }
    }

    @Test
    fun `should not notify frameProcessors when compositeFrameProcessor cleared `() {
        val compositeFrameProcessor = CompositeFrameProcessor()
        val mockFrameProcessor = mockk<FrameProcessor>(relaxed = true)
        val mockFrameInfo = mockk<FrameInfo>(relaxed = true)

        compositeFrameProcessor += mockFrameProcessor
        compositeFrameProcessor.clear()
        compositeFrameProcessor.processFrame(mockFrameInfo)

        verify {
            mockFrameProcessor wasNot Called
        }
    }
}
package com.onfido.camera.frame

import java.util.*

/**
 * Created by gokhan.alici on 03.03.2019
 */
data class FrameInfo(
    val width: Int,
    val height: Int,
    val byteArray: ByteArray,
    val rotation: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FrameInfo

        if (width != other.width) return false
        if (height != other.height) return false
        if (!Arrays.equals(byteArray, other.byteArray)) return false
        if (rotation != other.rotation) return false

        return true
    }

    override fun hashCode(): Int {
        var result = 0
        result = 31 * result + Arrays.hashCode(byteArray)
        result = 31 * result + rotation
        result = 31 * result + width
        result = 31 * result + height
        return result
    }
}
package com.raillylinker.module_common.util_components

import java.awt.image.*
import java.io.InputStream
import java.io.OutputStream
import java.util.*

// [Gif 관련 유틸 오브젝트]
interface GifUtil {
    // <멤버 변수 공간>


    // ---------------------------------------------------------------------------------------------
    // <공개 메소드 공간>
    // (Gif input 을 프레임 리스트로 분리)
    fun decodeGif(inputStream: InputStream): ArrayList<GifFrame>

    // (Frame 리스트를 Gif Output 으로 합치기)
    fun encodeGif(
        gifFrameList: ArrayList<GifFrame>,
        outputStream: OutputStream,
        repeatCount: Int,
        applyDither: Boolean
    )


    // ---------------------------------------------------------------------------------------------
    // <비공개 메소드 공간>


    // ---------------------------------------------------------------------------------------------
    // <중첩 클래스 공간>
    data class GifFrame(
        val frameBufferedImage: BufferedImage,
        val frameDelay: Int
    )
}
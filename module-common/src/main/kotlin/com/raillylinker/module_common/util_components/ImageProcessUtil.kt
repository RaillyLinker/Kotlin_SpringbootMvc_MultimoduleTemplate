package com.raillylinker.module_common.util_components

import java.awt.Color
import java.awt.Font
import java.awt.image.BufferedImage
import java.io.*

// [이미지 처리 유틸]
interface ImageProcessUtil {
    // (움직이지 않는 정적 이미지 리사이징 및 리포멧 함수)
    fun resizeImage(
        imageBytes: ByteArray,
        resizeWidth: Int,
        resizeHeight: Int,
        imageTypeEnum: ResizeImageTypeEnum
    ): ByteArray

    enum class ResizeImageTypeEnum(val typeStr: String) {
        JPG("jpg"),
        PNG("png"),
        BMP("bmp"),
        GIF("gif")
    }

    // (Gif 를 이미지 리스트로 분리)
    fun gifToImageList(inputStream: InputStream): ArrayList<GifUtil.GifFrame>

    // (이미지 리스트를 Gif 로 병합)
    fun imageListToGif(gifFrameList: ArrayList<GifUtil.GifFrame>, outputStream: OutputStream)

    fun resizeGifImage(inputStream: InputStream, newWidth: Int, newHeight: Int): ByteArray

    // (문자열을 투명 배경 서명 이미지로 변경하는 함수)
    fun createSignatureImage(
        // 서명화할 텍스트
        text: String,
        // 사인 이미지 사이즈
        signImageWidth: Int,
        signImageHeight: Int,
        // 사인 텍스트 색상
        signColor: Color,
        // 사인 텍스트 폰트
        signFont: Font
    ): BufferedImage
}
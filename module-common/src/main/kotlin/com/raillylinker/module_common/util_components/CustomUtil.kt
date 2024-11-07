package com.raillylinker.module_common.util_components

import java.io.File
import java.nio.file.Path
import java.util.zip.ZipOutputStream

// [커스텀 유틸 함수 모음]
interface CustomUtil {
    // (디렉토리 내 파일들을 ZipOutputStream 으로 추가)
    fun compressDirectoryToZip(directory: File, path: String, zipOutputStream: ZipOutputStream)

    // (파일들을 ZipOutputStream 으로 추가)
    fun addToZip(file: File, fileName: String, zipOutputStream: ZipOutputStream)

    // (zip 파일을 압축 풀기)
    fun unzipFile(zipFilePath: String, destDirectory: Path)

    // (랜덤 영문 대소문자 + 숫자 문자열 생성)
    fun getRandomString(length: Int): String

    // (이메일 적합성 검증)
    fun isValidEmail(email: String): Boolean

    // (ThymeLeaf 엔진으로 랜더링 한 HTML String 을 반환)
    fun parseHtmlFileToHtmlString(justHtmlFileNameWithOutSuffix: String, variableDataMap: Map<String, Any?>): String

    // (byteArray 를 Hex String 으로 반환)
    fun bytesToHex(bytes: ByteArray): String

    // (degree 를 radian 으로)
    fun deg2rad(deg: Double): Double

    // (radian 을 degree 로)
    fun rad2deg(rad: Double): Double


    // ---------------------------------------------------------------------------------------------
    // <중첩 클래스 공간>

}
package com.raillylinker.module_common.util_components

// [암호화, 복호화 관련 유틸]
interface CryptoUtil {
    // [암호화 / 복호화]
    // (AES256 암호화)
    fun encryptAES256(
        text: String, // 암호화하려는 평문
        alg: String, // 암호화 알고리즘 (ex : "AES/CBC/PKCS5Padding")
        initializationVector: String, // 초기화 벡터 16byte = 16char
        encryptionKey: String // 암호화 키 32byte = 32char
    ): String

    // (AES256 복호화)
    fun decryptAES256(
        cipherText: String, // 복호화하려는 암호문
        alg: String, // 암호화 알고리즘 (ex : "AES/CBC/PKCS5Padding")
        initializationVector: String, // 초기화 벡터 16byte = 16char
        encryptionKey: String // 암호화 키 32byte = 32char
    ): String


    ///////////////////////////////////////////////////////////////////////////////////////////
    // [인코딩 / 디코딩]
    // (Base64 인코딩)
    fun base64Encode(str: String): String

    // (Base64 디코딩)
    fun base64Decode(str: String): String


    ///////////////////////////////////////////////////////////////////////////////////////////
    // [해싱]
    // (SHA256 해싱)
    fun hashSHA256(str: String): String

    // (HmacSHA256)
    fun hmacSha256(data: String, secret: String): String
}
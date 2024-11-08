package com.raillylinker.module_security.util_components

import java.time.LocalDateTime

// [JWT 토큰 유틸]
interface JwtTokenUtil {
    // <공개 메소드 공간>
    // (액세스 토큰 발행)
    // memberRoleList : 멤버 권한 리스트 (ex : ["ROLE_ADMIN", "ROLE_DEVELOPER"])
    fun generateAccessToken(
        memberUid: Long,
        accessTokenExpirationTimeSec: Long,
        jwtClaimsAes256InitializationVector: String,
        jwtClaimsAes256EncryptionKey: String,
        issuer: String,
        jwtSecretKeyString: String,
        roleList: List<String>
    ): String

    // (리프레시 토큰 발행)
    fun generateRefreshToken(
        memberUid: Long,
        refreshTokenExpirationTimeSec: Long,
        jwtClaimsAes256InitializationVector: String,
        jwtClaimsAes256EncryptionKey: String,
        issuer: String,
        jwtSecretKeyString: String
    ): String

    // (JWT Secret 확인)
    // : 토큰 유효성 검증. 유효시 true, 위변조시 false
    fun validateSignature(
        token: String,
        jwtSecretKeyString: String
    ): Boolean

    // (JWT 정보 반환)
    // Member Uid
    fun getMemberUid(
        token: String,
        jwtClaimsAes256InitializationVector: String,
        jwtClaimsAes256EncryptionKey: String
    ): Long

    // (Token 용도 (access or refresh) 반환)
    fun getTokenUsage(
        token: String,
        jwtClaimsAes256InitializationVector: String,
        jwtClaimsAes256EncryptionKey: String
    ): String

    // (멤버 권한 리스트 반환)
    fun getRoleList(
        token: String,
        jwtClaimsAes256InitializationVector: String,
        jwtClaimsAes256EncryptionKey: String
    ): List<String>

    // (발행자 반환)
    fun getIssuer(token: String): String

    // (토큰 남은 유효 시간(초) 반환 (만료된 토큰이라면 0))
    fun getRemainSeconds(token: String): Long

    // (토큰 만료 일시 반환)
    fun getExpirationDateTime(token: String): LocalDateTime

    // (토큰 타입)
    fun getTokenType(token: String): String
}
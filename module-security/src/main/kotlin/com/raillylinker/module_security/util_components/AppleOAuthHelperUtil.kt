package com.raillylinker.module_security.util_components

// [Apple OAuth2 검증 관련 유틸]
interface AppleOAuthHelperUtil {
    // 애플 Id Token 검증 함수 - 검증이 완료되었다면 프로필 정보가 반환되고, 검증되지 않는다면 null 반환
    fun getAppleMemberData(idToken: String): AppleProfileData?

    data class AppleProfileData(
        val snsId: String,
        val email: String?
    )
}
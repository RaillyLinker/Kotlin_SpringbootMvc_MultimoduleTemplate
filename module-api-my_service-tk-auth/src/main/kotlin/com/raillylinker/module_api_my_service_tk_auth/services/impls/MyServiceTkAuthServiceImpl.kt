package com.raillylinker.module_api_my_service_tk_auth.services.impls

import com.raillylinker.module_api_my_service_tk_auth.controllers.MyServiceTkAuthController
import com.raillylinker.module_api_my_service_tk_auth.services.MyServiceTkAuthService
import com.raillylinker.module_common.util_components.EmailSender
import com.raillylinker.module_common.util_components.NaverSmsSenderComponent
import com.raillylinker.module_jpa.annotations.CustomTransactional
import com.raillylinker.module_jpa.configurations.jpa_configs.Db1MainConfig
import com.raillylinker.module_jpa.jpa_beans.db1_main.entities.*
import com.raillylinker.module_jpa.jpa_beans.db1_main.repositories.*
import com.raillylinker.module_redis.redis_map_components.redis1_main.Redis1_Map_Service1ForceExpireAuthorizationSet
import com.raillylinker.module_retrofit2.retrofit2_classes.RepositoryNetworkRetrofit2
import com.raillylinker.module_security.configurations.SecurityConfig.AuthTokenFilterService1Tk.Companion.AUTH_JWT_ACCESS_TOKEN_EXPIRATION_TIME_SEC
import com.raillylinker.module_security.configurations.SecurityConfig.AuthTokenFilterService1Tk.Companion.AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
import com.raillylinker.module_security.configurations.SecurityConfig.AuthTokenFilterService1Tk.Companion.AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR
import com.raillylinker.module_security.configurations.SecurityConfig.AuthTokenFilterService1Tk.Companion.AUTH_JWT_ISSUER
import com.raillylinker.module_security.configurations.SecurityConfig.AuthTokenFilterService1Tk.Companion.AUTH_JWT_REFRESH_TOKEN_EXPIRATION_TIME_SEC
import com.raillylinker.module_security.configurations.SecurityConfig.AuthTokenFilterService1Tk.Companion.AUTH_JWT_SECRET_KEY_STRING
import com.raillylinker.module_security.util_components.AppleOAuthHelperUtil
import com.raillylinker.module_security.util_components.JwtTokenUtil
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.Resource
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class MyServiceTkAuthServiceImpl(
    // (프로젝트 실행시 사용 설정한 프로필명 (ex : dev8080, prod80, local8080, 설정 안하면 default 반환))
    @Value("\${spring.profiles.active:default}") private var activeProfile: String,

    private val passwordEncoder: PasswordEncoder,
    private val emailSender: EmailSender,
    private val naverSmsSenderComponent: NaverSmsSenderComponent,
    private val jwtTokenUtil: JwtTokenUtil,
    private val appleOAuthHelperUtil: AppleOAuthHelperUtil,

    // (Redis Map)
    private val redis1Service1ForceExpireAuthorizationSet: Redis1_Map_Service1ForceExpireAuthorizationSet,

    // (Database Repository)
    private val db1NativeRepository: Db1_Native_Repository,
    private val db1RaillyLinkerCompanyService1MemberDataRepository: Db1_RaillyLinkerCompany_Service1MemberData_Repository,
    private val db1RaillyLinkerCompanyService1MemberRoleDataRepository: Db1_RaillyLinkerCompany_Service1MemberRoleData_Repository,
    private val db1RaillyLinkerCompanyService1MemberEmailDataRepository: Db1_RaillyLinkerCompany_Service1MemberEmailData_Repository,
    private val db1RaillyLinkerCompanyService1MemberPhoneDataRepository: Db1_RaillyLinkerCompany_Service1MemberPhoneData_Repository,
    private val db1RaillyLinkerCompanyService1MemberOauth2LoginDataRepository: Db1_RaillyLinkerCompany_Service1MemberOauth2LoginData_Repository,
    private val db1RaillyLinkerCompanyService1JoinTheMembershipWithPhoneNumberVerificationDataRepository: Db1_RaillyLinkerCompany_Service1JoinTheMembershipWithPhoneNumberVerificationData_Repository,
    private val db1RaillyLinkerCompanyService1JoinTheMembershipWithEmailVerificationDataRepository: Db1_RaillyLinkerCompany_Service1JoinTheMembershipWithEmailVerificationData_Repository,
    private val db1RaillyLinkerCompanyService1JoinTheMembershipWithOauth2VerificationDataRepository: Db1_RaillyLinkerCompany_Service1JoinTheMembershipWithOauth2VerificationData_Repository,
    private val db1RaillyLinkerCompanyService1FindPasswordWithPhoneNumberVerificationDataRepository: Db1_RaillyLinkerCompany_Service1FindPasswordWithPhoneNumberVerificationData_Repository,
    private val db1RaillyLinkerCompanyService1FindPasswordWithEmailVerificationDataRepository: Db1_RaillyLinkerCompany_Service1FindPasswordWithEmailVerificationData_Repository,
    private val db1RaillyLinkerCompanyService1AddEmailVerificationDataRepository: Db1_RaillyLinkerCompany_Service1AddEmailVerificationData_Repository,
    private val db1RaillyLinkerCompanyService1AddPhoneNumberVerificationDataRepository: Db1_RaillyLinkerCompany_Service1AddPhoneNumberVerificationData_Repository,
    private val db1RaillyLinkerCompanyService1MemberProfileDataRepository: Db1_RaillyLinkerCompany_Service1MemberProfileData_Repository,
    private val db1RaillyLinkerCompanyService1LogInTokenHistoryRepository: Db1_RaillyLinkerCompany_Service1LogInTokenHistory_Repository
) : MyServiceTkAuthService {
    // <멤버 변수 공간>
    private val classLogger: Logger = LoggerFactory.getLogger(this::class.java)

    // Retrofit2 요청 객체
    val networkRetrofit2: RepositoryNetworkRetrofit2 = RepositoryNetworkRetrofit2.getInstance()

    // (현 프로젝트 동작 서버의 외부 접속 주소)
    // 프로필 이미지 로컬 저장 및 다운로드 주소 지정을 위해 필요
    // !!!프로필별 접속 주소 설정하기!!
    // ex : http://127.0.0.1:8080
    private val externalAccessAddress: String
        get() {
            return when (activeProfile) {
                "prod80" -> {
                    "http://127.0.0.1"
                }

                "dev8080" -> {
                    "http://127.0.0.1:8080"
                }

                else -> {
                    "http://127.0.0.1:8080"
                }
            }
        }


    // ---------------------------------------------------------------------------------------------
    // <공개 메소드 공간>
    override fun noLoggedInAccessTest(httpServletResponse: HttpServletResponse): String? {
        httpServletResponse.status = HttpStatus.OK.value()
        return externalAccessAddress
    }


    ////
    override fun loggedInAccessTest(httpServletResponse: HttpServletResponse, authorization: String): String? {
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )

        httpServletResponse.status = HttpStatus.OK.value()
        return "Member No.$memberUid : Test Success"
    }

    ////
    override fun adminAccessTest(httpServletResponse: HttpServletResponse, authorization: String): String? {
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )

        httpServletResponse.status = HttpStatus.OK.value()
        return "Member No.$memberUid : Test Success"
    }

    ////
    override fun developerAccessTest(httpServletResponse: HttpServletResponse, authorization: String): String? {
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )

        httpServletResponse.status = HttpStatus.OK.value()
        return "Member No.$memberUid : Test Success"
    }


    ////
    @CustomTransactional([Db1MainConfig.TRANSACTION_NAME])
    override fun doExpireAccessToken(
        httpServletResponse: HttpServletResponse,
        memberUid: Long,
        inputVo: MyServiceTkAuthController.DoExpireAccessTokenInputVo
    ) {
        if (inputVo.apiSecret != "aadke234!@") {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        val memberEntity = db1RaillyLinkerCompanyService1MemberDataRepository.findById(memberUid)

        if (memberEntity.isEmpty) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "2")
            return
        } else {
            val tokenEntityList =
                db1RaillyLinkerCompanyService1LogInTokenHistoryRepository.findAllByService1MemberDataAndAccessTokenExpireWhenAfterAndRowDeleteDateStr(
                    memberEntity.get(),
                    LocalDateTime.now(),
                    "/"
                )
            for (tokenEntity in tokenEntityList) {
                val tokenType = tokenEntity.tokenType
                val accessToken = tokenEntity.accessToken

                val accessTokenExpireRemainSeconds = when (tokenType) {
                    "Bearer" -> {
                        jwtTokenUtil.getRemainSeconds(accessToken)
                    }

                    else -> {
                        null
                    }
                }

                // 강제 만료 정보에 입력하기
                try {
                    redis1Service1ForceExpireAuthorizationSet.saveKeyValue(
                        "${tokenType}_${accessToken}",
                        Redis1_Map_Service1ForceExpireAuthorizationSet.ValueVo(),
                        accessTokenExpireRemainSeconds!! * 1000
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }

        httpServletResponse.status = HttpStatus.OK.value()
        return
    }

    ////
    @CustomTransactional([Db1MainConfig.TRANSACTION_NAME])
    override fun loginWithPassword(
        httpServletResponse: HttpServletResponse,
        inputVo: MyServiceTkAuthController.LoginWithPasswordInputVo
    ): MyServiceTkAuthController.LoginOutputVo? {
        val memberData: Db1_RaillyLinkerCompany_Service1MemberData
        when (inputVo.loginTypeCode) {
            0 -> { // 아이디
                // (정보 검증 로직 수행)
                val member = db1RaillyLinkerCompanyService1MemberDataRepository.findByAccountIdAndRowDeleteDateStr(
                    inputVo.id,
                    "/"
                )

                if (member == null) { // 가입된 회원이 없음
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "1")
                    return null
                }
                memberData = member
            }

            1 -> { // 이메일
                // (정보 검증 로직 수행)
                val memberEmail =
                    db1RaillyLinkerCompanyService1MemberEmailDataRepository.findByEmailAddressAndRowDeleteDateStr(
                        inputVo.id,
                        "/"
                    )

                if (memberEmail == null) { // 가입된 회원이 없음
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "1")
                    return null
                }
                memberData = memberEmail.service1MemberData
            }

            2 -> { // 전화번호
                // (정보 검증 로직 수행)
                val memberPhone =
                    db1RaillyLinkerCompanyService1MemberPhoneDataRepository.findByPhoneNumberAndRowDeleteDateStr(
                        inputVo.id,
                        "/"
                    )

                if (memberPhone == null) { // 가입된 회원이 없음
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "1")
                    return null
                }
                memberData = memberPhone.service1MemberData
            }

            else -> {
                classLogger.info("loginTypeCode ${inputVo.loginTypeCode} Not Supported")
                httpServletResponse.status = HttpStatus.BAD_REQUEST.value()
                return null
            }
        }

        if (memberData.accountPassword == null || // 페스워드는 아직 만들지 않음
            !passwordEncoder.matches(inputVo.password, memberData.accountPassword!!) // 패스워드 불일치
        ) {
            // 두 상황 모두 비밀번호 찾기를 하면 해결이 됨
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "2")
            return null
        }

        // 계정 정지 검증
        val lockList = db1NativeRepository.findAllNowActivateMemberLockInfo(memberData.uid!!, LocalDateTime.now())
        if (lockList.isNotEmpty()) {
            // 계정 정지 당한 상황
            val lockedOutputList: MutableList<MyServiceTkAuthController.LoginOutputVo.LockedOutput> =
                mutableListOf()
            for (lockInfo in lockList) {
                lockedOutputList.add(
                    MyServiceTkAuthController.LoginOutputVo.LockedOutput(
                        memberData.uid!!,
                        lockInfo.lockStart.atZone(ZoneId.systemDefault())
                            .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                        if (lockInfo.lockBefore == null) {
                            null
                        } else {
                            lockInfo.lockBefore!!.atZone(ZoneId.systemDefault())
                                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
                        },
                        lockInfo.lockReasonCode.toInt(),
                        lockInfo.lockReason
                    )
                )
            }

            httpServletResponse.status = HttpStatus.OK.value()
            return MyServiceTkAuthController.LoginOutputVo(
                null,
                lockedOutputList
            )
        }

        // 멤버의 권한 리스트를 조회 후 반환
        val memberRoleList =
            db1RaillyLinkerCompanyService1MemberRoleDataRepository.findAllByService1MemberDataAndRowDeleteDateStr(
                memberData,
                "/"
            )
        val roleList: ArrayList<String> = arrayListOf()
        for (userRole in memberRoleList) {
            roleList.add(userRole.role)
        }

        // (토큰 생성 로직 수행)
        // 멤버 고유번호로 엑세스 토큰 생성
        val jwtAccessToken = jwtTokenUtil.generateAccessToken(
            memberData.uid!!,
            AUTH_JWT_ACCESS_TOKEN_EXPIRATION_TIME_SEC,
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY,
            AUTH_JWT_ISSUER,
            AUTH_JWT_SECRET_KEY_STRING,
            roleList
        )

        val accessTokenExpireWhen = jwtTokenUtil.getExpirationDateTime(jwtAccessToken)

        // 액세스 토큰의 리프레시 토큰 생성 및 DB 저장 = 액세스 토큰에 대한 리프레시 토큰은 1개 혹은 0개
        val jwtRefreshToken = jwtTokenUtil.generateRefreshToken(
            memberData.uid!!,
            AUTH_JWT_REFRESH_TOKEN_EXPIRATION_TIME_SEC,
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY,
            AUTH_JWT_ISSUER,
            AUTH_JWT_SECRET_KEY_STRING
        )

        val refreshTokenExpireWhen = jwtTokenUtil.getExpirationDateTime(jwtRefreshToken)

        // 로그인 정보 저장
        db1RaillyLinkerCompanyService1LogInTokenHistoryRepository.save(
            Db1_RaillyLinkerCompany_Service1LogInTokenHistory(
                memberData,
                "Bearer",
                LocalDateTime.now(),
                jwtAccessToken,
                accessTokenExpireWhen,
                jwtRefreshToken,
                refreshTokenExpireWhen,
                null
            )
        )

        httpServletResponse.status = HttpStatus.OK.value()
        return MyServiceTkAuthController.LoginOutputVo(
            MyServiceTkAuthController.LoginOutputVo.LoggedInOutput(
                memberData.uid!!,
                "Bearer",
                jwtAccessToken,
                jwtRefreshToken,
                accessTokenExpireWhen.atZone(ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                refreshTokenExpireWhen.atZone(ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
            ),
            null
        )
    }


    ////
    override fun getOAuth2AccessToken(
        httpServletResponse: HttpServletResponse,
        oauth2TypeCode: Int,
        oauth2Code: String
    ): MyServiceTkAuthController.GetOAuth2AccessTokenOutputVo? {
        val snsAccessTokenType: String
        val snsAccessToken: String

        // !!!OAuth2 ClientId!!
        val clientId = "TODO"

        // !!!OAuth2 clientSecret!!
        val clientSecret = "TODO"

        // !!!OAuth2 로그인할때 사용한 Redirect Uri!!
        val redirectUri = "TODO"

        // (정보 검증 로직 수행)
        when (oauth2TypeCode) {
            1 -> { // GOOGLE
                // Access Token 가져오기
                val atResponse = networkRetrofit2.accountsGoogleComRequestApi.postOOauth2Token(
                    oauth2Code,
                    clientId,
                    clientSecret,
                    "authorization_code",
                    redirectUri
                ).execute()

                // code 사용 결과 검증
                if (atResponse.code() != 200 ||
                    atResponse.body() == null ||
                    atResponse.body()!!.accessToken == null
                ) {
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "1")
                    return null
                }

                snsAccessTokenType = atResponse.body()!!.tokenType!!
                snsAccessToken = atResponse.body()!!.accessToken!!
            }

            2 -> { // NAVER
                // !!!OAuth2 로그인시 사용한 State!!
                val state = "TODO"

                // Access Token 가져오기
                val atResponse = networkRetrofit2.nidNaverComRequestApi.getOAuth2Dot0Token(
                    "authorization_code",
                    clientId,
                    clientSecret,
                    redirectUri,
                    oauth2Code,
                    state
                ).execute()

                // code 사용 결과 검증
                if (atResponse.code() != 200 ||
                    atResponse.body() == null ||
                    atResponse.body()!!.accessToken == null
                ) {
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "1")
                    return null
                }

                snsAccessTokenType = atResponse.body()!!.tokenType!!
                snsAccessToken = atResponse.body()!!.accessToken!!
            }

            3 -> { // KAKAO
                // Access Token 가져오기
                val atResponse = networkRetrofit2.kauthKakaoComRequestApi.postOOauthToken(
                    "authorization_code",
                    clientId,
                    clientSecret,
                    redirectUri,
                    oauth2Code
                ).execute()

                // code 사용 결과 검증
                if (atResponse.code() != 200 ||
                    atResponse.body() == null ||
                    atResponse.body()!!.accessToken == null
                ) {
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "1")
                    return null
                }

                snsAccessTokenType = atResponse.body()!!.tokenType!!
                snsAccessToken = atResponse.body()!!.accessToken!!
            }

            else -> {
                classLogger.info("SNS Login Type $oauth2TypeCode Not Supported")
                httpServletResponse.status = 400
                return null
            }
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return MyServiceTkAuthController.GetOAuth2AccessTokenOutputVo(
            snsAccessTokenType,
            snsAccessToken
        )
    }


    ////
    @CustomTransactional([Db1MainConfig.TRANSACTION_NAME])
    override fun loginWithOAuth2AccessToken(
        httpServletResponse: HttpServletResponse,
        inputVo: MyServiceTkAuthController.LoginWithOAuth2AccessTokenInputVo
    ): MyServiceTkAuthController.LoginOutputVo? {
        val snsOauth2: Db1_RaillyLinkerCompany_Service1MemberOauth2LoginData?

        // (정보 검증 로직 수행)
        when (inputVo.oauth2TypeCode) {
            1 -> { // GOOGLE
                // 클라이언트에서 받은 access 토큰으로 멤버 정보 요청
                val response = networkRetrofit2.wwwGoogleapisComRequestApi.getOauth2V1UserInfo(
                    inputVo.oauth2AccessToken
                ).execute()

                // 액세트 토큰 정상 동작 확인
                if (response.code() != 200 ||
                    response.body() == null
                ) {
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "1")
                    return null
                }

                snsOauth2 =
                    db1RaillyLinkerCompanyService1MemberOauth2LoginDataRepository.findByOauth2TypeCodeAndOauth2IdAndRowDeleteDateStr(
                        1,
                        response.body()!!.id!!,
                        "/"
                    )
            }

            2 -> { // NAVER
                // 클라이언트에서 받은 access 토큰으로 멤버 정보 요청
                val response = networkRetrofit2.openapiNaverComRequestApi.getV1NidMe(
                    inputVo.oauth2AccessToken
                ).execute()

                // 액세트 토큰 정상 동작 확인
                if (response.code() != 200 ||
                    response.body() == null
                ) {
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "1")
                    return null
                }

                snsOauth2 =
                    db1RaillyLinkerCompanyService1MemberOauth2LoginDataRepository.findByOauth2TypeCodeAndOauth2IdAndRowDeleteDateStr(
                        2,
                        response.body()!!.response.id,
                        "/"
                    )
            }

            3 -> { // KAKAO
                // 클라이언트에서 받은 access 토큰으로 멤버 정보 요청
                val response = networkRetrofit2.kapiKakaoComRequestApi.getV2UserMe(
                    inputVo.oauth2AccessToken
                ).execute()

                // 액세트 토큰 정상 동작 확인
                if (response.code() != 200 ||
                    response.body() == null
                ) {
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "1")
                    return null
                }

                snsOauth2 =
                    db1RaillyLinkerCompanyService1MemberOauth2LoginDataRepository.findByOauth2TypeCodeAndOauth2IdAndRowDeleteDateStr(
                        3,
                        response.body()!!.id.toString(),
                        "/"
                    )
            }

            else -> {
                classLogger.info("SNS Login Type ${inputVo.oauth2TypeCode} Not Supported")
                httpServletResponse.status = 400
                return null
            }
        }

        if (snsOauth2 == null) { // 가입된 회원이 없음
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "2")
            return null
        }

        // 계정 정지 검증
        val lockList =
            db1NativeRepository.findAllNowActivateMemberLockInfo(
                snsOauth2.service1MemberData.uid!!,
                LocalDateTime.now()
            )
        if (lockList.isNotEmpty()) {
            // 계정 정지 당한 상황
            val lockedOutputList: MutableList<MyServiceTkAuthController.LoginOutputVo.LockedOutput> =
                mutableListOf()
            for (lockInfo in lockList) {
                lockedOutputList.add(
                    MyServiceTkAuthController.LoginOutputVo.LockedOutput(
                        snsOauth2.service1MemberData.uid!!,
                        lockInfo.lockStart.atZone(ZoneId.systemDefault())
                            .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                        if (lockInfo.lockBefore == null) {
                            null
                        } else {
                            lockInfo.lockBefore!!.atZone(ZoneId.systemDefault())
                                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
                        },
                        lockInfo.lockReasonCode.toInt(),
                        lockInfo.lockReason
                    )
                )
            }

            httpServletResponse.status = HttpStatus.OK.value()
            return MyServiceTkAuthController.LoginOutputVo(
                null,
                lockedOutputList
            )
        }

        // 멤버의 권한 리스트를 조회 후 반환
        val memberRoleList =
            db1RaillyLinkerCompanyService1MemberRoleDataRepository.findAllByService1MemberDataAndRowDeleteDateStr(
                snsOauth2.service1MemberData,
                "/"
            )
        val roleList: ArrayList<String> = arrayListOf()
        for (memberRole in memberRoleList) {
            roleList.add(memberRole.role)
        }

        // (토큰 생성 로직 수행)
        // 멤버 고유번호로 엑세스 토큰 생성
        val jwtAccessToken = jwtTokenUtil.generateAccessToken(
            snsOauth2.service1MemberData.uid!!,
            AUTH_JWT_ACCESS_TOKEN_EXPIRATION_TIME_SEC,
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY,
            AUTH_JWT_ISSUER,
            AUTH_JWT_SECRET_KEY_STRING,
            roleList
        )

        val accessTokenExpireWhen = jwtTokenUtil.getExpirationDateTime(jwtAccessToken)

        // 액세스 토큰의 리프레시 토큰 생성 및 DB 저장 = 액세스 토큰에 대한 리프레시 토큰은 1개 혹은 0개
        val jwtRefreshToken = jwtTokenUtil.generateRefreshToken(
            snsOauth2.service1MemberData.uid!!,
            AUTH_JWT_REFRESH_TOKEN_EXPIRATION_TIME_SEC,
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY,
            AUTH_JWT_ISSUER,
            AUTH_JWT_SECRET_KEY_STRING
        )

        val refreshTokenExpireWhen = jwtTokenUtil.getExpirationDateTime(jwtRefreshToken)

        // 로그인 정보 저장
        db1RaillyLinkerCompanyService1LogInTokenHistoryRepository.save(
            Db1_RaillyLinkerCompany_Service1LogInTokenHistory(
                snsOauth2.service1MemberData,
                "Bearer",
                LocalDateTime.now(),
                jwtAccessToken,
                accessTokenExpireWhen,
                jwtRefreshToken,
                refreshTokenExpireWhen,
                null
            )
        )

        httpServletResponse.status = HttpStatus.OK.value()
        return MyServiceTkAuthController.LoginOutputVo(
            MyServiceTkAuthController.LoginOutputVo.LoggedInOutput(
                snsOauth2.service1MemberData.uid!!,
                "Bearer",
                jwtAccessToken,
                jwtRefreshToken,
                accessTokenExpireWhen.atZone(ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                refreshTokenExpireWhen.atZone(ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
            ),
            null
        )
    }


    ////
    @CustomTransactional([Db1MainConfig.TRANSACTION_NAME])
    override fun loginWithOAuth2IdToken(
        httpServletResponse: HttpServletResponse,
        inputVo: MyServiceTkAuthController.LoginWithOAuth2IdTokenInputVo
    ): MyServiceTkAuthController.LoginOutputVo? {
        val snsOauth2: Db1_RaillyLinkerCompany_Service1MemberOauth2LoginData?

        // (정보 검증 로직 수행)
        when (inputVo.oauth2TypeCode) {
            4 -> { // APPLE
                val appleInfo = appleOAuthHelperUtil.getAppleMemberData(inputVo.oauth2IdToken)

                val loginId: String
                if (appleInfo != null) {
                    loginId = appleInfo.snsId
                } else {
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "1")
                    return null
                }

                snsOauth2 =
                    db1RaillyLinkerCompanyService1MemberOauth2LoginDataRepository.findByOauth2TypeCodeAndOauth2IdAndRowDeleteDateStr(
                        4,
                        loginId,
                        "/"
                    )
            }

            else -> {
                classLogger.info("SNS Login Type ${inputVo.oauth2TypeCode} Not Supported")
                httpServletResponse.status = 400
                return null
            }
        }

        if (snsOauth2 == null) { // 가입된 회원이 없음
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "2")
            return null
        }

        // 계정 정지 검증
        val lockList =
            db1NativeRepository.findAllNowActivateMemberLockInfo(
                snsOauth2.service1MemberData.uid!!,
                LocalDateTime.now()
            )
        if (lockList.isNotEmpty()) {
            // 계정 정지 당한 상황
            val lockedOutputList: MutableList<MyServiceTkAuthController.LoginOutputVo.LockedOutput> =
                mutableListOf()
            for (lockInfo in lockList) {
                lockedOutputList.add(
                    MyServiceTkAuthController.LoginOutputVo.LockedOutput(
                        snsOauth2.service1MemberData.uid!!,
                        lockInfo.lockStart.atZone(ZoneId.systemDefault())
                            .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                        if (lockInfo.lockBefore == null) {
                            null
                        } else {
                            lockInfo.lockBefore!!.atZone(ZoneId.systemDefault())
                                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
                        },
                        lockInfo.lockReasonCode.toInt(),
                        lockInfo.lockReason
                    )
                )
            }

            httpServletResponse.status = HttpStatus.OK.value()
            return MyServiceTkAuthController.LoginOutputVo(
                null,
                lockedOutputList
            )
        }

        // 멤버의 권한 리스트를 조회 후 반환
        val memberRoleList =
            db1RaillyLinkerCompanyService1MemberRoleDataRepository.findAllByService1MemberDataAndRowDeleteDateStr(
                snsOauth2.service1MemberData,
                "/"
            )
        val roleList: ArrayList<String> = arrayListOf()
        for (userRole in memberRoleList) {
            roleList.add(userRole.role)
        }

        // (토큰 생성 로직 수행)
        // 멤버 고유번호로 엑세스 토큰 생성
        val jwtAccessToken = jwtTokenUtil.generateAccessToken(
            snsOauth2.service1MemberData.uid!!,
            AUTH_JWT_ACCESS_TOKEN_EXPIRATION_TIME_SEC,
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY,
            AUTH_JWT_ISSUER,
            AUTH_JWT_SECRET_KEY_STRING,
            roleList
        )

        val accessTokenExpireWhen = jwtTokenUtil.getExpirationDateTime(jwtAccessToken)

        // 액세스 토큰의 리프레시 토큰 생성 및 DB 저장 = 액세스 토큰에 대한 리프레시 토큰은 1개 혹은 0개
        val jwtRefreshToken = jwtTokenUtil.generateRefreshToken(
            snsOauth2.service1MemberData.uid!!,
            AUTH_JWT_REFRESH_TOKEN_EXPIRATION_TIME_SEC,
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY,
            AUTH_JWT_ISSUER,
            AUTH_JWT_SECRET_KEY_STRING
        )

        val refreshTokenExpireWhen = jwtTokenUtil.getExpirationDateTime(jwtRefreshToken)

        // 로그인 정보 저장
        db1RaillyLinkerCompanyService1LogInTokenHistoryRepository.save(
            Db1_RaillyLinkerCompany_Service1LogInTokenHistory(
                snsOauth2.service1MemberData,
                "Bearer",
                LocalDateTime.now(),
                jwtAccessToken,
                accessTokenExpireWhen,
                jwtRefreshToken,
                refreshTokenExpireWhen,
                null
            )
        )

        httpServletResponse.status = HttpStatus.OK.value()
        return MyServiceTkAuthController.LoginOutputVo(
            MyServiceTkAuthController.LoginOutputVo.LoggedInOutput(
                snsOauth2.service1MemberData.uid!!,
                "Bearer",
                jwtAccessToken,
                jwtRefreshToken,
                accessTokenExpireWhen.atZone(ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                refreshTokenExpireWhen.atZone(ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
            ),
            null
        )
    }


    ////
    @CustomTransactional([Db1MainConfig.TRANSACTION_NAME])
    override fun logout(authorization: String, httpServletResponse: HttpServletResponse) {
        val authorizationSplit = authorization.split(" ") // ex : ["Bearer", "qwer1234"]
        val token = authorizationSplit[1].trim() // (ex : "abcd1234")

        // 해당 멤버의 토큰 발행 정보 삭제
        val tokenType = authorizationSplit[0].trim().lowercase() // (ex : "bearer")

        val tokenInfo =
            db1RaillyLinkerCompanyService1LogInTokenHistoryRepository.findByTokenTypeAndAccessTokenAndLogoutDateAndRowDeleteDateStr(
                tokenType,
                token,
                null,
                "/"
            )

        if (tokenInfo != null) {
            tokenInfo.logoutDate = LocalDateTime.now()
            db1RaillyLinkerCompanyService1LogInTokenHistoryRepository.save(tokenInfo)

            // 토큰 만료처리
            val tokenType1 = tokenInfo.tokenType
            val accessToken = tokenInfo.accessToken

            val accessTokenExpireRemainSeconds = when (tokenType1) {
                "Bearer" -> {
                    jwtTokenUtil.getRemainSeconds(accessToken)
                }

                else -> {
                    null
                }
            }

            try {
                redis1Service1ForceExpireAuthorizationSet.saveKeyValue(
                    "${tokenType1}_${accessToken}",
                    Redis1_Map_Service1ForceExpireAuthorizationSet.ValueVo(),
                    accessTokenExpireRemainSeconds!! * 1000
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        httpServletResponse.status = HttpStatus.OK.value()
    }


    ////
    @CustomTransactional([Db1MainConfig.TRANSACTION_NAME])
    override fun reissueJwt(
        authorization: String?,
        inputVo: MyServiceTkAuthController.ReissueJwtInputVo,
        httpServletResponse: HttpServletResponse
    ): MyServiceTkAuthController.LoginOutputVo? {
        if (authorization == null) {
            // 올바르지 않은 Authorization Token
            httpServletResponse.setHeader("api-result-code", "3")
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            return null
        }

        val authorizationSplit = authorization.split(" ") // ex : ["Bearer", "qwer1234"]
        if (authorizationSplit.size < 2) {
            // 올바르지 않은 Authorization Token
            httpServletResponse.setHeader("api-result-code", "3")
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            return null
        }

        val accessTokenType = authorizationSplit[0].trim() // (ex : "bearer")
        val accessToken = authorizationSplit[1].trim() // (ex : "abcd1234")

        // 토큰 검증
        if (accessToken == "") {
            // 액세스 토큰이 비어있음 (올바르지 않은 Authorization Token)
            httpServletResponse.setHeader("api-result-code", "3")
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            return null
        }

        when (accessTokenType.lowercase()) { // 타입 검증
            "bearer" -> { // Bearer JWT 토큰 검증
                // 토큰 문자열 해석 가능여부 확인
                val accessTokenType1: String? = try {
                    jwtTokenUtil.getTokenType(accessToken)
                } catch (_: Exception) {
                    null
                }

                if (accessTokenType1 == null || // 해석 불가능한 JWT 토큰
                    accessTokenType1.lowercase() != "jwt" || // 토큰 타입이 JWT 가 아님
                    jwtTokenUtil.getTokenUsage(
                        accessToken,
                        AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
                        AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
                    ).lowercase() != "access" || // 토큰 용도가 다름
                    // 남은 시간이 최대 만료시간을 초과 (서버 기준이 변경되었을 때, 남은 시간이 더 많은 토큰을 견제하기 위한 처리)
                    jwtTokenUtil.getRemainSeconds(accessToken) > AUTH_JWT_ACCESS_TOKEN_EXPIRATION_TIME_SEC ||
                    jwtTokenUtil.getIssuer(accessToken) != AUTH_JWT_ISSUER || // 발행인 불일치
                    !jwtTokenUtil.validateSignature(
                        accessToken,
                        AUTH_JWT_SECRET_KEY_STRING
                    ) // 시크릿 검증이 무효 = 위변조 된 토큰
                ) {
                    // 올바르지 않은 Authorization Token
                    httpServletResponse.setHeader("api-result-code", "3")
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    return null
                }

                // 토큰 검증 정상 -> 데이터베이스 현 상태 확인

                // 유저 탈퇴 여부 확인
                val accessTokenMemberUid = jwtTokenUtil.getMemberUid(
                    accessToken,
                    AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
                    AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
                )
                val memberDataOpt = db1RaillyLinkerCompanyService1MemberDataRepository.findById(accessTokenMemberUid)

                if (memberDataOpt.isEmpty) {
                    // 멤버 탈퇴
                    httpServletResponse.setHeader("api-result-code", "4")
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    return null
                }

                val memberData = memberDataOpt.get()

                // 정지 여부 파악
                val lockList =
                    db1NativeRepository.findAllNowActivateMemberLockInfo(
                        memberData.uid!!,
                        LocalDateTime.now()
                    )
                if (lockList.isNotEmpty()) {
                    // 계정 정지 당한 상황
                    val lockedOutputList: MutableList<MyServiceTkAuthController.LoginOutputVo.LockedOutput> =
                        mutableListOf()
                    for (lockInfo in lockList) {
                        lockedOutputList.add(
                            MyServiceTkAuthController.LoginOutputVo.LockedOutput(
                                memberData.uid!!,
                                lockInfo.lockStart.atZone(ZoneId.systemDefault())
                                    .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                                if (lockInfo.lockBefore == null) {
                                    null
                                } else {
                                    lockInfo.lockBefore!!.atZone(ZoneId.systemDefault())
                                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
                                },
                                lockInfo.lockReasonCode.toInt(),
                                lockInfo.lockReason
                            )
                        )
                    }

                    httpServletResponse.status = HttpStatus.OK.value()
                    return MyServiceTkAuthController.LoginOutputVo(
                        null,
                        lockedOutputList
                    )
                }

                // 로그아웃 여부 파악
                val tokenInfo =
                    db1RaillyLinkerCompanyService1LogInTokenHistoryRepository.findByTokenTypeAndAccessTokenAndLogoutDateAndRowDeleteDateStr(
                        accessTokenType,
                        accessToken,
                        null,
                        "/"
                    )

                if (tokenInfo == null) {
                    // 로그아웃된 토큰
                    httpServletResponse.setHeader("api-result-code", "5")
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    return null
                }

                // 액세스 토큰 만료 외의 인증/인가 검증 완료

                // 리플레시 토큰 검증 시작
                // 타입과 토큰을 분리
                val refreshTokenInputSplit = inputVo.refreshToken.split(" ") // ex : ["Bearer", "qwer1234"]
                if (refreshTokenInputSplit.size < 2) {
                    // 올바르지 않은 Token
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "1")
                    return null
                }

                // 타입 분리
                val tokenType = refreshTokenInputSplit[0].trim() // 첫번째 단어는 토큰 타입
                val jwtRefreshToken = refreshTokenInputSplit[1].trim() // 앞의 타입을 자르고 남은 토큰

                if (jwtRefreshToken == "") {
                    // 토큰이 비어있음 (올바르지 않은 Authorization Token)
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "1")
                    return null
                }

                when (tokenType.lowercase()) { // 타입 검증
                    "bearer" -> { // Bearer JWT 토큰 검증
                        // 토큰 문자열 해석 가능여부 확인
                        val refreshTokenType: String? = try {
                            jwtTokenUtil.getTokenType(jwtRefreshToken)
                        } catch (_: Exception) {
                            null
                        }

                        // 리프레시 토큰 검증
                        if (refreshTokenType == null || // 해석 불가능한 리프레시 토큰
                            refreshTokenType.lowercase() != "jwt" || // 토큰 타입이 JWT 가 아닐 때
                            jwtTokenUtil.getTokenUsage(
                                jwtRefreshToken,
                                AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
                                AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
                            ).lowercase() != "refresh" || // 토큰 타입이 Refresh 토큰이 아닐 때
                            // 남은 시간이 최대 만료시간을 초과 (서버 기준이 변경되었을 때, 남은 시간이 더 많은 토큰을 견제하기 위한 처리)
                            jwtTokenUtil.getRemainSeconds(jwtRefreshToken) > AUTH_JWT_REFRESH_TOKEN_EXPIRATION_TIME_SEC ||
                            jwtTokenUtil.getIssuer(jwtRefreshToken) != AUTH_JWT_ISSUER || // 발행인이 다를 때
                            !jwtTokenUtil.validateSignature(
                                jwtRefreshToken,
                                AUTH_JWT_SECRET_KEY_STRING
                            ) || // 시크릿 검증이 유효하지 않을 때 = 위변조된 토큰
                            jwtTokenUtil.getMemberUid(
                                jwtRefreshToken,
                                AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
                                AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
                            ) != accessTokenMemberUid // 리프레시 토큰의 멤버 고유번호와 액세스 토큰 멤버 고유번호가 다를시
                        ) {
                            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                            httpServletResponse.setHeader("api-result-code", "1")
                            return null
                        }

                        if (jwtTokenUtil.getRemainSeconds(jwtRefreshToken) <= 0L) {
                            // 리플레시 토큰 만료
                            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                            httpServletResponse.setHeader("api-result-code", "2")
                            return null
                        }

                        if (jwtRefreshToken != tokenInfo.refreshToken) {
                            // 건내받은 토큰이 해당 액세스 토큰의 가용 토큰과 맞지 않음
                            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                            httpServletResponse.setHeader("api-result-code", "1")
                            return null
                        }

                        // 먼저 로그아웃 처리
                        tokenInfo.logoutDate = LocalDateTime.now()
                        db1RaillyLinkerCompanyService1LogInTokenHistoryRepository.save(tokenInfo)

                        // 토큰 만료처리
                        val tokenType1 = tokenInfo.tokenType
                        val accessToken1 = tokenInfo.accessToken

                        val accessTokenExpireRemainSeconds = when (tokenType1) {
                            "Bearer" -> {
                                jwtTokenUtil.getRemainSeconds(accessToken1)
                            }

                            else -> {
                                null
                            }
                        }

                        try {
                            redis1Service1ForceExpireAuthorizationSet.saveKeyValue(
                                "${tokenType1}_${accessToken1}",
                                Redis1_Map_Service1ForceExpireAuthorizationSet.ValueVo(),
                                accessTokenExpireRemainSeconds!! * 1000
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        // 멤버의 권한 리스트를 조회 후 반환
                        val memberRoleList =
                            db1RaillyLinkerCompanyService1MemberRoleDataRepository.findAllByService1MemberDataAndRowDeleteDateStr(
                                tokenInfo.service1MemberData,
                                "/"
                            )
                        val roleList: ArrayList<String> = arrayListOf()
                        for (userRole in memberRoleList) {
                            roleList.add(userRole.role)
                        }

                        // 새 토큰 생성 및 로그인 처리
                        val newJwtAccessToken = jwtTokenUtil.generateAccessToken(
                            accessTokenMemberUid,
                            AUTH_JWT_ACCESS_TOKEN_EXPIRATION_TIME_SEC,
                            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
                            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY,
                            AUTH_JWT_ISSUER,
                            AUTH_JWT_SECRET_KEY_STRING,
                            roleList
                        )

                        val accessTokenExpireWhen = jwtTokenUtil.getExpirationDateTime(newJwtAccessToken)

                        val newRefreshToken = jwtTokenUtil.generateRefreshToken(
                            accessTokenMemberUid,
                            AUTH_JWT_REFRESH_TOKEN_EXPIRATION_TIME_SEC,
                            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
                            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY,
                            AUTH_JWT_ISSUER,
                            AUTH_JWT_SECRET_KEY_STRING
                        )

                        val refreshTokenExpireWhen = jwtTokenUtil.getExpirationDateTime(newRefreshToken)

                        // 로그인 정보 저장
                        db1RaillyLinkerCompanyService1LogInTokenHistoryRepository.save(
                            Db1_RaillyLinkerCompany_Service1LogInTokenHistory(
                                tokenInfo.service1MemberData,
                                "Bearer",
                                LocalDateTime.now(),
                                newJwtAccessToken,
                                accessTokenExpireWhen,
                                newRefreshToken,
                                refreshTokenExpireWhen,
                                null
                            )
                        )

                        httpServletResponse.status = HttpStatus.OK.value()
                        return MyServiceTkAuthController.LoginOutputVo(
                            MyServiceTkAuthController.LoginOutputVo.LoggedInOutput(
                                memberData.uid!!,
                                "Bearer",
                                newJwtAccessToken,
                                newRefreshToken,
                                accessTokenExpireWhen.atZone(ZoneId.systemDefault())
                                    .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z")),
                                refreshTokenExpireWhen.atZone(ZoneId.systemDefault())
                                    .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
                            ),
                            null
                        )
                    }

                    else -> {
                        // 지원하지 않는 토큰 타입 (올바르지 않은 Authorization Token)
                        httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                        httpServletResponse.setHeader("api-result-code", "1")
                        return null
                    }
                }
            }

            else -> {
                // 올바르지 않은 Authorization Token
                httpServletResponse.setHeader("api-result-code", "3")
                httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                return null
            }
        }
    }


    ////
    @CustomTransactional([Db1MainConfig.TRANSACTION_NAME])
    override fun deleteAllJwtOfAMember(authorization: String, httpServletResponse: HttpServletResponse) {
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )
        val memberData = db1RaillyLinkerCompanyService1MemberDataRepository.findById(memberUid).get()

        // loginAccessToken 의 Iterable 가져오기
        val tokenInfoList =
            db1RaillyLinkerCompanyService1LogInTokenHistoryRepository.findAllByService1MemberDataAndLogoutDateAndRowDeleteDateStr(
                memberData,
                null,
                "/"
            )

        // 발행되었던 모든 액세스 토큰 무효화 (다른 디바이스에선 사용중 로그아웃된 것과 동일한 효과)
        for (tokenInfo in tokenInfoList) {
            tokenInfo.logoutDate = LocalDateTime.now()
            db1RaillyLinkerCompanyService1LogInTokenHistoryRepository.save(tokenInfo)

            // 토큰 만료처리
            val tokenType = tokenInfo.tokenType
            val accessToken = tokenInfo.accessToken

            val accessTokenExpireRemainSeconds = when (tokenType) {
                "Bearer" -> {
                    jwtTokenUtil.getRemainSeconds(accessToken)
                }

                else -> {
                    null
                }
            }

            try {
                redis1Service1ForceExpireAuthorizationSet.saveKeyValue(
                    "${tokenType}_${accessToken}",
                    Redis1_Map_Service1ForceExpireAuthorizationSet.ValueVo(),
                    accessTokenExpireRemainSeconds!! * 1000
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        httpServletResponse.status = HttpStatus.OK.value()
    }


    ////
    override fun getMemberInfo(
        httpServletResponse: HttpServletResponse,
        authorization: String
    ): MyServiceTkAuthController.GetMemberInfoOutputVo? {
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )
        val memberData = db1RaillyLinkerCompanyService1MemberDataRepository.findById(memberUid).get()

        // 멤버의 권한 리스트를 조회 후 반환
        val memberRoleList =
            db1RaillyLinkerCompanyService1MemberRoleDataRepository.findAllByService1MemberDataAndRowDeleteDateStr(
                memberData,
                "/"
            )

        val roleList: ArrayList<String> = arrayListOf()
        for (userRole in memberRoleList) {
            roleList.add(userRole.role)
        }

        val profileData =
            db1RaillyLinkerCompanyService1MemberProfileDataRepository.findAllByService1MemberDataAndRowDeleteDateStr(
                memberData,
                "/"
            )
        val myProfileList: ArrayList<MyServiceTkAuthController.GetMemberInfoOutputVo.ProfileInfo> =
            arrayListOf()
        for (profile in profileData) {
            myProfileList.add(
                MyServiceTkAuthController.GetMemberInfoOutputVo.ProfileInfo(
                    profile.uid!!,
                    profile.imageFullUrl,
                    profile.uid == memberData.frontService1MemberProfileData?.uid
                )
            )
        }

        val emailEntityList =
            db1RaillyLinkerCompanyService1MemberEmailDataRepository.findAllByService1MemberDataAndRowDeleteDateStr(
                memberData,
                "/"
            )
        val myEmailList: ArrayList<MyServiceTkAuthController.GetMemberInfoOutputVo.EmailInfo> =
            arrayListOf()
        for (emailEntity in emailEntityList) {
            myEmailList.add(
                MyServiceTkAuthController.GetMemberInfoOutputVo.EmailInfo(
                    emailEntity.uid!!,
                    emailEntity.emailAddress,
                    emailEntity.uid == memberData.frontService1MemberEmailData?.uid
                )
            )
        }

        val phoneEntityList =
            db1RaillyLinkerCompanyService1MemberPhoneDataRepository.findAllByService1MemberDataAndRowDeleteDateStr(
                memberData,
                "/"
            )
        val myPhoneNumberList: ArrayList<MyServiceTkAuthController.GetMemberInfoOutputVo.PhoneNumberInfo> =
            arrayListOf()
        for (phoneEntity in phoneEntityList) {
            myPhoneNumberList.add(
                MyServiceTkAuthController.GetMemberInfoOutputVo.PhoneNumberInfo(
                    phoneEntity.uid!!,
                    phoneEntity.phoneNumber,
                    phoneEntity.uid == memberData.frontService1MemberPhoneData?.uid
                )
            )
        }

        val oAuth2EntityList =
            db1RaillyLinkerCompanyService1MemberOauth2LoginDataRepository.findAllByService1MemberDataAndRowDeleteDateStr(
                memberData,
                "/"
            )
        val myOAuth2List = ArrayList<MyServiceTkAuthController.GetMemberInfoOutputVo.OAuth2Info>()
        for (oAuth2Entity in oAuth2EntityList) {
            myOAuth2List.add(
                MyServiceTkAuthController.GetMemberInfoOutputVo.OAuth2Info(
                    oAuth2Entity.uid!!,
                    oAuth2Entity.oauth2TypeCode.toInt(),
                    oAuth2Entity.oauth2Id
                )
            )
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return MyServiceTkAuthController.GetMemberInfoOutputVo(
            memberData.accountId,
            roleList,
            myOAuth2List,
            myProfileList,
            myEmailList,
            myPhoneNumberList,
            memberData.accountPassword == null
        )
    }


    ////
    override fun checkIdDuplicate(
        httpServletResponse: HttpServletResponse,
        id: String
    ): MyServiceTkAuthController.CheckIdDuplicateOutputVo? {
        httpServletResponse.status = HttpStatus.OK.value()
        return MyServiceTkAuthController.CheckIdDuplicateOutputVo(
            db1RaillyLinkerCompanyService1MemberDataRepository.existsByAccountIdAndRowDeleteDateStr(id.trim(), "/")
        )
    }


    ////
    @CustomTransactional([Db1MainConfig.TRANSACTION_NAME])
    override fun updateId(httpServletResponse: HttpServletResponse, authorization: String, id: String) {
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )
        val memberData = db1RaillyLinkerCompanyService1MemberDataRepository.findById(memberUid).get()

        if (db1RaillyLinkerCompanyService1MemberDataRepository.existsByAccountIdAndRowDeleteDateStr(id, "/")) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        memberData.accountId = id
        db1RaillyLinkerCompanyService1MemberDataRepository.save(
            memberData
        )

        httpServletResponse.status = HttpStatus.OK.value()
    }


    ////
    @CustomTransactional([Db1MainConfig.TRANSACTION_NAME])
    override fun joinTheMembershipForTest(
        httpServletResponse: HttpServletResponse,
        inputVo: MyServiceTkAuthController.JoinTheMembershipForTestInputVo
    ) {
        if (inputVo.apiSecret != "aadke234!@") {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        if (db1RaillyLinkerCompanyService1MemberDataRepository.existsByAccountIdAndRowDeleteDateStr(
                inputVo.id.trim(),
                "/"
            )
        ) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "2")
            return
        }

        if (inputVo.email != null) {
            val isUserExists =
                db1RaillyLinkerCompanyService1MemberEmailDataRepository.existsByEmailAddressAndRowDeleteDateStr(
                    inputVo.email,
                    "/"
                )
            if (isUserExists) { // 기존 회원이 있을 때
                httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                httpServletResponse.setHeader("api-result-code", "3")
                return
            }
        }

        if (inputVo.phoneNumber != null) {
            val isUserExists =
                db1RaillyLinkerCompanyService1MemberPhoneDataRepository.existsByPhoneNumberAndRowDeleteDateStr(
                    inputVo.phoneNumber,
                    "/"
                )
            if (isUserExists) { // 기존 회원이 있을 때
                httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                httpServletResponse.setHeader("api-result-code", "4")
                return
            }
        }

        val password = passwordEncoder.encode(inputVo.password)!! // 비밀번호 암호화

        // 회원가입
        val memberEntity = db1RaillyLinkerCompanyService1MemberDataRepository.save(
            Db1_RaillyLinkerCompany_Service1MemberData(
                inputVo.id,
                password,
                null,
                null,
                null
            )
        )

        // 역할 저장
//        val memberRoleList = ArrayList<Database1_Service1_MemberRoleData>()
//        // 필요하다면 기본 권한 추가
//        memberRoleList.add(
//            Database1_Service1_MemberRoleData(
//                memberEntity,
//                "ROLE_USER"
//            )
//        )
//        database1Service1MemberRoleDataRepository.saveAll(memberRoleList)

        if (inputVo.profileImageFile != null) {
            // 저장된 프로필 이미지 파일을 다운로드 할 수 있는 URL
            val savedProfileImageUrl: String

            // 프로필 이미지 파일 저장

            //----------------------------------------------------------------------------------------------------------
            // 프로필 이미지를 서버 스토리지에 저장할 때 사용하는 방식
            // 파일 저장 기본 디렉토리 경로
            val saveDirectoryPath: Path =
                Paths.get("./by_product_files/member/profile").toAbsolutePath().normalize()

            // 파일 저장 기본 디렉토리 생성
            Files.createDirectories(saveDirectoryPath)

            // 원본 파일명(with suffix)
            val multiPartFileNameString = StringUtils.cleanPath(inputVo.profileImageFile.originalFilename!!)

            // 파일 확장자 구분 위치
            val fileExtensionSplitIdx = multiPartFileNameString.lastIndexOf('.')

            // 확장자가 없는 파일명
            val fileNameWithOutExtension: String
            // 확장자
            val fileExtension: String

            if (fileExtensionSplitIdx == -1) {
                fileNameWithOutExtension = multiPartFileNameString
                fileExtension = ""
            } else {
                fileNameWithOutExtension = multiPartFileNameString.substring(0, fileExtensionSplitIdx)
                fileExtension =
                    multiPartFileNameString.substring(fileExtensionSplitIdx + 1, multiPartFileNameString.length)
            }

            val savedFileName = "${fileNameWithOutExtension}(${
                LocalDateTime.now().atZone(ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
            }).$fileExtension"

            // multipartFile 을 targetPath 에 저장
            inputVo.profileImageFile.transferTo(
                // 파일 저장 경로와 파일명(with index) 을 합친 path 객체
                saveDirectoryPath.resolve(savedFileName).normalize()
            )

            savedProfileImageUrl = "${externalAccessAddress}/service1/tk/v1/auth/member-profile/$savedFileName"
            //----------------------------------------------------------------------------------------------------------

            val memberProfileData =
                db1RaillyLinkerCompanyService1MemberProfileDataRepository.save(
                    Db1_RaillyLinkerCompany_Service1MemberProfileData(
                        memberEntity,
                        savedProfileImageUrl
                    )
                )

            memberEntity.frontService1MemberProfileData = memberProfileData
        }

        if (inputVo.email != null) {
            // 이메일 저장
            val memberEmailData =
                db1RaillyLinkerCompanyService1MemberEmailDataRepository.save(
                    Db1_RaillyLinkerCompany_Service1MemberEmailData(
                        memberEntity,
                        inputVo.email
                    )
                )

            memberEntity.frontService1MemberEmailData = memberEmailData
        }

        if (inputVo.phoneNumber != null) {
            // 전화번호 저장
            val memberPhoneData =
                db1RaillyLinkerCompanyService1MemberPhoneDataRepository.save(
                    Db1_RaillyLinkerCompany_Service1MemberPhoneData(
                        memberEntity,
                        inputVo.phoneNumber
                    )
                )

            memberEntity.frontService1MemberPhoneData = memberPhoneData
        }

        db1RaillyLinkerCompanyService1MemberDataRepository.save(memberEntity)

        httpServletResponse.status = HttpStatus.OK.value()
        return
    }


    ////
    @CustomTransactional([Db1MainConfig.TRANSACTION_NAME])
    override fun sendEmailVerificationForJoin(
        httpServletResponse: HttpServletResponse,
        inputVo: MyServiceTkAuthController.SendEmailVerificationForJoinInputVo
    ): MyServiceTkAuthController.SendEmailVerificationForJoinOutputVo? {
        // 입력 데이터 검증
        val memberExists =
            db1RaillyLinkerCompanyService1MemberEmailDataRepository.existsByEmailAddressAndRowDeleteDateStr(
                inputVo.email,
                "/"
            )

        if (memberExists) { // 기존 회원 존재
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return null
        }

        // 정보 저장 후 이메일 발송
        val verificationTimeSec: Long = 60 * 10
        val verificationCode = String.format("%06d", Random().nextInt(999999)) // 랜덤 6자리 숫자
        val memberRegisterEmailVerificationData =
            db1RaillyLinkerCompanyService1JoinTheMembershipWithEmailVerificationDataRepository.save(
                Db1_RaillyLinkerCompany_Service1JoinTheMembershipWithEmailVerificationData(
                    inputVo.email,
                    verificationCode,
                    LocalDateTime.now().plusSeconds(verificationTimeSec)
                )
            )

        emailSender.sendThymeLeafHtmlMail(
            "Springboot Mvc Project Template",
            arrayOf(inputVo.email),
            null,
            "Springboot Mvc Project Template 회원가입 - 본인 계정 확인용 이메일입니다.",
            "send_email_verification_for_join/email_verification_email",
            hashMapOf(
                Pair("verificationCode", verificationCode)
            ),
            null,
            null,
            null,
            null
        )

        httpServletResponse.status = HttpStatus.OK.value()
        return MyServiceTkAuthController.SendEmailVerificationForJoinOutputVo(
            memberRegisterEmailVerificationData.uid!!,
            memberRegisterEmailVerificationData.verificationExpireWhen.atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
        )
    }


    ////
    override fun checkEmailVerificationForJoin(
        httpServletResponse: HttpServletResponse,
        verificationUid: Long,
        email: String,
        verificationCode: String
    ) {
        val emailVerificationOpt =
            db1RaillyLinkerCompanyService1JoinTheMembershipWithEmailVerificationDataRepository.findById(verificationUid)

        if (emailVerificationOpt.isEmpty) { // 해당 이메일 검증을 요청한적이 없음
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        val emailVerification = emailVerificationOpt.get()

        if (emailVerification.emailAddress != email) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        if (LocalDateTime.now().isAfter(emailVerification.verificationExpireWhen)) {
            // 만료됨
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "2")
            return
        }

        // 입력 코드와 발급된 코드와의 매칭
        if (emailVerification.verificationSecret == verificationCode) {
            // 코드 일치
            httpServletResponse.status = HttpStatus.OK.value()
        } else {
            // 코드 불일치
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "3")
        }
    }


    ////
    @CustomTransactional([Db1MainConfig.TRANSACTION_NAME])
    override fun joinTheMembershipWithEmail(
        httpServletResponse: HttpServletResponse,
        inputVo: MyServiceTkAuthController.JoinTheMembershipWithEmailInputVo
    ) {
        val emailVerificationOpt =
            db1RaillyLinkerCompanyService1JoinTheMembershipWithEmailVerificationDataRepository.findById(inputVo.verificationUid)

        if (emailVerificationOpt.isEmpty) { // 해당 이메일 검증을 요청한적이 없음
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        val emailVerification = emailVerificationOpt.get()

        if (emailVerification.emailAddress != inputVo.email) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        if (LocalDateTime.now().isAfter(emailVerification.verificationExpireWhen)) {
            // 만료됨
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "2")
            return
        }

        // 입력 코드와 발급된 코드와의 매칭
        if (emailVerification.verificationSecret == inputVo.verificationCode) { // 코드 일치
            val isUserExists =
                db1RaillyLinkerCompanyService1MemberEmailDataRepository.existsByEmailAddressAndRowDeleteDateStr(
                    inputVo.email,
                    "/"
                )
            if (isUserExists) { // 기존 회원이 있을 때
                httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                httpServletResponse.setHeader("api-result-code", "4")
                return
            }

            if (db1RaillyLinkerCompanyService1MemberDataRepository.existsByAccountIdAndRowDeleteDateStr(
                    inputVo.id.trim(),
                    "/"
                )
            ) {
                httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                httpServletResponse.setHeader("api-result-code", "5")
                return
            }

            val password = passwordEncoder.encode(inputVo.password)!! // 비밀번호 암호화

            // 회원가입
            val memberData = db1RaillyLinkerCompanyService1MemberDataRepository.save(
                Db1_RaillyLinkerCompany_Service1MemberData(
                    inputVo.id,
                    password,
                    null,
                    null,
                    null
                )
            )

            // 이메일 저장
            val memberEmailData = db1RaillyLinkerCompanyService1MemberEmailDataRepository.save(
                Db1_RaillyLinkerCompany_Service1MemberEmailData(
                    memberData,
                    inputVo.email
                )
            )

            memberData.frontService1MemberEmailData = memberEmailData

            // 역할 저장
//            val memberUserRoleList = ArrayList<Database1_Service1_MemberRoleData>()
//            // 기본 권한 추가
//            memberUserRoleList.add(
//                Database1_Service1_MemberRoleData(
//                    memberData,
//                    "ROLE_USER"
//                )
//            )
//            database1Service1MemberRoleDataRepository.saveAll(memberUserRoleList)

            if (inputVo.profileImageFile != null) {
                // 저장된 프로필 이미지 파일을 다운로드 할 수 있는 URL
                val savedProfileImageUrl: String

                // 프로필 이미지 파일 저장

                //----------------------------------------------------------------------------------------------------------
                // 프로필 이미지를 서버 스토리지에 저장할 때 사용하는 방식
                // 파일 저장 기본 디렉토리 경로
                val saveDirectoryPath: Path =
                    Paths.get("./by_product_files/member/profile").toAbsolutePath().normalize()

                // 파일 저장 기본 디렉토리 생성
                Files.createDirectories(saveDirectoryPath)

                // 원본 파일명(with suffix)
                val multiPartFileNameString = StringUtils.cleanPath(inputVo.profileImageFile.originalFilename!!)

                // 파일 확장자 구분 위치
                val fileExtensionSplitIdx = multiPartFileNameString.lastIndexOf('.')

                // 확장자가 없는 파일명
                val fileNameWithOutExtension: String
                // 확장자
                val fileExtension: String

                if (fileExtensionSplitIdx == -1) {
                    fileNameWithOutExtension = multiPartFileNameString
                    fileExtension = ""
                } else {
                    fileNameWithOutExtension = multiPartFileNameString.substring(0, fileExtensionSplitIdx)
                    fileExtension =
                        multiPartFileNameString.substring(fileExtensionSplitIdx + 1, multiPartFileNameString.length)
                }

                val savedFileName = "${fileNameWithOutExtension}(${
                    LocalDateTime.now().atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
                }).$fileExtension"

                // multipartFile 을 targetPath 에 저장
                inputVo.profileImageFile.transferTo(
                    // 파일 저장 경로와 파일명(with index) 을 합친 path 객체
                    saveDirectoryPath.resolve(savedFileName).normalize()
                )

                savedProfileImageUrl = "${externalAccessAddress}/service1/tk/v1/auth/member-profile/$savedFileName"
                //----------------------------------------------------------------------------------------------------------

                val memberProfileData =
                    db1RaillyLinkerCompanyService1MemberProfileDataRepository.save(
                        Db1_RaillyLinkerCompany_Service1MemberProfileData(
                            memberData,
                            savedProfileImageUrl
                        )
                    )

                memberData.frontService1MemberProfileData = memberProfileData
            }

            db1RaillyLinkerCompanyService1MemberDataRepository.save(memberData)

            // 확인 완료된 검증 요청 정보 삭제
            db1RaillyLinkerCompanyService1JoinTheMembershipWithEmailVerificationDataRepository.deleteById(
                emailVerification.uid!!
            )

            httpServletResponse.status = HttpStatus.OK.value()
            return
        } else { // 코드 불일치
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "3")
            return
        }
    }


    ////
    @CustomTransactional([Db1MainConfig.TRANSACTION_NAME])
    override fun sendPhoneVerificationForJoin(
        httpServletResponse: HttpServletResponse,
        inputVo: MyServiceTkAuthController.SendPhoneVerificationForJoinInputVo
    ): MyServiceTkAuthController.SendPhoneVerificationForJoinOutputVo? {
        // 입력 데이터 검증
        val memberExists =
            db1RaillyLinkerCompanyService1MemberPhoneDataRepository.existsByPhoneNumberAndRowDeleteDateStr(
                inputVo.phoneNumber,
                "/"
            )

        if (memberExists) { // 기존 회원 존재
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return null
        }

        // 정보 저장 후 발송
        val verificationTimeSec: Long = 60 * 10
        val verificationCode = String.format("%06d", Random().nextInt(999999)) // 랜덤 6자리 숫자
        val memberRegisterPhoneNumberVerificationData =
            db1RaillyLinkerCompanyService1JoinTheMembershipWithPhoneNumberVerificationDataRepository.save(
                Db1_RaillyLinkerCompany_Service1JoinTheMembershipWithPhoneNumberVerificationData(
                    inputVo.phoneNumber,
                    verificationCode,
                    LocalDateTime.now().plusSeconds(verificationTimeSec)
                )
            )

        val phoneNumberSplit = inputVo.phoneNumber.split(")") // ["82", "010-0000-0000"]

        // 국가 코드 (ex : 82)
        val countryCode = phoneNumberSplit[0]

        // 전화번호 (ex : "01000000000")
        val phoneNumber = (phoneNumberSplit[1].replace("-", "")).replace(" ", "")

        val sendSmsResult = naverSmsSenderComponent.sendSms(
            NaverSmsSenderComponent.SendSmsInputVo(
                "SMS",
                countryCode,
                phoneNumber,
                "[Springboot Mvc Project Template - 회원가입] 인증번호 [${verificationCode}]"
            )
        )

        if (!sendSmsResult) {
            throw Exception()
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return MyServiceTkAuthController.SendPhoneVerificationForJoinOutputVo(
            memberRegisterPhoneNumberVerificationData.uid!!,
            memberRegisterPhoneNumberVerificationData.verificationExpireWhen.atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
        )
    }


    ////
    override fun checkPhoneVerificationForJoin(
        httpServletResponse: HttpServletResponse,
        verificationUid: Long,
        phoneNumber: String,
        verificationCode: String
    ) {
        val phoneNumberVerificationOpt =
            db1RaillyLinkerCompanyService1JoinTheMembershipWithPhoneNumberVerificationDataRepository.findById(
                verificationUid
            )

        if (phoneNumberVerificationOpt.isEmpty) { // 해당 이메일 검증을 요청한적이 없음
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        val phoneNumberVerification = phoneNumberVerificationOpt.get()

        if (phoneNumberVerification.phoneNumber != phoneNumber) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        if (LocalDateTime.now().isAfter(phoneNumberVerification.verificationExpireWhen)) {
            // 만료됨
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "2")
            return
        }

        // 입력 코드와 발급된 코드와의 매칭
        if (phoneNumberVerification.verificationSecret == verificationCode) {
            // 코드 일치
            httpServletResponse.status = HttpStatus.OK.value()
        } else {
            // 코드 불일치
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "3")
        }
    }


    ////
    @CustomTransactional([Db1MainConfig.TRANSACTION_NAME])
    override fun joinTheMembershipWithPhoneNumber(
        httpServletResponse: HttpServletResponse,
        inputVo: MyServiceTkAuthController.JoinTheMembershipWithPhoneNumberInputVo
    ) {
        val phoneNumberVerificationOpt =
            db1RaillyLinkerCompanyService1JoinTheMembershipWithPhoneNumberVerificationDataRepository.findById(inputVo.verificationUid)

        if (phoneNumberVerificationOpt.isEmpty) { // 해당 이메일 검증을 요청한적이 없음
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        val phoneNumberVerification = phoneNumberVerificationOpt.get()

        if (phoneNumberVerification.phoneNumber != inputVo.phoneNumber) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        if (LocalDateTime.now().isAfter(phoneNumberVerification.verificationExpireWhen)) {
            // 만료됨
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "2")
            return
        }

        // 입력 코드와 발급된 코드와의 매칭
        if (phoneNumberVerification.verificationSecret == inputVo.verificationCode) { // 코드 일치
            val isUserExists =
                db1RaillyLinkerCompanyService1MemberPhoneDataRepository.existsByPhoneNumberAndRowDeleteDateStr(
                    inputVo.phoneNumber,
                    "/"
                )
            if (isUserExists) { // 기존 회원이 있을 때
                httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                httpServletResponse.setHeader("api-result-code", "4")
                return
            }

            if (db1RaillyLinkerCompanyService1MemberDataRepository.existsByAccountIdAndRowDeleteDateStr(
                    inputVo.id.trim(),
                    "/"
                )
            ) {
                httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                httpServletResponse.setHeader("api-result-code", "5")
                return
            }

            val password: String = passwordEncoder.encode(inputVo.password)!! // 비밀번호 암호화

            // 회원가입
            val memberUser = db1RaillyLinkerCompanyService1MemberDataRepository.save(
                Db1_RaillyLinkerCompany_Service1MemberData(
                    inputVo.id,
                    password,
                    null,
                    null,
                    null
                )
            )

            // 전화번호 저장
            val memberPhoneData =
                db1RaillyLinkerCompanyService1MemberPhoneDataRepository.save(
                    Db1_RaillyLinkerCompany_Service1MemberPhoneData(
                        memberUser,
                        inputVo.phoneNumber
                    )
                )

            memberUser.frontService1MemberPhoneData = memberPhoneData

            // 역할 저장
//            val memberUserRoleList = ArrayList<Database1_Service1_MemberRoleData>()
//            // 기본 권한 추가
//            memberUserRoleList.add(
//                Database1_Service1_MemberRoleData(
//                    memberUser,
//                    "ROLE_USER"
//                )
//            )
//            database1Service1MemberRoleDataRepository.saveAll(memberUserRoleList)

            if (inputVo.profileImageFile != null) {
                // 저장된 프로필 이미지 파일을 다운로드 할 수 있는 URL
                val savedProfileImageUrl: String

                // 프로필 이미지 파일 저장

                //----------------------------------------------------------------------------------------------------------
                // 프로필 이미지를 서버 스토리지에 저장할 때 사용하는 방식
                // 파일 저장 기본 디렉토리 경로
                val saveDirectoryPath: Path =
                    Paths.get("./by_product_files/member/profile").toAbsolutePath().normalize()

                // 파일 저장 기본 디렉토리 생성
                Files.createDirectories(saveDirectoryPath)

                // 원본 파일명(with suffix)
                val multiPartFileNameString = StringUtils.cleanPath(inputVo.profileImageFile.originalFilename!!)

                // 파일 확장자 구분 위치
                val fileExtensionSplitIdx = multiPartFileNameString.lastIndexOf('.')

                // 확장자가 없는 파일명
                val fileNameWithOutExtension: String
                // 확장자
                val fileExtension: String

                if (fileExtensionSplitIdx == -1) {
                    fileNameWithOutExtension = multiPartFileNameString
                    fileExtension = ""
                } else {
                    fileNameWithOutExtension = multiPartFileNameString.substring(0, fileExtensionSplitIdx)
                    fileExtension =
                        multiPartFileNameString.substring(fileExtensionSplitIdx + 1, multiPartFileNameString.length)
                }

                val savedFileName = "${fileNameWithOutExtension}(${
                    LocalDateTime.now().atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
                }).$fileExtension"

                // multipartFile 을 targetPath 에 저장
                inputVo.profileImageFile.transferTo(
                    // 파일 저장 경로와 파일명(with index) 을 합친 path 객체
                    saveDirectoryPath.resolve(savedFileName).normalize()
                )

                savedProfileImageUrl = "${externalAccessAddress}/service1/tk/v1/auth/member-profile/$savedFileName"
                //----------------------------------------------------------------------------------------------------------

                val memberProfileData = db1RaillyLinkerCompanyService1MemberProfileDataRepository.save(
                    Db1_RaillyLinkerCompany_Service1MemberProfileData(
                        memberUser,
                        savedProfileImageUrl
                    )
                )

                memberUser.frontService1MemberProfileData = memberProfileData
            }

            db1RaillyLinkerCompanyService1MemberDataRepository.save(memberUser)

            // 확인 완료된 검증 요청 정보 삭제
            db1RaillyLinkerCompanyService1JoinTheMembershipWithPhoneNumberVerificationDataRepository.deleteById(
                phoneNumberVerification.uid!!
            )

            httpServletResponse.status = HttpStatus.OK.value()
            return
        } else { // 코드 불일치
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "3")
            return
        }
    }


    ////
    @CustomTransactional([Db1MainConfig.TRANSACTION_NAME])
    override fun checkOauth2AccessTokenVerificationForJoin(
        httpServletResponse: HttpServletResponse,
        inputVo: MyServiceTkAuthController.CheckOauth2AccessTokenVerificationForJoinInputVo
    ): MyServiceTkAuthController.CheckOauth2AccessTokenVerificationForJoinOutputVo? {
        val verificationUid: Long
        val verificationCode: String
        val expireWhen: String
        val loginId: String

        val verificationTimeSec: Long = 60 * 10
        // (정보 검증 로직 수행)
        when (inputVo.oauth2TypeCode) {
            1 -> { // GOOGLE
                // 클라이언트에서 받은 access 토큰으로 멤버 정보 요청
                val response = networkRetrofit2.wwwGoogleapisComRequestApi.getOauth2V1UserInfo(
                    inputVo.oauth2AccessToken
                ).execute()

                // 액세트 토큰 정상 동작 확인
                if (response.code() != 200 ||
                    response.body() == null
                ) {
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "1")
                    return null
                }

                loginId = response.body()!!.id!!

                val memberExists =
                    db1RaillyLinkerCompanyService1MemberOauth2LoginDataRepository.existsByOauth2TypeCodeAndOauth2IdAndRowDeleteDateStr(
                        1,
                        loginId,
                        "/"
                    )

                if (memberExists) { // 기존 회원 존재
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "2")
                    return null
                }

                verificationCode = String.format("%06d", Random().nextInt(999999)) // 랜덤 6자리 숫자
                val memberRegisterOauth2VerificationData =
                    db1RaillyLinkerCompanyService1JoinTheMembershipWithOauth2VerificationDataRepository.save(
                        Db1_RaillyLinkerCompany_Service1JoinTheMembershipWithOauth2VerificationData(
                            1,
                            loginId,
                            verificationCode,
                            LocalDateTime.now().plusSeconds(verificationTimeSec)
                        )
                    )

                verificationUid = memberRegisterOauth2VerificationData.uid!!

                expireWhen =
                    memberRegisterOauth2VerificationData.verificationExpireWhen.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
            }

            2 -> { // NAVER
                // 클라이언트에서 받은 access 토큰으로 멤버 정보 요청
                val response = networkRetrofit2.openapiNaverComRequestApi.getV1NidMe(
                    inputVo.oauth2AccessToken
                ).execute()

                // 액세트 토큰 정상 동작 확인
                if (response.code() != 200 ||
                    response.body() == null
                ) {
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "1")
                    return null
                }

                loginId = response.body()!!.response.id

                val memberExists =
                    db1RaillyLinkerCompanyService1MemberOauth2LoginDataRepository.existsByOauth2TypeCodeAndOauth2IdAndRowDeleteDateStr(
                        2,
                        loginId,
                        "/"
                    )

                if (memberExists) { // 기존 회원 존재
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "2")
                    return null
                }

                verificationCode = String.format("%06d", Random().nextInt(999999)) // 랜덤 6자리 숫자
                val memberRegisterOauth2VerificationData =
                    db1RaillyLinkerCompanyService1JoinTheMembershipWithOauth2VerificationDataRepository.save(
                        Db1_RaillyLinkerCompany_Service1JoinTheMembershipWithOauth2VerificationData(
                            2,
                            loginId,
                            verificationCode,
                            LocalDateTime.now().plusSeconds(verificationTimeSec)
                        )
                    )

                verificationUid = memberRegisterOauth2VerificationData.uid!!

                expireWhen =
                    memberRegisterOauth2VerificationData.verificationExpireWhen.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
            }

            3 -> { // KAKAO TALK
                // 클라이언트에서 받은 access 토큰으로 멤버 정보 요청
                val response = networkRetrofit2.kapiKakaoComRequestApi.getV2UserMe(
                    inputVo.oauth2AccessToken
                ).execute()

                // 액세트 토큰 정상 동작 확인
                if (response.code() != 200 ||
                    response.body() == null
                ) {
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "1")
                    return null
                }

                loginId = response.body()!!.id.toString()

                val memberExists =
                    db1RaillyLinkerCompanyService1MemberOauth2LoginDataRepository.existsByOauth2TypeCodeAndOauth2IdAndRowDeleteDateStr(
                        3,
                        loginId,
                        "/"
                    )

                if (memberExists) { // 기존 회원 존재
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "2")
                    return null
                }

                verificationCode = String.format("%06d", Random().nextInt(999999)) // 랜덤 6자리 숫자
                val memberRegisterOauth2VerificationData =
                    db1RaillyLinkerCompanyService1JoinTheMembershipWithOauth2VerificationDataRepository.save(
                        Db1_RaillyLinkerCompany_Service1JoinTheMembershipWithOauth2VerificationData(
                            3,
                            loginId,
                            verificationCode,
                            LocalDateTime.now().plusSeconds(verificationTimeSec)
                        )
                    )

                verificationUid = memberRegisterOauth2VerificationData.uid!!

                expireWhen =
                    memberRegisterOauth2VerificationData.verificationExpireWhen.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
            }

            else -> {
                classLogger.info("SNS Login Type ${inputVo.oauth2TypeCode} Not Supported")
                httpServletResponse.status = 400
                return null
            }
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return MyServiceTkAuthController.CheckOauth2AccessTokenVerificationForJoinOutputVo(
            verificationUid,
            verificationCode,
            loginId,
            expireWhen
        )
    }


    ////
    @CustomTransactional([Db1MainConfig.TRANSACTION_NAME])
    override fun checkOauth2IdTokenVerificationForJoin(
        httpServletResponse: HttpServletResponse,
        inputVo: MyServiceTkAuthController.CheckOauth2IdTokenVerificationForJoinInputVo
    ): MyServiceTkAuthController.CheckOauth2IdTokenVerificationForJoinOutputVo? {
        val verificationUid: Long
        val verificationCode: String
        val expireWhen: String
        val loginId: String

        val verificationTimeSec: Long = 60 * 10
        // (정보 검증 로직 수행)
        when (inputVo.oauth2TypeCode) {
            4 -> { // Apple
                val appleInfo = appleOAuthHelperUtil.getAppleMemberData(inputVo.oauth2IdToken)

                if (appleInfo != null) {
                    loginId = appleInfo.snsId
                } else {
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "1")
                    return null
                }

                val memberExists =
                    db1RaillyLinkerCompanyService1MemberOauth2LoginDataRepository.existsByOauth2TypeCodeAndOauth2IdAndRowDeleteDateStr(
                        4,
                        loginId,
                        "/"
                    )

                if (memberExists) { // 기존 회원 존재
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "2")
                    return null
                }

                verificationCode = String.format("%06d", Random().nextInt(999999)) // 랜덤 6자리 숫자
                val memberRegisterOauth2VerificationData =
                    db1RaillyLinkerCompanyService1JoinTheMembershipWithOauth2VerificationDataRepository.save(
                        Db1_RaillyLinkerCompany_Service1JoinTheMembershipWithOauth2VerificationData(
                            4,
                            loginId,
                            verificationCode,
                            LocalDateTime.now().plusSeconds(verificationTimeSec)
                        )
                    )

                verificationUid = memberRegisterOauth2VerificationData.uid!!

                expireWhen =
                    memberRegisterOauth2VerificationData.verificationExpireWhen.atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
            }

            else -> {
                classLogger.info("SNS Login Type ${inputVo.oauth2TypeCode} Not Supported")
                httpServletResponse.status = 400
                return null
            }
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return MyServiceTkAuthController.CheckOauth2IdTokenVerificationForJoinOutputVo(
            verificationUid,
            verificationCode,
            loginId,
            expireWhen
        )
    }


    ////
    @CustomTransactional([Db1MainConfig.TRANSACTION_NAME])
    override fun joinTheMembershipWithOauth2(
        httpServletResponse: HttpServletResponse,
        inputVo: MyServiceTkAuthController.JoinTheMembershipWithOauth2InputVo
    ) {
        // oauth2 종류 (1 : GOOGLE, 2 : NAVER, 3 : KAKAO)
        val oauth2TypeCode: Int

        when (inputVo.oauth2TypeCode) {
            1 -> {
                oauth2TypeCode = 1
            }

            2 -> {
                oauth2TypeCode = 2
            }

            3 -> {
                oauth2TypeCode = 3
            }

            4 -> {
                oauth2TypeCode = 4
            }

            else -> {
                httpServletResponse.status = 400
                return
            }
        }

        val oauth2VerificationOpt =
            db1RaillyLinkerCompanyService1JoinTheMembershipWithOauth2VerificationDataRepository.findById(inputVo.verificationUid)

        if (oauth2VerificationOpt.isEmpty) { // 해당 검증을 요청한적이 없음
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        val oauth2Verification = oauth2VerificationOpt.get()

        if (oauth2Verification.oauth2TypeCode != oauth2TypeCode.toByte() ||
            oauth2Verification.oauth2Id != inputVo.oauth2Id
        ) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        if (LocalDateTime.now().isAfter(oauth2Verification.verificationExpireWhen)) {
            // 만료됨
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "2")
            return
        }

        // 입력 코드와 발급된 코드와의 매칭
        if (oauth2Verification.verificationSecret == inputVo.verificationCode) { // 코드 일치
            val isUserExists =
                db1RaillyLinkerCompanyService1MemberOauth2LoginDataRepository.existsByOauth2TypeCodeAndOauth2IdAndRowDeleteDateStr(
                    inputVo.oauth2TypeCode.toByte(),
                    inputVo.oauth2Id,
                    "/"
                )
            if (isUserExists) { // 기존 회원이 있을 때
                httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                httpServletResponse.setHeader("api-result-code", "4")
                return
            }

            if (db1RaillyLinkerCompanyService1MemberDataRepository.existsByAccountIdAndRowDeleteDateStr(
                    inputVo.id.trim(),
                    "/"
                )
            ) {
                httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                httpServletResponse.setHeader("api-result-code", "5")
                return
            }

            // 회원가입
            val memberEntity = db1RaillyLinkerCompanyService1MemberDataRepository.save(
                Db1_RaillyLinkerCompany_Service1MemberData(
                    inputVo.id,
                    null,
                    null,
                    null,
                    null
                )
            )

            // SNS OAUth2 저장
            db1RaillyLinkerCompanyService1MemberOauth2LoginDataRepository.save(
                Db1_RaillyLinkerCompany_Service1MemberOauth2LoginData(
                    memberEntity,
                    inputVo.oauth2TypeCode.toByte(),
                    inputVo.oauth2Id
                )
            )

            // 역할 저장
//            val memberUserRoleList = ArrayList<Database1_Service1_MemberRoleData>()
//            // 기본 권한 추가
//            memberUserRoleList.add(
//                Database1_Service1_MemberRoleData(
//                    memberEntity,
//                    "ROLE_USER"
//                )
//            )
//            database1Service1MemberRoleDataRepository.saveAll(memberUserRoleList)

            if (inputVo.profileImageFile != null) {
                // 저장된 프로필 이미지 파일을 다운로드 할 수 있는 URL
                val savedProfileImageUrl: String

                // 프로필 이미지 파일 저장

                //----------------------------------------------------------------------------------------------------------
                // 프로필 이미지를 서버 스토리지에 저장할 때 사용하는 방식
                // 파일 저장 기본 디렉토리 경로
                val saveDirectoryPath: Path =
                    Paths.get("./by_product_files/member/profile").toAbsolutePath().normalize()

                // 파일 저장 기본 디렉토리 생성
                Files.createDirectories(saveDirectoryPath)

                // 원본 파일명(with suffix)
                val multiPartFileNameString = StringUtils.cleanPath(inputVo.profileImageFile.originalFilename!!)

                // 파일 확장자 구분 위치
                val fileExtensionSplitIdx = multiPartFileNameString.lastIndexOf('.')

                // 확장자가 없는 파일명
                val fileNameWithOutExtension: String
                // 확장자
                val fileExtension: String

                if (fileExtensionSplitIdx == -1) {
                    fileNameWithOutExtension = multiPartFileNameString
                    fileExtension = ""
                } else {
                    fileNameWithOutExtension = multiPartFileNameString.substring(0, fileExtensionSplitIdx)
                    fileExtension =
                        multiPartFileNameString.substring(fileExtensionSplitIdx + 1, multiPartFileNameString.length)
                }

                val savedFileName = "${fileNameWithOutExtension}(${
                    LocalDateTime.now().atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
                }).$fileExtension"

                // multipartFile 을 targetPath 에 저장
                inputVo.profileImageFile.transferTo(
                    // 파일 저장 경로와 파일명(with index) 을 합친 path 객체
                    saveDirectoryPath.resolve(savedFileName).normalize()
                )

                savedProfileImageUrl = "${externalAccessAddress}/service1/tk/v1/auth/member-profile/$savedFileName"
                //----------------------------------------------------------------------------------------------------------

                val memberProfileData = db1RaillyLinkerCompanyService1MemberProfileDataRepository.save(
                    Db1_RaillyLinkerCompany_Service1MemberProfileData(
                        memberEntity,
                        savedProfileImageUrl
                    )
                )

                memberEntity.frontService1MemberProfileData = memberProfileData
            }

            db1RaillyLinkerCompanyService1MemberDataRepository.save(memberEntity)

            // 확인 완료된 검증 요청 정보 삭제
            db1RaillyLinkerCompanyService1JoinTheMembershipWithOauth2VerificationDataRepository.deleteById(
                oauth2Verification.uid!!
            )

            httpServletResponse.status = HttpStatus.OK.value()
            return
        } else { // 코드 불일치
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "3")
            return
        }
    }


    ////
    @CustomTransactional([Db1MainConfig.TRANSACTION_NAME])
    override fun updateAccountPassword(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        inputVo: MyServiceTkAuthController.UpdateAccountPasswordInputVo
    ) {
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )
        val memberData = db1RaillyLinkerCompanyService1MemberDataRepository.findById(memberUid).get()

        if (memberData.accountPassword == null) { // 기존 비번이 존재하지 않음
            if (inputVo.oldPassword != null) { // 비밀번호 불일치
                httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                httpServletResponse.setHeader("api-result-code", "1")
                return
            }
        } else { // 기존 비번 존재
            if (inputVo.oldPassword == null || !passwordEncoder.matches(
                    inputVo.oldPassword,
                    memberData.accountPassword
                )
            ) { // 비밀번호 불일치
                httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                httpServletResponse.setHeader("api-result-code", "1")
                return
            }
        }

        if (inputVo.newPassword == null) {
            val oAuth2EntityList =
                db1RaillyLinkerCompanyService1MemberOauth2LoginDataRepository.findAllByService1MemberDataAndRowDeleteDateStr(
                    memberData,
                    "/"
                )

            if (oAuth2EntityList.isEmpty()) {
                // null 로 만들려고 할 때 account 외의 OAuth2 인증이 없다면 제거 불가
                httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                httpServletResponse.setHeader("api-result-code", "2")
                return
            }

            memberData.accountPassword = null
        } else {
            memberData.accountPassword = passwordEncoder.encode(inputVo.newPassword) // 비밀번호는 암호화
        }
        db1RaillyLinkerCompanyService1MemberDataRepository.save(memberData)

        // 모든 토큰 비활성화 처리
        // loginAccessToken 의 Iterable 가져오기
        val tokenInfoList =
            db1RaillyLinkerCompanyService1LogInTokenHistoryRepository.findAllByService1MemberDataAndLogoutDateAndRowDeleteDateStr(
                memberData,
                null,
                "/"
            )

        // 발행되었던 모든 액세스 토큰 무효화 (다른 디바이스에선 사용중 로그아웃된 것과 동일한 효과)
        for (tokenInfo in tokenInfoList) {
            tokenInfo.logoutDate = LocalDateTime.now()
            db1RaillyLinkerCompanyService1LogInTokenHistoryRepository.save(tokenInfo)

            // 토큰 만료처리
            val tokenType = tokenInfo.tokenType
            val accessToken = tokenInfo.accessToken

            val accessTokenExpireRemainSeconds = when (tokenType) {
                "Bearer" -> {
                    jwtTokenUtil.getRemainSeconds(accessToken)
                }

                else -> {
                    null
                }
            }

            try {
                redis1Service1ForceExpireAuthorizationSet.saveKeyValue(
                    "${tokenType}_${accessToken}",
                    Redis1_Map_Service1ForceExpireAuthorizationSet.ValueVo(),
                    accessTokenExpireRemainSeconds!! * 1000
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        httpServletResponse.status = HttpStatus.OK.value()
    }


    ////
    @CustomTransactional([Db1MainConfig.TRANSACTION_NAME])
    override fun sendEmailVerificationForFindPassword(
        httpServletResponse: HttpServletResponse,
        inputVo: MyServiceTkAuthController.SendEmailVerificationForFindPasswordInputVo
    ): MyServiceTkAuthController.SendEmailVerificationForFindPasswordOutputVo? {
        // 입력 데이터 검증
        val memberExists =
            db1RaillyLinkerCompanyService1MemberEmailDataRepository.existsByEmailAddressAndRowDeleteDateStr(
                inputVo.email,
                "/"
            )
        if (!memberExists) { // 회원 없음
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return null
        }

        // 정보 저장 후 이메일 발송
        val verificationTimeSec: Long = 60 * 10
        val verificationCode = String.format("%06d", Random().nextInt(999999)) // 랜덤 6자리 숫자
        val memberFindPasswordEmailVerificationData =
            db1RaillyLinkerCompanyService1FindPasswordWithEmailVerificationDataRepository.save(
                Db1_RaillyLinkerCompany_Service1FindPasswordWithEmailVerificationData(
                    inputVo.email,
                    verificationCode,
                    LocalDateTime.now().plusSeconds(verificationTimeSec)
                )
            )

        emailSender.sendThymeLeafHtmlMail(
            "Springboot Mvc Project Template",
            arrayOf(inputVo.email),
            null,
            "Springboot Mvc Project Template 비밀번호 찾기 - 본인 계정 확인용 이메일입니다.",
            "send_email_verification_for_find_password/find_password_email_verification_email",
            hashMapOf(
                Pair("verificationCode", verificationCode)
            ),
            null,
            null,
            null,
            null
        )

        return MyServiceTkAuthController.SendEmailVerificationForFindPasswordOutputVo(
            memberFindPasswordEmailVerificationData.uid!!,
            memberFindPasswordEmailVerificationData.verificationExpireWhen.atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
        )
    }


    ////
    override fun checkEmailVerificationForFindPassword(
        httpServletResponse: HttpServletResponse,
        verificationUid: Long,
        email: String,
        verificationCode: String
    ) {
        val emailVerificationOpt =
            db1RaillyLinkerCompanyService1FindPasswordWithEmailVerificationDataRepository.findById(verificationUid)

        if (emailVerificationOpt.isEmpty) { // 해당 이메일 검증을 요청한적이 없음
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        val emailVerification = emailVerificationOpt.get()

        if (emailVerification.emailAddress != email) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        if (LocalDateTime.now().isAfter(emailVerification.verificationExpireWhen)) {
            // 만료됨
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "2")
            return
        }

        // 입력 코드와 발급된 코드와의 매칭
        val codeMatched = emailVerification.verificationSecret == verificationCode

        if (codeMatched) {
            // 코드 일치
            httpServletResponse.status = HttpStatus.OK.value()
        } else {
            // 코드 불일치
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "3")
        }
    }


    ////
    @CustomTransactional([Db1MainConfig.TRANSACTION_NAME])
    override fun findPasswordWithEmail(
        httpServletResponse: HttpServletResponse,
        inputVo: MyServiceTkAuthController.FindPasswordWithEmailInputVo
    ) {
        val emailVerificationOpt =
            db1RaillyLinkerCompanyService1FindPasswordWithEmailVerificationDataRepository.findById(inputVo.verificationUid)

        if (emailVerificationOpt.isEmpty) { // 해당 이메일 검증을 요청한적이 없음
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        val emailVerification = emailVerificationOpt.get()

        if (emailVerification.emailAddress != inputVo.email) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        if (LocalDateTime.now().isAfter(emailVerification.verificationExpireWhen)) {
            // 만료됨
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "2")
            return
        }

        // 입력 코드와 발급된 코드와의 매칭
        if (emailVerification.verificationSecret == inputVo.verificationCode) { // 코드 일치
            // 입력 데이터 검증
            val memberEmail =
                db1RaillyLinkerCompanyService1MemberEmailDataRepository.findByEmailAddressAndRowDeleteDateStr(
                    inputVo.email,
                    "/"
                )

            if (memberEmail == null) {
                httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                httpServletResponse.setHeader("api-result-code", "4")
                return
            }

            // 랜덤 비번 생성 후 세팅
            val newPassword = String.format("%09d", Random().nextInt(999999999)) // 랜덤 9자리 숫자
            memberEmail.service1MemberData.accountPassword = passwordEncoder.encode(newPassword) // 비밀번호는 암호화
            db1RaillyLinkerCompanyService1MemberDataRepository.save(memberEmail.service1MemberData)

            // 생성된 비번 이메일 전송
            emailSender.sendThymeLeafHtmlMail(
                "Springboot Mvc Project Template",
                arrayOf(inputVo.email),
                null,
                "Springboot Mvc Project Template 새 비밀번호 발급",
                "find_password_with_email/find_password_new_password_email",
                hashMapOf(
                    Pair("newPassword", newPassword)
                ),
                null,
                null,
                null,
                null
            )

            // 확인 완료된 검증 요청 정보 삭제
            db1RaillyLinkerCompanyService1FindPasswordWithEmailVerificationDataRepository.deleteById(emailVerification.uid!!)

            // 모든 토큰 비활성화 처리
            // loginAccessToken 의 Iterable 가져오기
            val tokenInfoList =
                db1RaillyLinkerCompanyService1LogInTokenHistoryRepository.findAllByService1MemberDataAndLogoutDateAndRowDeleteDateStr(
                    memberEmail.service1MemberData,
                    null,
                    "/"
                )

            // 발행되었던 모든 액세스 토큰 무효화 (다른 디바이스에선 사용중 로그아웃된 것과 동일한 효과)
            for (tokenInfo in tokenInfoList) {
                tokenInfo.logoutDate = LocalDateTime.now()
                db1RaillyLinkerCompanyService1LogInTokenHistoryRepository.save(tokenInfo)

                // 토큰 만료처리
                val tokenType = tokenInfo.tokenType
                val accessToken = tokenInfo.accessToken

                val accessTokenExpireRemainSeconds = when (tokenType) {
                    "Bearer" -> {
                        jwtTokenUtil.getRemainSeconds(accessToken)
                    }

                    else -> {
                        null
                    }
                }

                try {
                    redis1Service1ForceExpireAuthorizationSet.saveKeyValue(
                        "${tokenType}_${accessToken}",
                        Redis1_Map_Service1ForceExpireAuthorizationSet.ValueVo(),
                        accessTokenExpireRemainSeconds!! * 1000
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            httpServletResponse.status = HttpStatus.OK.value()
            return
        } else { // 코드 불일치
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "3")
            return
        }
    }


    ////
    @CustomTransactional([Db1MainConfig.TRANSACTION_NAME])
    override fun sendPhoneVerificationForFindPassword(
        httpServletResponse: HttpServletResponse,
        inputVo: MyServiceTkAuthController.SendPhoneVerificationForFindPasswordInputVo
    ): MyServiceTkAuthController.SendPhoneVerificationForFindPasswordOutputVo? {
        // 입력 데이터 검증
        val memberExists =
            db1RaillyLinkerCompanyService1MemberPhoneDataRepository.existsByPhoneNumberAndRowDeleteDateStr(
                inputVo.phoneNumber,
                "/"
            )
        if (!memberExists) { // 회원 없음
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return null
        }

        // 정보 저장 후 발송
        val verificationTimeSec: Long = 60 * 10
        val verificationCode = String.format("%06d", Random().nextInt(999999)) // 랜덤 6자리 숫자
        val memberFindPasswordPhoneNumberVerificationData =
            db1RaillyLinkerCompanyService1FindPasswordWithPhoneNumberVerificationDataRepository.save(
                Db1_RaillyLinkerCompany_Service1FindPasswordWithPhoneNumberVerificationData(
                    inputVo.phoneNumber,
                    verificationCode,
                    LocalDateTime.now().plusSeconds(verificationTimeSec)
                )
            )

        val phoneNumberSplit = inputVo.phoneNumber.split(")") // ["82", "010-0000-0000"]

        // 국가 코드 (ex : 82)
        val countryCode = phoneNumberSplit[0]

        // 전화번호 (ex : "01000000000")
        val phoneNumber = (phoneNumberSplit[1].replace("-", "")).replace(" ", "")

        val sendSmsResult = naverSmsSenderComponent.sendSms(
            NaverSmsSenderComponent.SendSmsInputVo(
                "SMS",
                countryCode,
                phoneNumber,
                "[Springboot Mvc Project Template - 비밀번호 찾기] 인증번호 [${verificationCode}]"
            )
        )

        if (!sendSmsResult) {
            throw Exception()
        }

        return MyServiceTkAuthController.SendPhoneVerificationForFindPasswordOutputVo(
            memberFindPasswordPhoneNumberVerificationData.uid!!,
            memberFindPasswordPhoneNumberVerificationData.verificationExpireWhen.atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
        )
    }


    ////
    override fun checkPhoneVerificationForFindPassword(
        httpServletResponse: HttpServletResponse,
        verificationUid: Long,
        phoneNumber: String,
        verificationCode: String
    ) {
        val phoneNumberVerificationOpt =
            db1RaillyLinkerCompanyService1FindPasswordWithPhoneNumberVerificationDataRepository.findById(verificationUid)

        if (phoneNumberVerificationOpt.isEmpty) { // 해당 이메일 검증을 요청한적이 없음
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        val phoneNumberVerification = phoneNumberVerificationOpt.get()

        if (phoneNumberVerification.phoneNumber != phoneNumber) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        if (LocalDateTime.now().isAfter(phoneNumberVerification.verificationExpireWhen)) {
            // 만료됨
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "2")
            return
        }

        // 입력 코드와 발급된 코드와의 매칭
        val codeMatched = phoneNumberVerification.verificationSecret == verificationCode

        if (codeMatched) {
            // 코드 일치
            httpServletResponse.status = HttpStatus.OK.value()
        } else {
            // 코드 불일치
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "3")
        }
    }


    ////
    @CustomTransactional([Db1MainConfig.TRANSACTION_NAME])
    override fun findPasswordWithPhoneNumber(
        httpServletResponse: HttpServletResponse,
        inputVo: MyServiceTkAuthController.FindPasswordWithPhoneNumberInputVo
    ) {
        val phoneNumberVerificationOpt =
            db1RaillyLinkerCompanyService1FindPasswordWithPhoneNumberVerificationDataRepository.findById(inputVo.verificationUid)

        if (phoneNumberVerificationOpt.isEmpty) { // 해당 이메일 검증을 요청한적이 없음
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        val phoneNumberVerification = phoneNumberVerificationOpt.get()

        if (phoneNumberVerification.phoneNumber != inputVo.phoneNumber) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        if (LocalDateTime.now().isAfter(phoneNumberVerification.verificationExpireWhen)) {
            // 만료됨
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "2")
            return
        }

        // 입력 코드와 발급된 코드와의 매칭
        if (phoneNumberVerification.verificationSecret == inputVo.verificationCode) { // 코드 일치
            // 입력 데이터 검증
            val memberPhone =
                db1RaillyLinkerCompanyService1MemberPhoneDataRepository.findByPhoneNumberAndRowDeleteDateStr(
                    inputVo.phoneNumber,
                    "/"
                )

            if (memberPhone == null) {
                httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                httpServletResponse.setHeader("api-result-code", "4")
                return
            }

            // 랜덤 비번 생성 후 세팅
            val newPassword = String.format("%09d", Random().nextInt(999999999)) // 랜덤 9자리 숫자
            memberPhone.service1MemberData.accountPassword = passwordEncoder.encode(newPassword) // 비밀번호는 암호화
            db1RaillyLinkerCompanyService1MemberDataRepository.save(memberPhone.service1MemberData)

            val phoneNumberSplit = inputVo.phoneNumber.split(")") // ["82", "010-0000-0000"]

            // 국가 코드 (ex : 82)
            val countryCode = phoneNumberSplit[0]

            // 전화번호 (ex : "01000000000")
            val phoneNumber = (phoneNumberSplit[1].replace("-", "")).replace(" ", "")

            val sendSmsResult = naverSmsSenderComponent.sendSms(
                NaverSmsSenderComponent.SendSmsInputVo(
                    "SMS",
                    countryCode,
                    phoneNumber,
                    "[Springboot Mvc Project Template - 새 비밀번호] $newPassword"
                )
            )

            if (!sendSmsResult) {
                throw Exception()
            }

            // 확인 완료된 검증 요청 정보 삭제
            db1RaillyLinkerCompanyService1FindPasswordWithPhoneNumberVerificationDataRepository.deleteById(
                phoneNumberVerification.uid!!
            )

            // 모든 토큰 비활성화 처리
            // loginAccessToken 의 Iterable 가져오기
            val tokenInfoList =
                db1RaillyLinkerCompanyService1LogInTokenHistoryRepository.findAllByService1MemberDataAndLogoutDateAndRowDeleteDateStr(
                    memberPhone.service1MemberData,
                    null,
                    "/"
                )

            // 발행되었던 모든 액세스 토큰 무효화 (다른 디바이스에선 사용중 로그아웃된 것과 동일한 효과)
            for (tokenInfo in tokenInfoList) {
                tokenInfo.logoutDate = LocalDateTime.now()
                db1RaillyLinkerCompanyService1LogInTokenHistoryRepository.save(tokenInfo)

                // 토큰 만료처리
                val tokenType = tokenInfo.tokenType
                val accessToken = tokenInfo.accessToken

                val accessTokenExpireRemainSeconds = when (tokenType) {
                    "Bearer" -> {
                        jwtTokenUtil.getRemainSeconds(accessToken)
                    }

                    else -> {
                        null
                    }
                }

                try {
                    redis1Service1ForceExpireAuthorizationSet.saveKeyValue(
                        "${tokenType}_${accessToken}",
                        Redis1_Map_Service1ForceExpireAuthorizationSet.ValueVo(),
                        accessTokenExpireRemainSeconds!! * 1000
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            httpServletResponse.status = HttpStatus.OK.value()
            return
        } else { // 코드 불일치
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "3")
            return
        }
    }


    ////
    override fun getMyEmailList(
        httpServletResponse: HttpServletResponse,
        authorization: String
    ): MyServiceTkAuthController.GetMyEmailListOutputVo? {
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )
        val memberData = db1RaillyLinkerCompanyService1MemberDataRepository.findById(memberUid).get()

        val emailEntityList =
            db1RaillyLinkerCompanyService1MemberEmailDataRepository.findAllByService1MemberDataAndRowDeleteDateStr(
                memberData,
                "/"
            )
        val emailList = ArrayList<MyServiceTkAuthController.GetMyEmailListOutputVo.EmailInfo>()
        for (emailEntity in emailEntityList) {
            emailList.add(
                MyServiceTkAuthController.GetMyEmailListOutputVo.EmailInfo(
                    emailEntity.uid!!,
                    emailEntity.emailAddress,
                    emailEntity.uid == memberData.frontService1MemberEmailData?.uid
                )
            )
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return MyServiceTkAuthController.GetMyEmailListOutputVo(
            emailList
        )
    }


    ////
    override fun getMyPhoneNumberList(
        httpServletResponse: HttpServletResponse,
        authorization: String
    ): MyServiceTkAuthController.GetMyPhoneNumberListOutputVo? {
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )
        val memberData = db1RaillyLinkerCompanyService1MemberDataRepository.findById(memberUid).get()

        val phoneEntityList =
            db1RaillyLinkerCompanyService1MemberPhoneDataRepository.findAllByService1MemberDataAndRowDeleteDateStr(
                memberData,
                "/"
            )
        val phoneNumberList = ArrayList<MyServiceTkAuthController.GetMyPhoneNumberListOutputVo.PhoneInfo>()
        for (phoneEntity in phoneEntityList) {
            phoneNumberList.add(
                MyServiceTkAuthController.GetMyPhoneNumberListOutputVo.PhoneInfo(
                    phoneEntity.uid!!,
                    phoneEntity.phoneNumber,
                    phoneEntity.uid == memberData.frontService1MemberPhoneData?.uid
                )
            )
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return MyServiceTkAuthController.GetMyPhoneNumberListOutputVo(
            phoneNumberList
        )
    }


    ////
    override fun getMyOauth2List(
        httpServletResponse: HttpServletResponse,
        authorization: String
    ): MyServiceTkAuthController.GetMyOauth2ListOutputVo? {
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )
        val memberData = db1RaillyLinkerCompanyService1MemberDataRepository.findById(memberUid).get()

        val oAuth2EntityList =
            db1RaillyLinkerCompanyService1MemberOauth2LoginDataRepository.findAllByService1MemberDataAndRowDeleteDateStr(
                memberData,
                "/"
            )
        val myOAuth2List = ArrayList<MyServiceTkAuthController.GetMyOauth2ListOutputVo.OAuth2Info>()
        for (oAuth2Entity in oAuth2EntityList) {
            myOAuth2List.add(
                MyServiceTkAuthController.GetMyOauth2ListOutputVo.OAuth2Info(
                    oAuth2Entity.uid!!,
                    oAuth2Entity.oauth2TypeCode.toInt(),
                    oAuth2Entity.oauth2Id
                )
            )
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return MyServiceTkAuthController.GetMyOauth2ListOutputVo(
            myOAuth2List
        )
    }


    ////
    @CustomTransactional([Db1MainConfig.TRANSACTION_NAME])
    override fun sendEmailVerificationForAddNewEmail(
        httpServletResponse: HttpServletResponse,
        inputVo: MyServiceTkAuthController.SendEmailVerificationForAddNewEmailInputVo,
        authorization: String
    ): MyServiceTkAuthController.SendEmailVerificationForAddNewEmailOutputVo? {
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )
        val memberData = db1RaillyLinkerCompanyService1MemberDataRepository.findById(memberUid).get()

        // 입력 데이터 검증
        val memberExists =
            db1RaillyLinkerCompanyService1MemberEmailDataRepository.existsByEmailAddressAndRowDeleteDateStr(
                inputVo.email,
                "/"
            )

        if (memberExists) { // 기존 회원 존재
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return null
        }

        // 정보 저장 후 이메일 발송
        val verificationTimeSec: Long = 60 * 10
        val verificationCode = String.format("%06d", Random().nextInt(999999)) // 랜덤 6자리 숫자
        val memberRegisterEmailVerificationData = db1RaillyLinkerCompanyService1AddEmailVerificationDataRepository.save(
            Db1_RaillyLinkerCompany_Service1AddEmailVerificationData(
                memberData,
                inputVo.email,
                verificationCode,
                LocalDateTime.now().plusSeconds(verificationTimeSec)
            )
        )

        emailSender.sendThymeLeafHtmlMail(
            "Springboot Mvc Project Template",
            arrayOf(inputVo.email),
            null,
            "Springboot Mvc Project Template 이메일 추가 - 본인 계정 확인용 이메일입니다.",
            "send_email_verification_for_add_new_email/add_email_verification_email",
            hashMapOf(
                Pair("verificationCode", verificationCode)
            ),
            null,
            null,
            null,
            null
        )

        httpServletResponse.status = HttpStatus.OK.value()
        return MyServiceTkAuthController.SendEmailVerificationForAddNewEmailOutputVo(
            memberRegisterEmailVerificationData.uid!!,
            memberRegisterEmailVerificationData.verificationExpireWhen.atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
        )
    }


    ////
    override fun checkEmailVerificationForAddNewEmail(
        httpServletResponse: HttpServletResponse,
        verificationUid: Long,
        email: String,
        verificationCode: String,
        authorization: String
    ) {
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )
        val emailVerificationOpt =
            db1RaillyLinkerCompanyService1AddEmailVerificationDataRepository.findById(verificationUid)

        if (emailVerificationOpt.isEmpty) { // 해당 이메일 검증을 요청한적이 없음
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        val emailVerification = emailVerificationOpt.get()

        if (emailVerification.service1MemberData.uid!! != memberUid ||
            emailVerification.emailAddress != email
        ) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        if (LocalDateTime.now().isAfter(emailVerification.verificationExpireWhen)) {
            // 만료됨
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "2")
            return
        }

        // 입력 코드와 발급된 코드와의 매칭
        val codeMatched = emailVerification.verificationSecret == verificationCode

        if (codeMatched) {
            // 코드 일치
            httpServletResponse.status = HttpStatus.OK.value()
        } else {
            // 코드 불일치
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "3")
        }
    }


    ////
    @CustomTransactional([Db1MainConfig.TRANSACTION_NAME])
    override fun addNewEmail(
        httpServletResponse: HttpServletResponse,
        inputVo: MyServiceTkAuthController.AddNewEmailInputVo,
        authorization: String
    ): MyServiceTkAuthController.AddNewEmailOutputVo? {
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )
        val memberData = db1RaillyLinkerCompanyService1MemberDataRepository.findById(memberUid).get()

        val emailVerificationOpt =
            db1RaillyLinkerCompanyService1AddEmailVerificationDataRepository.findById(inputVo.verificationUid)

        if (emailVerificationOpt.isEmpty) { // 해당 이메일 검증을 요청한적이 없음
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return null
        }

        val emailVerification = emailVerificationOpt.get()

        if (emailVerification.service1MemberData.uid!! != memberUid ||
            emailVerification.emailAddress != inputVo.email
        ) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return null
        }

        if (LocalDateTime.now().isAfter(emailVerification.verificationExpireWhen)) {
            // 만료됨
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "2")
            return null
        }

        // 입력 코드와 발급된 코드와의 매칭
        if (emailVerification.verificationSecret == inputVo.verificationCode) { // 코드 일치
            val isUserExists =
                db1RaillyLinkerCompanyService1MemberEmailDataRepository.existsByEmailAddressAndRowDeleteDateStr(
                    inputVo.email,
                    "/"
                )
            if (isUserExists) { // 기존 회원이 있을 때
                httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                httpServletResponse.setHeader("api-result-code", "4")
                return null
            }

            // 이메일 추가
            val memberEmailData = db1RaillyLinkerCompanyService1MemberEmailDataRepository.save(
                Db1_RaillyLinkerCompany_Service1MemberEmailData(
                    memberData,
                    inputVo.email
                )
            )

            // 확인 완료된 검증 요청 정보 삭제
            db1RaillyLinkerCompanyService1AddEmailVerificationDataRepository.deleteById(emailVerification.uid!!)

            if (inputVo.frontEmail) {
                // 대표 이메일로 설정
                memberData.frontService1MemberEmailData = memberEmailData
                db1RaillyLinkerCompanyService1MemberDataRepository.save(memberData)
            }

            httpServletResponse.status = HttpStatus.OK.value()
            return MyServiceTkAuthController.AddNewEmailOutputVo(
                memberEmailData.uid!!
            )
        } else { // 코드 불일치
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "3")
            return null
        }
    }


    ////
    @CustomTransactional([Db1MainConfig.TRANSACTION_NAME])
    override fun deleteMyEmail(
        httpServletResponse: HttpServletResponse,
        emailUid: Long,
        authorization: String
    ) {
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )
        val memberData = db1RaillyLinkerCompanyService1MemberDataRepository.findById(memberUid).get()

        // 내 계정에 등록된 모든 이메일 리스트 가져오기
        val myEmailList =
            db1RaillyLinkerCompanyService1MemberEmailDataRepository.findAllByService1MemberDataAndRowDeleteDateStr(
                memberData,
                "/"
            )

        if (myEmailList.isEmpty()) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        var myEmailVo: Db1_RaillyLinkerCompany_Service1MemberEmailData? = null

        for (myEmail in myEmailList) {
            if (myEmail.uid == emailUid) {
                myEmailVo = myEmail
                break
            }
        }

        if (myEmailVo == null) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        val isOauth2Exists =
            db1RaillyLinkerCompanyService1MemberOauth2LoginDataRepository.existsByService1MemberDataAndRowDeleteDateStr(
                memberData,
                "/"
            )

        val isMemberPhoneExists =
            db1RaillyLinkerCompanyService1MemberPhoneDataRepository.existsByService1MemberDataAndRowDeleteDateStr(
                memberData,
                "/"
            )

        if (isOauth2Exists ||
            (memberData.accountPassword != null && myEmailList.size > 1) ||
            (memberData.accountPassword != null && isMemberPhoneExists)
        ) {
            // 이메일 지우기
            db1RaillyLinkerCompanyService1MemberEmailDataRepository.deleteById(myEmailVo.uid!!)

            if (memberData.frontService1MemberEmailData?.uid == emailUid) {
                // 대표 이메일 삭제
                memberData.frontService1MemberEmailData = null
                db1RaillyLinkerCompanyService1MemberDataRepository.save(memberData)
            }

            httpServletResponse.status = HttpStatus.OK.value()
            return
        } else {
            // 이외에 사용 가능한 로그인 정보가 존재하지 않을 때
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "2")
            return
        }
    }


    ////
    @CustomTransactional([Db1MainConfig.TRANSACTION_NAME])
    override fun sendPhoneVerificationForAddNewPhoneNumber(
        httpServletResponse: HttpServletResponse,
        inputVo: MyServiceTkAuthController.SendPhoneVerificationForAddNewPhoneNumberInputVo,
        authorization: String
    ): MyServiceTkAuthController.SendPhoneVerificationForAddNewPhoneNumberOutputVo? {
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )
        val memberData = db1RaillyLinkerCompanyService1MemberDataRepository.findById(memberUid).get()

        // 입력 데이터 검증
        val memberExists =
            db1RaillyLinkerCompanyService1MemberPhoneDataRepository.existsByPhoneNumberAndRowDeleteDateStr(
                inputVo.phoneNumber,
                "/"
            )

        if (memberExists) { // 기존 회원 존재
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return null
        }

        // 정보 저장 후 이메일 발송
        val verificationTimeSec: Long = 60 * 10
        val verificationCode = String.format("%06d", Random().nextInt(999999)) // 랜덤 6자리 숫자
        val memberAddPhoneNumberVerificationData =
            db1RaillyLinkerCompanyService1AddPhoneNumberVerificationDataRepository.save(
                Db1_RaillyLinkerCompany_Service1AddPhoneNumberVerificationData(
                    memberData,
                    inputVo.phoneNumber,
                    verificationCode,
                    LocalDateTime.now().plusSeconds(verificationTimeSec)
                )
            )

        val phoneNumberSplit = inputVo.phoneNumber.split(")") // ["82", "010-0000-0000"]

        // 국가 코드 (ex : 82)
        val countryCode = phoneNumberSplit[0]

        // 전화번호 (ex : "01000000000")
        val phoneNumber = (phoneNumberSplit[1].replace("-", "")).replace(" ", "")

        val sendSmsResult = naverSmsSenderComponent.sendSms(
            NaverSmsSenderComponent.SendSmsInputVo(
                "SMS",
                countryCode,
                phoneNumber,
                "[Springboot Mvc Project Template - 전화번호 추가] 인증번호 [${verificationCode}]"
            )
        )

        if (!sendSmsResult) {
            throw Exception()
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return MyServiceTkAuthController.SendPhoneVerificationForAddNewPhoneNumberOutputVo(
            memberAddPhoneNumberVerificationData.uid!!,
            memberAddPhoneNumberVerificationData.verificationExpireWhen.atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
        )
    }


    ////
    override fun checkPhoneVerificationForAddNewPhoneNumber(
        httpServletResponse: HttpServletResponse,
        verificationUid: Long,
        phoneNumber: String,
        verificationCode: String,
        authorization: String
    ) {
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )

        val phoneNumberVerificationOpt =
            db1RaillyLinkerCompanyService1AddPhoneNumberVerificationDataRepository.findById(verificationUid)

        if (phoneNumberVerificationOpt.isEmpty) { // 해당 이메일 검증을 요청한적이 없음
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        val phoneNumberVerification = phoneNumberVerificationOpt.get()

        if (phoneNumberVerification.service1MemberData.uid!! != memberUid ||
            phoneNumberVerification.phoneNumber != phoneNumber
        ) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        if (LocalDateTime.now().isAfter(phoneNumberVerification.verificationExpireWhen)) {
            // 만료됨
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "2")
            return
        }

        // 입력 코드와 발급된 코드와의 매칭
        if (phoneNumberVerification.verificationSecret == verificationCode) {
            // 코드 일치
            httpServletResponse.status = HttpStatus.OK.value()
        } else {
            // 코드 불일치
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "3")
        }
    }


    ////
    @CustomTransactional([Db1MainConfig.TRANSACTION_NAME])
    override fun addNewPhoneNumber(
        httpServletResponse: HttpServletResponse,
        inputVo: MyServiceTkAuthController.AddNewPhoneNumberInputVo,
        authorization: String
    ): MyServiceTkAuthController.AddNewPhoneNumberOutputVo? {
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )
        val memberData = db1RaillyLinkerCompanyService1MemberDataRepository.findById(memberUid).get()

        val phoneNumberVerificationOpt =
            db1RaillyLinkerCompanyService1AddPhoneNumberVerificationDataRepository.findById(inputVo.verificationUid)

        if (phoneNumberVerificationOpt.isEmpty) { // 해당 이메일 검증을 요청한적이 없음
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return null
        }

        val phoneNumberVerification = phoneNumberVerificationOpt.get()

        if (phoneNumberVerification.service1MemberData.uid!! != memberUid ||
            phoneNumberVerification.phoneNumber != inputVo.phoneNumber
        ) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return null
        }

        if (LocalDateTime.now().isAfter(phoneNumberVerification.verificationExpireWhen)) {
            // 만료됨
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "2")
            return null
        }

        // 입력 코드와 발급된 코드와의 매칭
        val codeMatched = phoneNumberVerification.verificationSecret == inputVo.verificationCode

        if (codeMatched) { // 코드 일치
            val isUserExists =
                db1RaillyLinkerCompanyService1MemberPhoneDataRepository.existsByPhoneNumberAndRowDeleteDateStr(
                    inputVo.phoneNumber,
                    "/"
                )
            if (isUserExists) { // 기존 회원이 있을 때
                httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                httpServletResponse.setHeader("api-result-code", "4")
                return null
            }

            // 추가
            val memberPhoneData = db1RaillyLinkerCompanyService1MemberPhoneDataRepository.save(
                Db1_RaillyLinkerCompany_Service1MemberPhoneData(
                    memberData,
                    inputVo.phoneNumber
                )
            )

            // 확인 완료된 검증 요청 정보 삭제
            db1RaillyLinkerCompanyService1AddPhoneNumberVerificationDataRepository.deleteById(phoneNumberVerification.uid!!)

            if (inputVo.frontPhoneNumber) {
                // 대표 전화로 설정
                memberData.frontService1MemberPhoneData = memberPhoneData
                db1RaillyLinkerCompanyService1MemberDataRepository.save(memberData)
            }

            httpServletResponse.status = HttpStatus.OK.value()
            return MyServiceTkAuthController.AddNewPhoneNumberOutputVo(
                memberPhoneData.uid!!
            )
        } else { // 코드 불일치
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "3")
            return null
        }
    }


    ////
    @CustomTransactional([Db1MainConfig.TRANSACTION_NAME])
    override fun deleteMyPhoneNumber(
        httpServletResponse: HttpServletResponse,
        phoneUid: Long,
        authorization: String
    ) {
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )
        val memberData = db1RaillyLinkerCompanyService1MemberDataRepository.findById(memberUid).get()

        // 내 계정에 등록된 모든 전화번호 리스트 가져오기
        val myPhoneList =
            db1RaillyLinkerCompanyService1MemberPhoneDataRepository.findAllByService1MemberDataAndRowDeleteDateStr(
                memberData,
                "/"
            )

        if (myPhoneList.isEmpty()) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        var myPhoneVo: Db1_RaillyLinkerCompany_Service1MemberPhoneData? = null

        for (myPhone in myPhoneList) {
            if (myPhone.uid == phoneUid) {
                myPhoneVo = myPhone
                break
            }
        }

        if (myPhoneVo == null) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        val isOauth2Exists =
            db1RaillyLinkerCompanyService1MemberOauth2LoginDataRepository.existsByService1MemberDataAndRowDeleteDateStr(
                memberData,
                "/"
            )

        val isMemberEmailExists =
            db1RaillyLinkerCompanyService1MemberEmailDataRepository.existsByService1MemberDataAndRowDeleteDateStr(
                memberData,
                "/"
            )

        if (isOauth2Exists ||
            (memberData.accountPassword != null && myPhoneList.size > 1) ||
            (memberData.accountPassword != null && isMemberEmailExists)
        ) {
            // 전화번호 지우기
            db1RaillyLinkerCompanyService1MemberPhoneDataRepository.deleteById(myPhoneVo.uid!!)

            if (memberData.frontService1MemberPhoneData?.uid == phoneUid) {
                memberData.frontService1MemberPhoneData = null
                db1RaillyLinkerCompanyService1MemberDataRepository.save(memberData)
            }

            httpServletResponse.status = HttpStatus.OK.value()
            return
        } else {
            // 이외에 사용 가능한 로그인 정보가 존재하지 않을 때
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "2")
            return
        }
    }


    ////
    @CustomTransactional([Db1MainConfig.TRANSACTION_NAME])
    override fun addNewOauth2WithAccessToken(
        httpServletResponse: HttpServletResponse,
        inputVo: MyServiceTkAuthController.AddNewOauth2WithAccessTokenInputVo,
        authorization: String
    ) {
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )
        val memberData = db1RaillyLinkerCompanyService1MemberDataRepository.findById(memberUid).get()

        val snsTypeCode: Int
        val snsId: String

        // (정보 검증 로직 수행)
        when (inputVo.oauth2TypeCode) {
            1 -> { // GOOGLE
                // 클라이언트에서 받은 access 토큰으로 멤버 정보 요청
                val response = networkRetrofit2.wwwGoogleapisComRequestApi.getOauth2V1UserInfo(
                    inputVo.oauth2AccessToken
                ).execute()

                // 액세트 토큰 정상 동작 확인
                if (response.code() != 200 ||
                    response.body() == null
                ) {
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "1")
                    return
                }

                snsTypeCode = 1
                snsId = response.body()!!.id!!
            }

            2 -> { // NAVER
                // 클라이언트에서 받은 access 토큰으로 멤버 정보 요청
                val response = networkRetrofit2.openapiNaverComRequestApi.getV1NidMe(
                    inputVo.oauth2AccessToken
                ).execute()

                // 액세트 토큰 정상 동작 확인
                if (response.body() == null
                ) {
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "1")
                    return
                }

                snsTypeCode = 2
                snsId = response.body()!!.response.id
            }

            3 -> { // KAKAO TALK
                // 클라이언트에서 받은 access 토큰으로 멤버 정보 요청
                val response = networkRetrofit2.kapiKakaoComRequestApi.getV2UserMe(
                    inputVo.oauth2AccessToken
                ).execute()

                // 액세트 토큰 정상 동작 확인
                if (response.code() != 200 ||
                    response.body() == null
                ) {
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "1")
                    return
                }

                snsTypeCode = 3
                snsId = response.body()!!.id.toString()
            }

            else -> {
                classLogger.info("SNS Login Type ${inputVo.oauth2TypeCode} Not Supported")
                httpServletResponse.status = 400
                return
            }
        }

        // 사용중인지 아닌지 검증
        val memberExists =
            db1RaillyLinkerCompanyService1MemberOauth2LoginDataRepository.existsByOauth2TypeCodeAndOauth2IdAndRowDeleteDateStr(
                snsTypeCode.toByte(),
                snsId,
                "/"
            )

        if (memberExists) { // 이미 사용중인 SNS 인증
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "2")
            return
        }

        // SNS 인증 추가
        db1RaillyLinkerCompanyService1MemberOauth2LoginDataRepository.save(
            Db1_RaillyLinkerCompany_Service1MemberOauth2LoginData(
                memberData,
                snsTypeCode.toByte(),
                snsId
            )
        )

        httpServletResponse.status = HttpStatus.OK.value()
    }


    ////
    @CustomTransactional([Db1MainConfig.TRANSACTION_NAME])
    override fun addNewOauth2WithIdToken(
        httpServletResponse: HttpServletResponse,
        inputVo: MyServiceTkAuthController.AddNewOauth2WithIdTokenInputVo,
        authorization: String
    ) {
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )
        val memberData = db1RaillyLinkerCompanyService1MemberDataRepository.findById(memberUid).get()

        val snsTypeCode: Int
        val snsId: String

        // (정보 검증 로직 수행)
        when (inputVo.oauth2TypeCode) {
            4 -> { // Apple
                val appleInfo = appleOAuthHelperUtil.getAppleMemberData(inputVo.oauth2IdToken)

                if (appleInfo != null) {
                    snsId = appleInfo.snsId
                } else {
                    httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                    httpServletResponse.setHeader("api-result-code", "1")
                    return
                }

                snsTypeCode = 4
            }

            else -> {
                classLogger.info("SNS Login Type ${inputVo.oauth2TypeCode} Not Supported")
                httpServletResponse.status = 400
                return
            }
        }

        // 사용중인지 아닌지 검증
        val memberExists =
            db1RaillyLinkerCompanyService1MemberOauth2LoginDataRepository.existsByOauth2TypeCodeAndOauth2IdAndRowDeleteDateStr(
                snsTypeCode.toByte(),
                snsId,
                "/"
            )

        if (memberExists) { // 이미 사용중인 SNS 인증
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "2")
            return
        }

        // SNS 인증 추가
        db1RaillyLinkerCompanyService1MemberOauth2LoginDataRepository.save(
            Db1_RaillyLinkerCompany_Service1MemberOauth2LoginData(
                memberData,
                snsTypeCode.toByte(),
                snsId
            )
        )

        httpServletResponse.status = HttpStatus.OK.value()
    }


    ////
    @CustomTransactional([Db1MainConfig.TRANSACTION_NAME])
    override fun deleteMyOauth2(
        httpServletResponse: HttpServletResponse,
        oAuth2Uid: Long,
        authorization: String
    ) {
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )
        val memberData = db1RaillyLinkerCompanyService1MemberDataRepository.findById(memberUid).get()

        // 내 계정에 등록된 모든 인증 리스트 가져오기
        val myOAuth2List =
            db1RaillyLinkerCompanyService1MemberOauth2LoginDataRepository.findAllByService1MemberDataAndRowDeleteDateStr(
                memberData,
                "/"
            )

        if (myOAuth2List.isEmpty()) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        var myOAuth2Vo: Db1_RaillyLinkerCompany_Service1MemberOauth2LoginData? = null

        for (myOAuth2 in myOAuth2List) {
            if (myOAuth2.uid == oAuth2Uid) {
                myOAuth2Vo = myOAuth2
                break
            }
        }

        if (myOAuth2Vo == null) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        val isMemberEmailExists =
            db1RaillyLinkerCompanyService1MemberEmailDataRepository.existsByService1MemberDataAndRowDeleteDateStr(
                memberData,
                "/"
            )

        val isMemberPhoneExists =
            db1RaillyLinkerCompanyService1MemberPhoneDataRepository.existsByService1MemberDataAndRowDeleteDateStr(
                memberData,
                "/"
            )

        if (myOAuth2List.size > 1 ||
            (memberData.accountPassword != null && isMemberEmailExists) ||
            (memberData.accountPassword != null && isMemberPhoneExists)
        ) {
            // 로그인 정보 지우기
            db1RaillyLinkerCompanyService1MemberOauth2LoginDataRepository.deleteById(myOAuth2Vo.uid!!)

            httpServletResponse.status = HttpStatus.OK.value()
            return
        } else {
            // 이외에 사용 가능한 로그인 정보가 존재하지 않을 때
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "2")
            return
        }
    }


    ////
    @CustomTransactional([Db1MainConfig.TRANSACTION_NAME])
    override fun withdrawalMembership(
        httpServletResponse: HttpServletResponse,
        authorization: String
    ) {
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )
        val memberData = db1RaillyLinkerCompanyService1MemberDataRepository.findById(memberUid).get()

        // member_phone, member_email, member_role, member_sns_oauth2, member_profile, loginAccessToken 비활성화

        // !!!회원과 관계된 처리!!
        // cascade 설정이 되어있으므로 memberData 를 참조중인 테이블은 자동으로 삭제됩니다. 파일같은 경우에는 수동으로 처리하세요.
//        val profileData = memberProfileDataRepository.findAllByMemberData(memberData)
//        for (profile in profileData) {
//            // !!!프로필 이미지 파일 삭제하세요!!!
//        }

        // 이미 발행된 토큰 만료처리
        val tokenEntityList =
            db1RaillyLinkerCompanyService1LogInTokenHistoryRepository.findAllByService1MemberDataAndAccessTokenExpireWhenAfterAndRowDeleteDateStr(
                memberData,
                LocalDateTime.now(),
                "/"
            )
        for (tokenEntity in tokenEntityList) {
            val tokenType = tokenEntity.tokenType
            val accessToken = tokenEntity.accessToken

            val accessTokenExpireRemainSeconds = when (tokenType) {
                "Bearer" -> {
                    jwtTokenUtil.getRemainSeconds(accessToken)
                }

                else -> {
                    null
                }
            }

            try {
                redis1Service1ForceExpireAuthorizationSet.saveKeyValue(
                    "${tokenType}_${accessToken}",
                    Redis1_Map_Service1ForceExpireAuthorizationSet.ValueVo(),
                    accessTokenExpireRemainSeconds!! * 1000
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // 회원탈퇴 처리
        db1RaillyLinkerCompanyService1MemberDataRepository.deleteById(memberData.uid!!)

        httpServletResponse.status = HttpStatus.OK.value()
    }


    ////
    override fun getMyProfileList(
        httpServletResponse: HttpServletResponse,
        authorization: String
    ): MyServiceTkAuthController.GetMyProfileListOutputVo? {
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )
        val memberData = db1RaillyLinkerCompanyService1MemberDataRepository.findById(memberUid).get()

        val profileData =
            db1RaillyLinkerCompanyService1MemberProfileDataRepository.findAllByService1MemberDataAndRowDeleteDateStr(
                memberData,
                "/"
            )

        val myProfileList: ArrayList<MyServiceTkAuthController.GetMyProfileListOutputVo.ProfileInfo> =
            ArrayList()
        for (profile in profileData) {
            myProfileList.add(
                MyServiceTkAuthController.GetMyProfileListOutputVo.ProfileInfo(
                    profile.uid!!,
                    profile.imageFullUrl,
                    profile.uid == memberData.frontService1MemberProfileData?.uid
                )
            )
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return MyServiceTkAuthController.GetMyProfileListOutputVo(
            myProfileList
        )
    }


    ////
    override fun getMyFrontProfile(
        httpServletResponse: HttpServletResponse,
        authorization: String
    ): MyServiceTkAuthController.GetMyFrontProfileOutputVo? {
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )
        val memberData = db1RaillyLinkerCompanyService1MemberDataRepository.findById(memberUid).get()

        val profileData =
            db1RaillyLinkerCompanyService1MemberProfileDataRepository.findAllByService1MemberDataAndRowDeleteDateStr(
                memberData,
                "/"
            )

        var myProfile: MyServiceTkAuthController.GetMyFrontProfileOutputVo.ProfileInfo? = null
        for (profile in profileData) {
            if (profile.uid!! == memberData.frontService1MemberProfileData?.uid) {
                myProfile = MyServiceTkAuthController.GetMyFrontProfileOutputVo.ProfileInfo(
                    profile.uid!!,
                    profile.imageFullUrl
                )
                break
            }
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return MyServiceTkAuthController.GetMyFrontProfileOutputVo(
            myProfile
        )
    }


    ////
    @CustomTransactional([Db1MainConfig.TRANSACTION_NAME])
    override fun setMyFrontProfile(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        profileUid: Long?
    ) {
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )
        val memberData = db1RaillyLinkerCompanyService1MemberDataRepository.findById(memberUid).get()

        // 내 프로필 리스트 가져오기
        val profileDataList =
            db1RaillyLinkerCompanyService1MemberProfileDataRepository.findAllByService1MemberDataAndRowDeleteDateStr(
                memberData,
                "/"
            )

        if (profileDataList.isEmpty()) {
            // 내 프로필이 하나도 없을 때
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        if (profileUid == null) {
            memberData.frontService1MemberProfileData = null
            db1RaillyLinkerCompanyService1MemberDataRepository.save(memberData)

            httpServletResponse.status = HttpStatus.OK.value()
            return
        }

        // 이번에 선택하려는 프로필
        var selectedProfile: Db1_RaillyLinkerCompany_Service1MemberProfileData? = null
        for (profile in profileDataList) {
            if (profileUid == profile.uid) {
                selectedProfile = profile
            }
        }

        if (selectedProfile == null) {
            // 이번에 선택하려는 프로필이 없을 때
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        // 이번에 선택하려는 프로필을 선택하기
        memberData.frontService1MemberProfileData = selectedProfile
        db1RaillyLinkerCompanyService1MemberDataRepository.save(memberData)

        httpServletResponse.status = HttpStatus.OK.value()
    }


    ////
    @CustomTransactional([Db1MainConfig.TRANSACTION_NAME])
    override fun deleteMyProfile(
        authorization: String,
        httpServletResponse: HttpServletResponse,
        profileUid: Long
    ) {
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )
        val memberData = db1RaillyLinkerCompanyService1MemberDataRepository.findById(memberUid).get()

        // 프로필 가져오기
        val profileData =
            db1RaillyLinkerCompanyService1MemberProfileDataRepository.findByUidAndService1MemberDataAndRowDeleteDateStr(
                profileUid,
                memberData,
                "/"
            )

        if (profileData == null) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        // 프로필 비활성화
        db1RaillyLinkerCompanyService1MemberProfileDataRepository.deleteById(profileData.uid!!)
        // !!!프로필 이미지 파일 삭제하세요!!!

        if (memberData.frontService1MemberProfileData?.uid == profileUid) {
            // 대표 프로필을 삭제했을 때 멤버 데이터에 반영
            memberData.frontService1MemberProfileData = null
            db1RaillyLinkerCompanyService1MemberDataRepository.save(memberData)
        }

        httpServletResponse.status = HttpStatus.OK.value()
    }


    ////
    @CustomTransactional([Db1MainConfig.TRANSACTION_NAME])
    override fun addNewProfile(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        inputVo: MyServiceTkAuthController.AddNewProfileInputVo
    ): MyServiceTkAuthController.AddNewProfileOutputVo? {
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )
        val memberData = db1RaillyLinkerCompanyService1MemberDataRepository.findById(memberUid).get()

        // 저장된 프로필 이미지 파일을 다운로드 할 수 있는 URL
        val savedProfileImageUrl: String

        // 프로필 이미지 파일 저장

        //----------------------------------------------------------------------------------------------------------
        // 프로필 이미지를 서버 스토리지에 저장할 때 사용하는 방식
        // 파일 저장 기본 디렉토리 경로
        val saveDirectoryPath: Path = Paths.get("./by_product_files/member/profile").toAbsolutePath().normalize()

        // 파일 저장 기본 디렉토리 생성
        Files.createDirectories(saveDirectoryPath)

        // 원본 파일명(with suffix)
        val multiPartFileNameString = StringUtils.cleanPath(inputVo.profileImageFile.originalFilename!!)

        // 파일 확장자 구분 위치
        val fileExtensionSplitIdx = multiPartFileNameString.lastIndexOf('.')

        // 확장자가 없는 파일명
        val fileNameWithOutExtension: String
        // 확장자
        val fileExtension: String

        if (fileExtensionSplitIdx == -1) {
            fileNameWithOutExtension = multiPartFileNameString
            fileExtension = ""
        } else {
            fileNameWithOutExtension = multiPartFileNameString.substring(0, fileExtensionSplitIdx)
            fileExtension =
                multiPartFileNameString.substring(fileExtensionSplitIdx + 1, multiPartFileNameString.length)
        }

        val savedFileName = "${fileNameWithOutExtension}(${
            LocalDateTime.now().atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_'T'_HH_mm_ss_SSS_z"))
        }).$fileExtension"

        // multipartFile 을 targetPath 에 저장
        inputVo.profileImageFile.transferTo(
            // 파일 저장 경로와 파일명(with index) 을 합친 path 객체
            saveDirectoryPath.resolve(savedFileName).normalize()
        )

        savedProfileImageUrl = "${externalAccessAddress}/service1/tk/v1/auth/member-profile/$savedFileName"
        //----------------------------------------------------------------------------------------------------------

        val profileData = db1RaillyLinkerCompanyService1MemberProfileDataRepository.save(
            Db1_RaillyLinkerCompany_Service1MemberProfileData(
                memberData,
                savedProfileImageUrl
            )
        )

        if (inputVo.frontProfile) {
            memberData.frontService1MemberProfileData = profileData
            db1RaillyLinkerCompanyService1MemberDataRepository.save(memberData)
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return MyServiceTkAuthController.AddNewProfileOutputVo(
            profileData.uid!!,
            profileData.imageFullUrl
        )
    }


    ////
    override fun downloadProfileFile(
        httpServletResponse: HttpServletResponse,
        fileName: String
    ): ResponseEntity<Resource>? {
        // 프로젝트 루트 경로 (프로젝트 settings.gradle 이 있는 경로)
        val projectRootAbsolutePathString: String = File("").absolutePath

        // 파일 절대 경로 및 파일명
        val serverFilePathObject =
            Paths.get("$projectRootAbsolutePathString/by_product_files/member/profile/$fileName")

        when {
            Files.isDirectory(serverFilePathObject) -> {
                // 파일이 디렉토리일때
                httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                httpServletResponse.setHeader("api-result-code", "1")
                return null
            }

            Files.notExists(serverFilePathObject) -> {
                // 파일이 없을 때
                httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                httpServletResponse.setHeader("api-result-code", "1")
                return null
            }
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return ResponseEntity<Resource>(
            InputStreamResource(Files.newInputStream(serverFilePathObject)),
            HttpHeaders().apply {
                this.contentDisposition = ContentDisposition.builder("attachment")
                    .filename(fileName, StandardCharsets.UTF_8)
                    .build()
                this.add(HttpHeaders.CONTENT_TYPE, Files.probeContentType(serverFilePathObject))
            },
            HttpStatus.OK
        )
    }


    ////
    override fun getMyFrontEmail(
        httpServletResponse: HttpServletResponse,
        authorization: String
    ): MyServiceTkAuthController.GetMyFrontEmailOutputVo? {
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )
        val memberData = db1RaillyLinkerCompanyService1MemberDataRepository.findById(memberUid).get()

        val emailData =
            db1RaillyLinkerCompanyService1MemberEmailDataRepository.findAllByService1MemberDataAndRowDeleteDateStr(
                memberData,
                "/"
            )

        var myEmail: MyServiceTkAuthController.GetMyFrontEmailOutputVo.EmailInfo? = null
        for (email in emailData) {
            if (email.uid!! == memberData.frontService1MemberEmailData?.uid) {
                myEmail = MyServiceTkAuthController.GetMyFrontEmailOutputVo.EmailInfo(
                    email.uid!!,
                    email.emailAddress
                )
                break
            }
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return MyServiceTkAuthController.GetMyFrontEmailOutputVo(
            myEmail
        )
    }


    ////
    @CustomTransactional([Db1MainConfig.TRANSACTION_NAME])
    override fun setMyFrontEmail(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        emailUid: Long?
    ) {
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )
        val memberData = db1RaillyLinkerCompanyService1MemberDataRepository.findById(memberUid).get()

        // 내 이메일 리스트 가져오기
        val emailDataList =
            db1RaillyLinkerCompanyService1MemberEmailDataRepository.findAllByService1MemberDataAndRowDeleteDateStr(
                memberData,
                "/"
            )

        if (emailDataList.isEmpty()) {
            // 내 이메일이 하나도 없을 때
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        if (emailUid == null) {
            memberData.frontService1MemberEmailData = null
            db1RaillyLinkerCompanyService1MemberDataRepository.save(memberData)

            httpServletResponse.status = HttpStatus.OK.value()
            return
        }

        // 이번에 선택하려는 이메일
        var selectedEmail: Db1_RaillyLinkerCompany_Service1MemberEmailData? = null
        for (email in emailDataList) {
            if (emailUid == email.uid) {
                selectedEmail = email
            }
        }

        if (selectedEmail == null) {
            // 이번에 선택하려는 이메일이 없을 때
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        // 이번에 선택하려는 프로필을 선택하기
        memberData.frontService1MemberEmailData = selectedEmail
        db1RaillyLinkerCompanyService1MemberDataRepository.save(memberData)

        httpServletResponse.status = HttpStatus.OK.value()
    }


    ////
    override fun getMyFrontPhoneNumber(
        httpServletResponse: HttpServletResponse,
        authorization: String
    ): MyServiceTkAuthController.GetMyFrontPhoneNumberOutputVo? {
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )
        val memberData = db1RaillyLinkerCompanyService1MemberDataRepository.findById(memberUid).get()

        val phoneNumberData =
            db1RaillyLinkerCompanyService1MemberPhoneDataRepository.findAllByService1MemberDataAndRowDeleteDateStr(
                memberData,
                "/"
            )

        var myPhone: MyServiceTkAuthController.GetMyFrontPhoneNumberOutputVo.PhoneNumberInfo? = null
        for (phone in phoneNumberData) {
            if (phone.uid!! == memberData.frontService1MemberPhoneData?.uid) {
                myPhone = MyServiceTkAuthController.GetMyFrontPhoneNumberOutputVo.PhoneNumberInfo(
                    phone.uid!!,
                    phone.phoneNumber
                )
                break
            }
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return MyServiceTkAuthController.GetMyFrontPhoneNumberOutputVo(
            myPhone
        )
    }


    ////
    @CustomTransactional([Db1MainConfig.TRANSACTION_NAME])
    override fun setMyFrontPhoneNumber(
        httpServletResponse: HttpServletResponse,
        authorization: String,
        phoneNumberUid: Long?
    ) {
        val memberUid = jwtTokenUtil.getMemberUid(
            authorization.split(" ")[1].trim(),
            AUTH_JWT_CLAIMS_AES256_INITIALIZATION_VECTOR,
            AUTH_JWT_CLAIMS_AES256_ENCRYPTION_KEY
        )
        val memberData = db1RaillyLinkerCompanyService1MemberDataRepository.findById(memberUid).get()

        // 내 전화번호 리스트 가져오기
        val phoneNumberData =
            db1RaillyLinkerCompanyService1MemberPhoneDataRepository.findAllByService1MemberDataAndRowDeleteDateStr(
                memberData,
                "/"
            )

        if (phoneNumberData.isEmpty()) {
            // 내 전화번호가 하나도 없을 때
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        if (phoneNumberUid == null) {
            memberData.frontService1MemberPhoneData = null
            db1RaillyLinkerCompanyService1MemberDataRepository.save(memberData)

            httpServletResponse.status = HttpStatus.OK.value()
            return
        }

        // 이번에 선택하려는 전화번호
        var selectedPhone: Db1_RaillyLinkerCompany_Service1MemberPhoneData? = null
        for (phone in phoneNumberData) {
            if (phoneNumberUid == phone.uid) {
                selectedPhone = phone
            }
        }

        if (selectedPhone == null) {
            // 이번에 선택하려는 전화번호가 없을 때
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        // 이번에 선택하려는 프로필을 선택하기
        memberData.frontService1MemberPhoneData = selectedPhone
        db1RaillyLinkerCompanyService1MemberDataRepository.save(memberData)

        httpServletResponse.status = HttpStatus.OK.value()
    }


    ////
    override fun selectAllRedisKeyValueSample(httpServletResponse: HttpServletResponse): MyServiceTkAuthController.SelectAllRedisKeyValueSampleOutputVo? {
        // 전체 조회 테스트
        val keyValueList = redis1Service1ForceExpireAuthorizationSet.findAllKeyValues()

        val testEntityListVoList =
            ArrayList<MyServiceTkAuthController.SelectAllRedisKeyValueSampleOutputVo.KeyValueVo>()
        for (keyValue in keyValueList) {
            testEntityListVoList.add(
                MyServiceTkAuthController.SelectAllRedisKeyValueSampleOutputVo.KeyValueVo(
                    keyValue.key,
                    keyValue.expireTimeMs
                )
            )
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return MyServiceTkAuthController.SelectAllRedisKeyValueSampleOutputVo(
            testEntityListVoList
        )
    }
}
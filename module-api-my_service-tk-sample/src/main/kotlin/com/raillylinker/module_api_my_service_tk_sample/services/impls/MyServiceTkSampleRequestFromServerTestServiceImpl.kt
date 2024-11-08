package com.raillylinker.module_api_my_service_tk_sample.services.impls

import com.google.gson.Gson
import com.raillylinker.module_api_my_service_tk_sample.controllers.MyServiceTkSampleRequestFromServerTestController
import com.raillylinker.module_api_my_service_tk_sample.services.MyServiceTkSampleRequestFromServerTestService
import com.raillylinker.module_common.classes.SseClient
import com.raillylinker.module_retrofit2.retrofit2_classes.RepositoryNetworkRetrofit2
import com.raillylinker.module_retrofit2.retrofit2_classes.request_apis.LocalHostRequestApi
import jakarta.servlet.http.HttpServletResponse
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okio.ByteString
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.io.File
import java.net.SocketTimeoutException
import java.nio.file.Paths

@Service
class MyServiceTkSampleRequestFromServerTestServiceImpl(
    // (프로젝트 실행시 사용 설정한 프로필명 (ex : dev8080, prod80, local8080, 설정 안하면 default 반환))
    @Value("\${spring.profiles.active:default}") private var activeProfile: String
) : MyServiceTkSampleRequestFromServerTestService {
    // <멤버 변수 공간>
    private val classLogger: Logger = LoggerFactory.getLogger(this::class.java)

    // Retrofit2 요청 객체
    val networkRetrofit2: RepositoryNetworkRetrofit2 = RepositoryNetworkRetrofit2.getInstance()


    // ---------------------------------------------------------------------------------------------
    // <공개 메소드 공간>
    override fun basicRequestTest(httpServletResponse: HttpServletResponse): String? {
        try {
            // 네트워크 요청
            val responseObj = networkRetrofit2.localHostRequestApi.getMyServiceTkSampleRequestTest().execute()

            return if (responseObj.code() == 200) {
                // 정상 동작
                httpServletResponse.status = HttpStatus.OK.value()
                responseObj.body()!!
            } else {
                // 반환될 일 없는 상태 = 서버측 에러
                httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                httpServletResponse.setHeader("api-result-code", "2")
                return null
            }
        } catch (e: SocketTimeoutException) {
            // 타임아웃 에러 처리 = 런타임에서 처리해야하는 유일한 클라이언트 측 에러
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return null
        }
    }


    ////
    override fun redirectTest(httpServletResponse: HttpServletResponse): String? {
        try {
            // 네트워크 요청
            val responseObj =
                networkRetrofit2.localHostRequestApi.getMyServiceTkSampleRequestTestRedirectToBlank().execute()

            return if (responseObj.code() == 200) {
                // 정상 동작
                httpServletResponse.status = HttpStatus.OK.value()
                responseObj.body()!!
            } else {
                // 반환될 일 없는 상태 = 서버측 에러
                httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                httpServletResponse.setHeader("api-result-code", "2")
                return null
            }
        } catch (e: SocketTimeoutException) {
            // 타임아웃 에러 처리 = 런타임에서 처리해야하는 유일한 클라이언트 측 에러
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return null
        }
    }


    ////
    override fun forwardTest(httpServletResponse: HttpServletResponse): String? {
        try {
            // 네트워크 요청
            val responseObj =
                networkRetrofit2.localHostRequestApi.getMyServiceTkSampleRequestTestForwardToBlank().execute()

            return if (responseObj.code() == 200) {
                // 정상 동작
                httpServletResponse.status = HttpStatus.OK.value()
                responseObj.body()!!
            } else {
                // 반환될 일 없는 상태 = 서버측 에러
                httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                httpServletResponse.setHeader("api-result-code", "2")
                return null
            }
        } catch (e: SocketTimeoutException) {
            // 타임아웃 에러 처리 = 런타임에서 처리해야하는 유일한 클라이언트 측 에러
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return null
        }
    }


    ////
    override fun getRequestTest(httpServletResponse: HttpServletResponse): MyServiceTkSampleRequestFromServerTestController.GetRequestTestOutputVo? {
        try {
            // 네트워크 요청
            val responseObj = networkRetrofit2.localHostRequestApi.getMyServiceTkSampleRequestTestGetRequest(
                "paramFromServer",
                null,
                1,
                null,
                1.1,
                null,
                true,
                null,
                listOf("paramFromServer"),
                null
            ).execute()

            return if (responseObj.code() == 200) {
                // 정상 동작
                httpServletResponse.status = HttpStatus.OK.value()
                val responseBody = responseObj.body()!!
                MyServiceTkSampleRequestFromServerTestController.GetRequestTestOutputVo(
                    responseBody.queryParamString,
                    responseBody.queryParamStringNullable,
                    responseBody.queryParamInt,
                    responseBody.queryParamIntNullable,
                    responseBody.queryParamDouble,
                    responseBody.queryParamDoubleNullable,
                    responseBody.queryParamBoolean,
                    responseBody.queryParamBooleanNullable,
                    responseBody.queryParamStringList,
                    responseBody.queryParamStringListNullable
                )
            } else {
                // 반환될 일 없는 상태 = 서버측 에러
                httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                httpServletResponse.setHeader("api-result-code", "2")
                return null
            }
        } catch (e: SocketTimeoutException) {
            // 타임아웃 에러 처리 = 런타임에서 처리해야하는 유일한 클라이언트 측 에러
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return null
        }
    }


    ////
    override fun getRequestTestWithPathParam(httpServletResponse: HttpServletResponse): MyServiceTkSampleRequestFromServerTestController.GetRequestTestWithPathParamOutputVo? {
        try {
            // 네트워크 요청
            val responseObj =
                networkRetrofit2.localHostRequestApi.getMyServiceTkSampleRequestTestGetRequestPathParamInt(
                    1234
                ).execute()

            return if (responseObj.code() == 200) {
                // 정상 동작
                httpServletResponse.status = HttpStatus.OK.value()
                val responseBody = responseObj.body()!!
                MyServiceTkSampleRequestFromServerTestController.GetRequestTestWithPathParamOutputVo(
                    responseBody.pathParamInt
                )
            } else {
                // 반환될 일 없는 상태 = 서버측 에러
                httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                httpServletResponse.setHeader("api-result-code", "2")
                return null
            }
        } catch (e: SocketTimeoutException) {
            // 타임아웃 에러 처리 = 런타임에서 처리해야하는 유일한 클라이언트 측 에러
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return null
        }
    }


    ////
    override fun postRequestTestWithApplicationJsonTypeRequestBody(httpServletResponse: HttpServletResponse): MyServiceTkSampleRequestFromServerTestController.PostRequestTestWithApplicationJsonTypeRequestBodyOutputVo? {
        try {
            // 네트워크 요청
            val responseObj =
                networkRetrofit2.localHostRequestApi.postMyServiceTkSampleRequestTestPostRequestApplicationJson(
                    LocalHostRequestApi.PostMyServiceTkSampleRequestTestPostRequestApplicationJsonInputVO(
                        "paramFromServer",
                        null,
                        1,
                        null,
                        1.1,
                        null,
                        true,
                        null,
                        listOf("paramFromServer"),
                        null
                    )
                ).execute()

            return if (responseObj.code() == 200) {
                // 정상 동작
                httpServletResponse.status = HttpStatus.OK.value()
                val responseBody = responseObj.body()!!
                MyServiceTkSampleRequestFromServerTestController.PostRequestTestWithApplicationJsonTypeRequestBodyOutputVo(
                    responseBody.requestBodyString,
                    responseBody.requestBodyStringNullable,
                    responseBody.requestBodyInt,
                    responseBody.requestBodyIntNullable,
                    responseBody.requestBodyDouble,
                    responseBody.requestBodyDoubleNullable,
                    responseBody.requestBodyBoolean,
                    responseBody.requestBodyBooleanNullable,
                    responseBody.requestBodyStringList,
                    responseBody.requestBodyStringListNullable
                )
            } else {
                // 반환될 일 없는 상태 = 서버측 에러
                httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                httpServletResponse.setHeader("api-result-code", "2")
                return null
            }
        } catch (e: SocketTimeoutException) {
            // 타임아웃 에러 처리 = 런타임에서 처리해야하는 유일한 클라이언트 측 에러
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return null
        }
    }


    ////
    override fun postRequestTestWithFormTypeRequestBody(httpServletResponse: HttpServletResponse): MyServiceTkSampleRequestFromServerTestController.PostRequestTestWithFormTypeRequestBodyOutputVo? {
        try {
            // 네트워크 요청
            val responseObj =
                networkRetrofit2.localHostRequestApi.postMyServiceTkSampleRequestTestPostRequestXWwwFormUrlencoded(
                    "paramFromServer",
                    null,
                    1,
                    null,
                    1.1,
                    null,
                    true,
                    null,
                    listOf("paramFromServer"),
                    null
                ).execute()

            return if (responseObj.code() == 200) {
                // 정상 동작
                httpServletResponse.status = HttpStatus.OK.value()
                val responseBody = responseObj.body()!!
                MyServiceTkSampleRequestFromServerTestController.PostRequestTestWithFormTypeRequestBodyOutputVo(
                    responseBody.requestFormString,
                    responseBody.requestFormStringNullable,
                    responseBody.requestFormInt,
                    responseBody.requestFormIntNullable,
                    responseBody.requestFormDouble,
                    responseBody.requestFormDoubleNullable,
                    responseBody.requestFormBoolean,
                    responseBody.requestFormBooleanNullable,
                    responseBody.requestFormStringList,
                    responseBody.requestFormStringListNullable
                )
            } else {
                // 반환될 일 없는 상태 = 서버측 에러
                httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                httpServletResponse.setHeader("api-result-code", "2")
                return null
            }
        } catch (e: SocketTimeoutException) {
            // 타임아웃 에러 처리 = 런타임에서 처리해야하는 유일한 클라이언트 측 에러
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return null
        }
    }


    ////
    override fun postRequestTestWithMultipartFormTypeRequestBody(httpServletResponse: HttpServletResponse): MyServiceTkSampleRequestFromServerTestController.PostRequestTestWithMultipartFormTypeRequestBodyOutputVo? {
        try {
            // 네트워크 요청
            val requestFormString = MultipartBody.Part.createFormData("requestFormString", "paramFromServer")
            val requestFormInt = MultipartBody.Part.createFormData("requestFormInt", 1.toString())
            val requestFormDouble =
                MultipartBody.Part.createFormData("requestFormDouble", 1.1.toString())
            val requestFormBoolean =
                MultipartBody.Part.createFormData("requestFormBoolean", true.toString())

            val requestFormStringList = ArrayList<MultipartBody.Part>()
            for (requestFormString1 in listOf("paramFromServer")) {
                requestFormStringList.add(
                    MultipartBody.Part.createFormData("requestFormStringList", requestFormString1)
                )
            }

            // 전송 하려는 File
            val serverFile =
                Paths.get("${File("").absolutePath}/module-api-my_service-tk-sample/src/main/resources/static/post_request_test_with_multipart_form_type_request_body/test.txt")
                    .toFile()
            val multipartFileFormData = MultipartBody.Part.createFormData(
                "multipartFile",
                serverFile.name,
                serverFile.asRequestBody(serverFile.toURI().toURL().openConnection().contentType.toMediaTypeOrNull())
            )

            val responseObj =
                networkRetrofit2.localHostRequestApi.postMyServiceTkSampleRequestTestPostRequestMultipartFormData(
                    requestFormString,
                    null,
                    requestFormInt,
                    null,
                    requestFormDouble,
                    null,
                    requestFormBoolean,
                    null,
                    requestFormStringList,
                    null,
                    multipartFileFormData,
                    null
                ).execute()

            return if (responseObj.code() == 200) {
                // 정상 동작
                httpServletResponse.status = HttpStatus.OK.value()
                val responseBody = responseObj.body()!!
                MyServiceTkSampleRequestFromServerTestController.PostRequestTestWithMultipartFormTypeRequestBodyOutputVo(
                    responseBody.requestFormString,
                    responseBody.requestFormStringNullable,
                    responseBody.requestFormInt,
                    responseBody.requestFormIntNullable,
                    responseBody.requestFormDouble,
                    responseBody.requestFormDoubleNullable,
                    responseBody.requestFormBoolean,
                    responseBody.requestFormBooleanNullable,
                    responseBody.requestFormStringList,
                    responseBody.requestFormStringListNullable
                )
            } else {
                // 반환될 일 없는 상태 = 서버측 에러
                httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                httpServletResponse.setHeader("api-result-code", "2")
                return null
            }
        } catch (e: SocketTimeoutException) {
            // 타임아웃 에러 처리 = 런타임에서 처리해야하는 유일한 클라이언트 측 에러
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return null
        }
    }


    ////
    override fun postRequestTestWithMultipartFormTypeRequestBody2(httpServletResponse: HttpServletResponse): MyServiceTkSampleRequestFromServerTestController.PostRequestTestWithMultipartFormTypeRequestBody2OutputVo? {
        try {
            // 네트워크 요청
            val requestFormString = MultipartBody.Part.createFormData("requestFormString", "paramFromServer")
            val requestFormInt = MultipartBody.Part.createFormData("requestFormInt", 1.toString())
            val requestFormDouble =
                MultipartBody.Part.createFormData("requestFormDouble", 1.1.toString())
            val requestFormBoolean =
                MultipartBody.Part.createFormData("requestFormBoolean", true.toString())

            val requestFormStringList = ArrayList<MultipartBody.Part>()
            for (requestFormString1 in listOf("paramFromServer")) {
                requestFormStringList.add(
                    MultipartBody.Part.createFormData("requestFormStringList", requestFormString1)
                )
            }

            // 전송 하려는 File
            val serverFile1 =
                Paths.get("${File("").absolutePath}/module-api-my_service-tk-sample/src/main/resources/static/post_request_test_with_multipart_form_type_request_body2/test1.txt")
                    .toFile()
            val serverFile2 =
                Paths.get("${File("").absolutePath}/module-api-my_service-tk-sample/src/main/resources/static/post_request_test_with_multipart_form_type_request_body2/test2.txt")
                    .toFile()

            val multipartFileListFormData = listOf(
                MultipartBody.Part.createFormData(
                    "multipartFileList",
                    serverFile1.name,
                    serverFile1.asRequestBody(
                        serverFile1.toURI().toURL().openConnection().contentType.toMediaTypeOrNull()
                    )
                ),
                MultipartBody.Part.createFormData(
                    "multipartFileList",
                    serverFile2.name,
                    serverFile2.asRequestBody(
                        serverFile2.toURI().toURL().openConnection().contentType.toMediaTypeOrNull()
                    )
                )
            )

            val responseObj =
                networkRetrofit2.localHostRequestApi.postMyServiceTkSampleRequestTestPostRequestMultipartFormData2(
                    requestFormString,
                    null,
                    requestFormInt,
                    null,
                    requestFormDouble,
                    null,
                    requestFormBoolean,
                    null,
                    requestFormStringList,
                    null,
                    multipartFileListFormData,
                    null
                ).execute()

            return if (responseObj.code() == 200) {
                // 정상 동작
                httpServletResponse.status = HttpStatus.OK.value()
                val responseBody = responseObj.body()!!
                MyServiceTkSampleRequestFromServerTestController.PostRequestTestWithMultipartFormTypeRequestBody2OutputVo(
                    responseBody.requestFormString,
                    responseBody.requestFormStringNullable,
                    responseBody.requestFormInt,
                    responseBody.requestFormIntNullable,
                    responseBody.requestFormDouble,
                    responseBody.requestFormDoubleNullable,
                    responseBody.requestFormBoolean,
                    responseBody.requestFormBooleanNullable,
                    responseBody.requestFormStringList,
                    responseBody.requestFormStringListNullable
                )
            } else {
                // 반환될 일 없는 상태 = 서버측 에러
                httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                httpServletResponse.setHeader("api-result-code", "2")
                return null
            }
        } catch (e: SocketTimeoutException) {
            // 타임아웃 에러 처리 = 런타임에서 처리해야하는 유일한 클라이언트 측 에러
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return null
        }
    }


    ////
    override fun postRequestTestWithMultipartFormTypeRequestBody3(httpServletResponse: HttpServletResponse): MyServiceTkSampleRequestFromServerTestController.PostRequestTestWithMultipartFormTypeRequestBody3OutputVo? {
        try {
            // 네트워크 요청
            val jsonStringFormData = MultipartBody.Part.createFormData(
                "jsonString", Gson().toJson(
                    LocalHostRequestApi.PostMyServiceTkSampleRequestTestPostRequestMultipartFormDataJsonJsonStringVo(
                        "paramFromServer",
                        null,
                        1,
                        null,
                        1.1,
                        null,
                        true,
                        null,
                        listOf("paramFromServer"),
                        null
                    )
                )
            )

            // 전송 하려는 File
            val serverFile =
                Paths.get("${File("").absolutePath}/module-api-my_service-tk-sample/src/main/resources/static/post_request_test_with_multipart_form_type_request_body3/test.txt")
                    .toFile()
            val multipartFileFormData = MultipartBody.Part.createFormData(
                "multipartFile",
                serverFile.name,
                serverFile.asRequestBody(serverFile.toURI().toURL().openConnection().contentType.toMediaTypeOrNull())
            )

            val responseObj =
                networkRetrofit2.localHostRequestApi.postMyServiceTkSampleRequestTestPostRequestMultipartFormDataJson(
                    jsonStringFormData,
                    multipartFileFormData,
                    null
                ).execute()

            return if (responseObj.code() == 200) {
                // 정상 동작
                httpServletResponse.status = HttpStatus.OK.value()
                val responseBody = responseObj.body()!!
                MyServiceTkSampleRequestFromServerTestController.PostRequestTestWithMultipartFormTypeRequestBody3OutputVo(
                    responseBody.requestFormString,
                    responseBody.requestFormStringNullable,
                    responseBody.requestFormInt,
                    responseBody.requestFormIntNullable,
                    responseBody.requestFormDouble,
                    responseBody.requestFormDoubleNullable,
                    responseBody.requestFormBoolean,
                    responseBody.requestFormBooleanNullable,
                    responseBody.requestFormStringList,
                    responseBody.requestFormStringListNullable
                )
            } else {
                // 반환될 일 없는 상태 = 서버측 에러
                httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                httpServletResponse.setHeader("api-result-code", "2")
                return null
            }
        } catch (e: SocketTimeoutException) {
            // 타임아웃 에러 처리 = 런타임에서 처리해야하는 유일한 클라이언트 측 에러
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return null
        }
    }


    ////
    override fun generateErrorTest(httpServletResponse: HttpServletResponse) {
        try {
            // 네트워크 요청
            val responseObj =
                networkRetrofit2.localHostRequestApi.postMyServiceTkSampleRequestTestGenerateError().execute()

            return if (responseObj.code() == 200) {
                // 정상 동작
                httpServletResponse.status = HttpStatus.OK.value()
            } else {
                // 반환될 일 없는 상태 = 서버측 에러
                httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                httpServletResponse.setHeader("api-result-code", "2")
            }
        } catch (e: SocketTimeoutException) {
            // 타임아웃 에러 처리 = 런타임에서 처리해야하는 유일한 클라이언트 측 에러
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
        }
    }


    ////
    override fun returnResultCodeThroughHeaders(httpServletResponse: HttpServletResponse) {
        try {
            // 네트워크 요청
            val responseObj = networkRetrofit2.localHostRequestApi.postMyServiceTkSampleRequestTestApiResultCodeTest(
                LocalHostRequestApi.PostMyServiceTkSampleRequestTestApiResultCodeTestErrorTypeEnum.A
            ).execute()

            if (responseObj.code() == 200) {
                // 정상 동작
                httpServletResponse.status = HttpStatus.OK.value()
            } else if (responseObj.code() == 204) {
                // api-result-code 확인 필요

                val responseHeaders = responseObj.headers()
                val apiResultCode = responseHeaders["api-result-code"]

                // api-result-code 분기
                when (apiResultCode) {
                    "1" -> {
                        httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                        httpServletResponse.setHeader("api-result-code", "3")
                    }

                    "2" -> {
                        httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                        httpServletResponse.setHeader("api-result-code", "4")
                    }

                    "3" -> {
                        httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                        httpServletResponse.setHeader("api-result-code", "5")
                    }

                    else -> {
                        // 알수없는 api-result-code
                        httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                        httpServletResponse.setHeader("api-result-code", "2")
                    }
                }
            } else {
                // 반환될 일 없는 상태 = 서버측 에러
                httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                httpServletResponse.setHeader("api-result-code", "2")
            }
        } catch (e: SocketTimeoutException) {
            // 타임아웃 에러 처리 = 런타임에서 처리해야하는 유일한 클라이언트 측 에러
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
        }
    }


    ////
    override fun responseDelayTest(httpServletResponse: HttpServletResponse, delayTimeSec: Long) {
        try {
            // 네트워크 요청
            val responseObj =
                networkRetrofit2.localHostRequestApi.postMyServiceTkSampleRequestTestGenerateTimeOutError(delayTimeSec)
                    .execute()

            return if (responseObj.code() == 200) {
                // 정상 동작
                httpServletResponse.status = HttpStatus.OK.value()
            } else {
                // 반환될 일 없는 상태 = 서버측 에러
                httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                httpServletResponse.setHeader("api-result-code", "2")
            }
        } catch (e: SocketTimeoutException) {
            // 타임아웃 에러 처리 = 런타임에서 처리해야하는 유일한 클라이언트 측 에러
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
        }
    }


    ////
    override fun returnTextStringTest(httpServletResponse: HttpServletResponse): String? {
        try {
            // 네트워크 요청
            val responseObj =
                networkRetrofit2.localHostRequestApi.getMyServiceTkSampleRequestTestReturnTextString().execute()

            return if (responseObj.code() == 200) {
                // 정상 동작
                httpServletResponse.status = HttpStatus.OK.value()
                responseObj.body()!!
            } else {
                // 반환될 일 없는 상태 = 서버측 에러
                httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                httpServletResponse.setHeader("api-result-code", "2")
                return null
            }
        } catch (e: SocketTimeoutException) {
            // 타임아웃 에러 처리 = 런타임에서 처리해야하는 유일한 클라이언트 측 에러
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return null
        }
    }


    ////
    override fun returnTextHtmlTest(httpServletResponse: HttpServletResponse): String? {
        try {
            // 네트워크 요청
            val responseObj =
                networkRetrofit2.localHostRequestApi.getMyServiceTkSampleRequestTestReturnTextHtml().execute()

            return if (responseObj.code() == 200) {
                // 정상 동작
                httpServletResponse.status = HttpStatus.OK.value()
                responseObj.body()!!
            } else {
                // 반환될 일 없는 상태 = 서버측 에러
                httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                httpServletResponse.setHeader("api-result-code", "2")
                return null
            }
        } catch (e: SocketTimeoutException) {
            // 타임아웃 에러 처리 = 런타임에서 처리해야하는 유일한 클라이언트 측 에러
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return null
        }
    }


    ////
    override fun asynchronousResponseTest(httpServletResponse: HttpServletResponse): MyServiceTkSampleRequestFromServerTestController.AsynchronousResponseTestOutputVo? {
        try {
            // 네트워크 요청
            val responseObj =
                networkRetrofit2.localHostRequestApi.getMyServiceTkSampleRequestTestAsyncResult().execute()

            return if (responseObj.code() == 200) {
                // 정상 동작
                httpServletResponse.status = HttpStatus.OK.value()
                val responseBody = responseObj.body()!!
                MyServiceTkSampleRequestFromServerTestController.AsynchronousResponseTestOutputVo(
                    responseBody.resultMessage
                )
            } else {
                // 반환될 일 없는 상태 = 서버측 에러
                httpServletResponse.status = HttpStatus.NO_CONTENT.value()
                httpServletResponse.setHeader("api-result-code", "2")
                return null
            }
        } catch (e: SocketTimeoutException) {
            // 타임아웃 에러 처리 = 런타임에서 처리해야하는 유일한 클라이언트 측 에러
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return null
        }
    }


    ////
    override fun sseSubscribeTest(httpServletResponse: HttpServletResponse) {
        // SSE Subscribe Url 연결 객체 생성
        val sseClient =
            SseClient("http://127.0.0.1:8080/my-service/tk/sample/request-test/sse-test/subscribe")

        val maxCount = 5
        var count = 0

        // SSE 구독 연결
        sseClient.connect(
            5000,
            object : SseClient.ListenerCallback {
                override fun onConnectRequestFirstTime(sse: SseClient, originalRequest: Request) {
                    classLogger.info("++++ api17 : onConnectRequestFirstTime")
                }

                override fun onConnect(sse: SseClient, response: Response) {
                    classLogger.info("++++ api17 : onOpen")
                }

                override fun onMessageReceive(
                    sse: SseClient,
                    eventId: String?,
                    event: String,
                    message: String
                ) {
                    classLogger.info("++++ api17 : onMessage : $event $message")

                    // maxCount 만큼 반복했다면 연결 끊기
                    if (maxCount == count++) {
                        sseClient.disconnect()
                    }
                }

                override fun onCommentReceive(sse: SseClient, comment: String) {
                    classLogger.info("++++ api17 : onComment : $comment")
                }

                override fun onDisconnected(sse: SseClient) {
                    classLogger.info("++++ api17 : onClosed")
                }

                override fun onPreRetry(
                    sse: SseClient,
                    originalRequest: Request,
                    throwable: Throwable,
                    response: Response?
                ): Boolean {
                    classLogger.info("++++ api17 : onPreRetry")
                    return true
                }
            }
        )

        httpServletResponse.status = HttpStatus.OK.value()
    }


    ////
    override fun websocketConnectTest(httpServletResponse: HttpServletResponse) {
        val client = OkHttpClient()

        val maxCount = 5
        var count = 0

        val webSocket = client.newWebSocket(
            Request.Builder()
                .url("ws://localhost:8080/ws/test/websocket")
                .build(),
            object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    classLogger.info("++++ api18 : onOpen")
                    webSocket.send(
                        Gson().toJson(
                            MessagePayloadVo(
                                "++++ api18 Client",
                                "i am open!"
                            )
                        )
                    )
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    // maxCount 만큼 반복했다면 연결 끊기
                    if (maxCount == count++) {
                        webSocket.close(1000, null)
                    }

                    classLogger.info("++++ api18 : onMessage : $text")

                    // 메세지를 받으면 바로 메세지를 보내기
                    webSocket.send(
                        Gson().toJson(
                            MessagePayloadVo(
                                "++++ api18 Client",
                                "reply!"
                            )
                        )
                    )

                    // 웹소켓 종료시
//                    webSocket.close(1000, null)
                }

                override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                    classLogger.info("++++ api18 : onMessage : $bytes")
                }

                override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                    classLogger.info("++++ api18 : onClosing")
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    classLogger.info("vapi18 : onFailure")
                    t.printStackTrace()
                }
            }
        )
        client.dispatcher.executorService.shutdown()

        classLogger.info(webSocket.toString())

        httpServletResponse.status = HttpStatus.OK.value()
    }

    data class MessagePayloadVo(
        val sender: String, // 송신자 (실제로는 JWT 로 보안 처리를 할 것)
        val message: String
    )
}
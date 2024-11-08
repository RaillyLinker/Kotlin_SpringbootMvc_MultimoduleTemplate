package com.raillylinker.module_api_my_service_tk_sample.services

import com.raillylinker.module_api_my_service_tk_sample.controllers.Service1TkV1RequestTestController
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.io.Resource
import org.springframework.web.context.request.async.DeferredResult
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

interface Service1TkV1RequestTestService {
    // (기본 요청 테스트 API)
    fun basicRequestTest(httpServletResponse: HttpServletResponse): String?


    ////
    // (요청 Redirect 테스트)
    fun redirectTest(httpServletResponse: HttpServletResponse): ModelAndView?


    ////
    // (요청 Forward 테스트)
    fun forwardTest(httpServletResponse: HttpServletResponse): ModelAndView?


    ////
    // (Get 요청 테스트 (Query Parameter))
    fun getRequestTest(
        httpServletResponse: HttpServletResponse,
        queryParamString: String,
        queryParamStringNullable: String?,
        queryParamInt: Int,
        queryParamIntNullable: Int?,
        queryParamDouble: Double,
        queryParamDoubleNullable: Double?,
        queryParamBoolean: Boolean,
        queryParamBooleanNullable: Boolean?,
        queryParamStringList: List<String>,
        queryParamStringListNullable: List<String>?
    ): Service1TkV1RequestTestController.GetRequestTestOutputVo?


    ////
    // (Get 요청 테스트 (Path Parameter))
    fun getRequestTestWithPathParam(
        httpServletResponse: HttpServletResponse,
        pathParamInt: Int
    ): Service1TkV1RequestTestController.GetRequestTestWithPathParamOutputVo?


    ////
    // (Post 요청 테스트 (application-json))
    fun postRequestTestWithApplicationJsonTypeRequestBody(
        httpServletResponse: HttpServletResponse,
        inputVo: Service1TkV1RequestTestController.PostRequestTestWithApplicationJsonTypeRequestBodyInputVo
    ): Service1TkV1RequestTestController.PostRequestTestWithApplicationJsonTypeRequestBodyOutputVo?


    ////
    // (Post 요청 테스트 (application-json, 객체 파라미터 포함))
    fun postRequestTestWithApplicationJsonTypeRequestBody2(
        httpServletResponse: HttpServletResponse,
        inputVo: Service1TkV1RequestTestController.PostRequestTestWithApplicationJsonTypeRequestBody2InputVo
    ): Service1TkV1RequestTestController.PostRequestTestWithApplicationJsonTypeRequestBody2OutputVo?


    ////
    // (Post 요청 테스트 (입출력값 없음))
    fun postRequestTestWithNoInputAndOutput(
        httpServletResponse: HttpServletResponse
    )


    ////
    // (Post 요청 테스트 (x-www-form-urlencoded))
    fun postRequestTestWithFormTypeRequestBody(
        httpServletResponse: HttpServletResponse,
        inputVo: Service1TkV1RequestTestController.PostRequestTestWithFormTypeRequestBodyInputVo
    ): Service1TkV1RequestTestController.PostRequestTestWithFormTypeRequestBodyOutputVo?


    ////
    // (Post 요청 테스트 (multipart/form-data))
    fun postRequestTestWithMultipartFormTypeRequestBody(
        httpServletResponse: HttpServletResponse,
        inputVo: Service1TkV1RequestTestController.PostRequestTestWithMultipartFormTypeRequestBodyInputVo
    ): Service1TkV1RequestTestController.PostRequestTestWithMultipartFormTypeRequestBodyOutputVo?


    ////
    // (Post 요청 테스트2 (multipart/form-data))
    fun postRequestTestWithMultipartFormTypeRequestBody2(
        httpServletResponse: HttpServletResponse,
        inputVo: Service1TkV1RequestTestController.PostRequestTestWithMultipartFormTypeRequestBody2InputVo
    ): Service1TkV1RequestTestController.PostRequestTestWithMultipartFormTypeRequestBody2OutputVo?


    ////
    // (Post 요청 테스트 (multipart/form-data - JsonString))
    fun postRequestTestWithMultipartFormTypeRequestBody3(
        httpServletResponse: HttpServletResponse,
        inputVo: Service1TkV1RequestTestController.PostRequestTestWithMultipartFormTypeRequestBody3InputVo
    ): Service1TkV1RequestTestController.PostRequestTestWithMultipartFormTypeRequestBody3OutputVo?


    ////
    // (인위적 에러 발생 테스트)
    fun generateErrorTest(httpServletResponse: HttpServletResponse)

    ////
    // (결과 코드 발생 테스트)
    fun returnResultCodeThroughHeaders(
        httpServletResponse: HttpServletResponse,
        errorType: Service1TkV1RequestTestController.ReturnResultCodeThroughHeadersErrorTypeEnum?
    )


    ////
    // (인위적 응답 지연 테스트)
    fun responseDelayTest(httpServletResponse: HttpServletResponse, delayTimeSec: Long)


    ////
    // (text/string 반환 샘플)
    fun returnTextStringTest(httpServletResponse: HttpServletResponse): String?


    ////
    // (text/html 반환 샘플)
    fun returnTextHtmlTest(httpServletResponse: HttpServletResponse): ModelAndView?


    ////
    // (byte 반환 샘플)
    fun returnByteDataTest(httpServletResponse: HttpServletResponse): Resource?


    ////
    // (비디오 스트리밍 샘플)
    fun videoStreamingTest(
        videoHeight: Service1TkV1RequestTestController.VideoStreamingTestVideoHeight,
        httpServletResponse: HttpServletResponse
    ): Resource?


    ////
    // (오디오 스트리밍 샘플)
    fun audioStreamingTest(httpServletResponse: HttpServletResponse): Resource?


    ////
    // (비동기 처리 결과 반환 샘플)
    fun asynchronousResponseTest(httpServletResponse: HttpServletResponse): DeferredResult<Service1TkV1RequestTestController.AsynchronousResponseTestOutputVo>?


    ////
    // (클라이언트가 특정 SSE 이벤트를 구독)
    fun sseTestSubscribe(httpServletResponse: HttpServletResponse, lastSseEventId: String?): SseEmitter?


    ////
    // (SSE 이벤트 전송 트리거 테스트)
    fun sseTestEventTrigger(httpServletResponse: HttpServletResponse)


    ////
    // (빈 리스트 받기 테스트)
    fun emptyListRequestTest(
        httpServletResponse: HttpServletResponse,
        stringList: List<String>,
        inputVo: Service1TkV1RequestTestController.EmptyListRequestTestInputVo
    ): Service1TkV1RequestTestController.EmptyListRequestTestOutputVo?
}
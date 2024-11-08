package com.raillylinker.module_api_my_service_tk_sample.services

import com.raillylinker.module_api_my_service_tk_sample.controllers.MyServiceTkSampleRequestFromServerTestController
import jakarta.servlet.http.HttpServletResponse

interface MyServiceTkSampleRequestFromServerTestService {
    // (기본 요청 테스트)
    fun basicRequestTest(httpServletResponse: HttpServletResponse): String?


    ////
    // (Redirect 테스트)
    fun redirectTest(httpServletResponse: HttpServletResponse): String?


    ////
    // (Forward 테스트)
    fun forwardTest(httpServletResponse: HttpServletResponse): String?


    ////
    // (Get 요청 테스트 (Query Parameter))
    fun getRequestTest(httpServletResponse: HttpServletResponse): MyServiceTkSampleRequestFromServerTestController.GetRequestTestOutputVo?


    ////
    // (Get 요청 테스트 (Path Parameter))
    fun getRequestTestWithPathParam(httpServletResponse: HttpServletResponse): MyServiceTkSampleRequestFromServerTestController.GetRequestTestWithPathParamOutputVo?


    ////
    // (Post 요청 테스트 (Request Body, application/json))
    fun postRequestTestWithApplicationJsonTypeRequestBody(httpServletResponse: HttpServletResponse): MyServiceTkSampleRequestFromServerTestController.PostRequestTestWithApplicationJsonTypeRequestBodyOutputVo?


    ////
    // (Post 요청 테스트 (Request Body, x-www-form-urlencoded))
    fun postRequestTestWithFormTypeRequestBody(httpServletResponse: HttpServletResponse): MyServiceTkSampleRequestFromServerTestController.PostRequestTestWithFormTypeRequestBodyOutputVo?


    ////
    // (Post 요청 테스트 (Request Body, multipart/form-data))
    fun postRequestTestWithMultipartFormTypeRequestBody(httpServletResponse: HttpServletResponse): MyServiceTkSampleRequestFromServerTestController.PostRequestTestWithMultipartFormTypeRequestBodyOutputVo?


    ////
    // (Post 요청 테스트 (Request Body, multipart/form-data, MultipartFile List))
    fun postRequestTestWithMultipartFormTypeRequestBody2(httpServletResponse: HttpServletResponse): MyServiceTkSampleRequestFromServerTestController.PostRequestTestWithMultipartFormTypeRequestBody2OutputVo?


    ////
    // (Post 요청 테스트 (Request Body, multipart/form-data, with jsonString))
    fun postRequestTestWithMultipartFormTypeRequestBody3(httpServletResponse: HttpServletResponse): MyServiceTkSampleRequestFromServerTestController.PostRequestTestWithMultipartFormTypeRequestBody3OutputVo?


    ////
    // (에러 발생 테스트)
    fun generateErrorTest(httpServletResponse: HttpServletResponse)


    ////
    // (api-result-code 반환 테스트)
    fun returnResultCodeThroughHeaders(httpServletResponse: HttpServletResponse)


    ////
    // (응답 지연 발생 테스트)
    fun responseDelayTest(httpServletResponse: HttpServletResponse, delayTimeSec: Long)


    ////
    // (text/string 형식 Response 받아오기)
    fun returnTextStringTest(httpServletResponse: HttpServletResponse): String?


    ////
    // (text/html 형식 Response 받아오기)
    fun returnTextHtmlTest(httpServletResponse: HttpServletResponse): String?


    ////
    // (DeferredResult Get 요청 테스트)
    fun asynchronousResponseTest(httpServletResponse: HttpServletResponse): MyServiceTkSampleRequestFromServerTestController.AsynchronousResponseTestOutputVo?


    ////
    fun sseSubscribeTest(httpServletResponse: HttpServletResponse)


//    ////
    fun websocketConnectTest(httpServletResponse: HttpServletResponse)
}
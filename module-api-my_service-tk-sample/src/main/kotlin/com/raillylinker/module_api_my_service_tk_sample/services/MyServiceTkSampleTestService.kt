package com.raillylinker.module_api_my_service_tk_sample.services

import com.raillylinker.module_api_my_service_tk_sample.controllers.MyServiceTkSampleTestController
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.io.Resource
import org.springframework.http.ResponseEntity

interface MyServiceTkSampleTestService {
    // (이메일 발송 테스트)
    fun sendEmailTest(
        httpServletResponse: HttpServletResponse,
        inputVo: MyServiceTkSampleTestController.SendEmailTestInputVo
    )


    ////
    // (HTML 이메일 발송 테스트)
    fun sendHtmlEmailTest(
        httpServletResponse: HttpServletResponse,
        inputVo: MyServiceTkSampleTestController.SendHtmlEmailTestInputVo
    )


    ////
    // (Naver API SMS 발송 샘플)
    fun naverSmsSample(
        httpServletResponse: HttpServletResponse,
        inputVo: MyServiceTkSampleTestController.NaverSmsSampleInputVo
    )


    ////
    // (Naver API AlimTalk 발송 샘플)
    fun naverAlimTalkSample(
        httpServletResponse: HttpServletResponse,
        inputVo: MyServiceTkSampleTestController.NaverAlimTalkSampleInputVo
    )


    ////
    // (액셀 파일을 받아서 해석 후 데이터 반환)
    fun readExcelFileSample(
        httpServletResponse: HttpServletResponse,
        inputVo: MyServiceTkSampleTestController.ReadExcelFileSampleInputVo
    ): MyServiceTkSampleTestController.ReadExcelFileSampleOutputVo?


    ////
    // (액셀 파일 쓰기)
    fun writeExcelFileSample(httpServletResponse: HttpServletResponse)


    ////
    // (HTML 을 기반으로 PDF 를 생성)
    fun htmlToPdfSample(
        httpServletResponse: HttpServletResponse
    ): ResponseEntity<Resource>?


    ////
    // (입력받은 HTML 을 기반으로 PDF 를 생성 후 반환)
    fun multipartHtmlToPdfSample(
        httpServletResponse: HttpServletResponse,
        inputVo: MyServiceTkSampleTestController.MultipartHtmlToPdfSampleInputVo,
        controllerBasicMapping: String?
    ): ResponseEntity<Resource>?


    ////
    // (by_product_files/uploads/fonts 폴더에서 파일 다운받기)
    fun downloadFontFile(
        httpServletResponse: HttpServletResponse,
        fileName: String
    ): ResponseEntity<Resource>?


    ////
    // (Kafka 토픽 메세지 발행 테스트)
    fun sendKafkaTopicMessageTest(
        httpServletResponse: HttpServletResponse,
        inputVo: MyServiceTkSampleTestController.SendKafkaTopicMessageTestInputVo
    )


    ////
    // (ProcessBuilder 샘플)
    fun processBuilderTest(
        httpServletResponse: HttpServletResponse,
        javaEnvironmentPath: String?
    ): MyServiceTkSampleTestController.ProcessBuilderTestOutputVo?


    ////
    // (입력받은 폰트 파일의 내부 이름을 반환)
    fun checkFontFileInnerName(
        httpServletResponse: HttpServletResponse,
        inputVo: MyServiceTkSampleTestController.CheckFontFileInnerNameInputVo
    ): MyServiceTkSampleTestController.CheckFontFileInnerNameOutputVo?


    ////
    // (AES256 암호화 테스트)
    fun aes256EncryptTest(
        httpServletResponse: HttpServletResponse,
        plainText: String,
        alg: MyServiceTkSampleTestController.Aes256EncryptTestCryptoAlgEnum,
        initializationVector: String,
        encryptionKey: String
    ): MyServiceTkSampleTestController.Aes256EncryptTestOutputVo?


    ////
    // (AES256 복호화 테스트)
    fun aes256DecryptTest(
        httpServletResponse: HttpServletResponse,
        encryptedText: String,
        alg: MyServiceTkSampleTestController.Aes256DecryptTestCryptoAlgEnum,
        initializationVector: String,
        encryptionKey: String
    ): MyServiceTkSampleTestController.Aes256DecryptTestOutputVo?


    ////
    // (Jsoup 태그 조작 테스트)
    fun jsoupTest(httpServletResponse: HttpServletResponse, fix: Boolean): String?
}
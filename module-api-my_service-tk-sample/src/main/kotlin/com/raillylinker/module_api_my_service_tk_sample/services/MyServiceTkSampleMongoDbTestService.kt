package com.raillylinker.module_api_my_service_tk_sample.services

import com.raillylinker.module_api_my_service_tk_sample.controllers.MyServiceTkSampleMongoDbTestController
import jakarta.servlet.http.HttpServletResponse

interface MyServiceTkSampleMongoDbTestService {
    // (DB document 입력 테스트 API)
    fun insertDocumentTest(
        httpServletResponse: HttpServletResponse,
        inputVo: MyServiceTkSampleMongoDbTestController.InsertDocumentTestInputVo
    ): MyServiceTkSampleMongoDbTestController.InsertDocumentTestOutputVo?


    ////
    // (DB Rows 삭제 테스트 API)
    fun deleteAllDocumentTest(httpServletResponse: HttpServletResponse)


    ////
    // (DB Row 삭제 테스트)
    fun deleteDocumentTest(httpServletResponse: HttpServletResponse, id: String)


    ////
    // (DB Rows 조회 테스트)
    fun selectAllDocumentsTest(httpServletResponse: HttpServletResponse): MyServiceTkSampleMongoDbTestController.SelectAllDocumentsTestOutputVo?


    ////
    // (트랜젝션 동작 테스트)
    fun transactionRollbackTest(
        httpServletResponse: HttpServletResponse
    )


    ////
    // (트랜젝션 비동작 테스트)
    fun noTransactionRollbackTest(
        httpServletResponse: HttpServletResponse
    )
}
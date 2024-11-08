package com.raillylinker.module_api_my_service_tk_sample.services

import com.raillylinker.module_api_my_service_tk_sample.controllers.C7Service1TkV1DatabaseTestController
import jakarta.servlet.http.HttpServletResponse

interface C7Service1TkV1DatabaseTestService {
    // (DB Row 입력 테스트 API)
    fun api1InsertDataSample(
        httpServletResponse: HttpServletResponse,
        inputVo: C7Service1TkV1DatabaseTestController.Api1InsertDataSampleInputVo
    ): C7Service1TkV1DatabaseTestController.Api1InsertDataSampleOutputVo?


    ////
    // (DB Rows 삭제 테스트 API)
    fun api2DeleteRowsSample(httpServletResponse: HttpServletResponse, deleteLogically: Boolean)


    ////
    // (DB Row 삭제 테스트)
    fun api3DeleteRowSample(httpServletResponse: HttpServletResponse, index: Long, deleteLogically: Boolean)


    ////
    fun api4SelectRowsSample(httpServletResponse: HttpServletResponse): C7Service1TkV1DatabaseTestController.Api4SelectRowsSampleOutputVo?


    ////
    fun api5SelectRowsOrderByRandomNumSample(
        httpServletResponse: HttpServletResponse,
        num: Int
    ): C7Service1TkV1DatabaseTestController.Api5SelectRowsOrderByRandomNumSampleOutputVo?


    ////
    fun api6SelectRowsOrderByRowCreateDateSample(
        httpServletResponse: HttpServletResponse,
        dateString: String
    ): C7Service1TkV1DatabaseTestController.Api6SelectRowsOrderByRowCreateDateSampleOutputVo?


    ////
    fun api7SelectRowsPageSample(
        httpServletResponse: HttpServletResponse,
        page: Int,
        pageElementsCount: Int
    ): C7Service1TkV1DatabaseTestController.Api7SelectRowsPageSampleOutputVo?


    ////
    fun api8SelectRowsNativeQueryPageSample(
        httpServletResponse: HttpServletResponse,
        page: Int,
        pageElementsCount: Int,
        num: Int
    ): C7Service1TkV1DatabaseTestController.Api8SelectRowsNativeQueryPageSampleOutputVo?


    ////
    fun api9UpdateRowSample(
        httpServletResponse: HttpServletResponse,
        testTableUid: Long,
        inputVo: C7Service1TkV1DatabaseTestController.Api9UpdateRowSampleInputVo
    ): C7Service1TkV1DatabaseTestController.Api9UpdateRowSampleOutputVo?


    ////
    fun api10UpdateRowNativeQuerySample(
        httpServletResponse: HttpServletResponse,
        testTableUid: Long,
        inputVo: C7Service1TkV1DatabaseTestController.Api10UpdateRowNativeQuerySampleInputVo
    )


    ////
    fun api11SelectRowWhereSearchingKeywordSample(
        httpServletResponse: HttpServletResponse,
        page: Int,
        pageElementsCount: Int,
        searchKeyword: String
    ): C7Service1TkV1DatabaseTestController.Api11SelectRowWhereSearchingKeywordSampleOutputVo?


    ////
    fun api12TransactionTest(
        httpServletResponse: HttpServletResponse
    )


    ////
    fun api13NonTransactionTest(httpServletResponse: HttpServletResponse)


    ////
    fun api13Dot1TryCatchNonTransactionTest(httpServletResponse: HttpServletResponse)


    ////
    fun api14SelectRowsNoDuplicatePagingSample(
        httpServletResponse: HttpServletResponse,
        lastItemUid: Long?,
        pageElementsCount: Int
    ): C7Service1TkV1DatabaseTestController.Api14SelectRowsNoDuplicatePagingSampleOutputVo?


    ////
    fun api15SelectRowsCountSample(httpServletResponse: HttpServletResponse): C7Service1TkV1DatabaseTestController.Api15SelectRowsCountSampleOutputVo?


    ////
    fun api16SelectRowsCountByNativeQuerySample(httpServletResponse: HttpServletResponse): C7Service1TkV1DatabaseTestController.Api16SelectRowsCountByNativeQuerySampleOutputVo?


    ////
    fun api17SelectRowByNativeQuerySample(
        httpServletResponse: HttpServletResponse,
        testTableUid: Long
    ): C7Service1TkV1DatabaseTestController.Api17SelectRowByNativeQuerySampleOutputVo?


    ////
    fun api18InsertUniqueTestTableRowSample(
        httpServletResponse: HttpServletResponse,
        inputVo: C7Service1TkV1DatabaseTestController.Api18InsertUniqueTestTableRowSampleInputVo
    ): C7Service1TkV1DatabaseTestController.Api18InsertUniqueTestTableRowSampleOutputVo?


    ////
    fun api19SelectUniqueTestTableRowsSample(httpServletResponse: HttpServletResponse): C7Service1TkV1DatabaseTestController.Api19SelectUniqueTestTableRowsSampleOutputVo?


    ////
    fun api20UpdateUniqueTestTableRowSample(
        httpServletResponse: HttpServletResponse,
        testTableUid: Long,
        inputVo: C7Service1TkV1DatabaseTestController.Api20UpdateUniqueTestTableRowSampleInputVo
    ): C7Service1TkV1DatabaseTestController.Api20UpdateUniqueTestTableRowSampleOutputVo?


    ////
    fun api21DeleteUniqueTestTableRowSample(httpServletResponse: HttpServletResponse, index: Long)


    ////
    fun api22InsertFkParentRowSample(
        httpServletResponse: HttpServletResponse,
        inputVo: C7Service1TkV1DatabaseTestController.Api22InsertFkParentRowSampleInputVo
    ): C7Service1TkV1DatabaseTestController.Api22InsertFkParentRowSampleOutputVo?


    ////
    fun api23InsertFkChildRowSample(
        httpServletResponse: HttpServletResponse,
        parentUid: Long,
        inputVo: C7Service1TkV1DatabaseTestController.Api23InsertFkChildRowSampleInputVo
    ): C7Service1TkV1DatabaseTestController.Api23InsertFkChildRowSampleOutputVo?


    ////
    fun api24SelectFkTestTableRowsSample(httpServletResponse: HttpServletResponse): C7Service1TkV1DatabaseTestController.Api24SelectFkTestTableRowsSampleOutputVo?


    ////
    fun api24Dot1SelectFkTestTableRowsByNativeQuerySample(httpServletResponse: HttpServletResponse): C7Service1TkV1DatabaseTestController.Api24SelectFkTestTableRowsByNativeQuerySampleDot1OutputVo?


    ////
    fun api25GetNativeQueryReturnValueTest(
        httpServletResponse: HttpServletResponse,
        inputVal: Boolean
    ): C7Service1TkV1DatabaseTestController.Api25GetNativeQueryReturnValueTestOutputVo?


    ////
    fun api26SqlInjectionTest(
        httpServletResponse: HttpServletResponse,
        searchKeyword: String
    ): C7Service1TkV1DatabaseTestController.Api26SqlInjectionTestOutputVo?


    ////
    fun api27SelectFkTableRowsWithLatestChildSample(httpServletResponse: HttpServletResponse): C7Service1TkV1DatabaseTestController.Api27SelectFkTableRowsWithLatestChildSampleOutputVo?


    ////
    fun api28DeleteFkChildRowSample(httpServletResponse: HttpServletResponse, index: Long)


    ////
    fun api29DeleteFkParentRowSample(httpServletResponse: HttpServletResponse, index: Long)


    ////
    fun api35FkTableTransactionTest(
        httpServletResponse: HttpServletResponse
    )


    ////
    fun api36FkTableNonTransactionTest(httpServletResponse: HttpServletResponse)
}
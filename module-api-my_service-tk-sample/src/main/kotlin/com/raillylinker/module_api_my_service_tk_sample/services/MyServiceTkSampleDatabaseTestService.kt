package com.raillylinker.module_api_my_service_tk_sample.services

import com.raillylinker.module_api_my_service_tk_sample.controllers.MyServiceTkSampleDatabaseTestController
import jakarta.servlet.http.HttpServletResponse

interface MyServiceTkSampleDatabaseTestService {
    // (DB Row 입력 테스트 API)
    fun insertDataSample(
        httpServletResponse: HttpServletResponse,
        inputVo: MyServiceTkSampleDatabaseTestController.InsertDataSampleInputVo
    ): MyServiceTkSampleDatabaseTestController.InsertDataSampleOutputVo?


    ////
    // (DB Rows 삭제 테스트 API)
    fun deleteRowsSample(httpServletResponse: HttpServletResponse, deleteLogically: Boolean)


    ////
    // (DB Row 삭제 테스트)
    fun deleteRowSample(httpServletResponse: HttpServletResponse, index: Long, deleteLogically: Boolean)


    ////
    // (DB Rows 조회 테스트)
    fun selectRowsSample(httpServletResponse: HttpServletResponse): MyServiceTkSampleDatabaseTestController.SelectRowsSampleOutputVo?


    ////
    // (DB 테이블의 random_num 컬럼 근사치 기준으로 정렬한 리스트 조회 API)
    fun selectRowsOrderByRandomNumSample(
        httpServletResponse: HttpServletResponse,
        num: Int
    ): MyServiceTkSampleDatabaseTestController.SelectRowsOrderByRandomNumSampleOutputVo?


    ////
    // (DB 테이블의 row_create_date 컬럼 근사치 기준으로 정렬한 리스트 조회 API)
    fun selectRowsOrderByRowCreateDateSample(
        httpServletResponse: HttpServletResponse,
        dateString: String
    ): MyServiceTkSampleDatabaseTestController.SelectRowsOrderByRowCreateDateSampleOutputVo?


    ////
    // (DB Rows 조회 테스트 (페이징))
    fun selectRowsPageSample(
        httpServletResponse: HttpServletResponse,
        page: Int,
        pageElementsCount: Int
    ): MyServiceTkSampleDatabaseTestController.SelectRowsPageSampleOutputVo?


    ////
    // (DB Rows 조회 테스트 (네이티브 쿼리 페이징))
    fun selectRowsNativeQueryPageSample(
        httpServletResponse: HttpServletResponse,
        page: Int,
        pageElementsCount: Int,
        num: Int
    ): MyServiceTkSampleDatabaseTestController.SelectRowsNativeQueryPageSampleOutputVo?


    ////
    // (DB Row 수정 테스트)
    fun updateRowSample(
        httpServletResponse: HttpServletResponse,
        testTableUid: Long,
        inputVo: MyServiceTkSampleDatabaseTestController.UpdateRowSampleInputVo
    ): MyServiceTkSampleDatabaseTestController.UpdateRowSampleOutputVo?


    ////
    // (DB Row 수정 테스트 (네이티브 쿼리))
    fun updateRowNativeQuerySample(
        httpServletResponse: HttpServletResponse,
        testTableUid: Long,
        inputVo: MyServiceTkSampleDatabaseTestController.UpdateRowNativeQuerySampleInputVo
    )


    ////
    // (DB 정보 검색 테스트)
    fun selectRowWhereSearchingKeywordSample(
        httpServletResponse: HttpServletResponse,
        page: Int,
        pageElementsCount: Int,
        searchKeyword: String
    ): MyServiceTkSampleDatabaseTestController.SelectRowWhereSearchingKeywordSampleOutputVo?


    ////
    // (트랜젝션 동작 테스트)
    fun transactionTest(
        httpServletResponse: HttpServletResponse
    )


    ////
    // (트랜젝션 비동작 테스트)
    fun nonTransactionTest(httpServletResponse: HttpServletResponse)


    ////
    // (트랜젝션 비동작 테스트(try-catch))
    fun tryCatchNonTransactionTest(httpServletResponse: HttpServletResponse)


    ////
    // (DB Rows 조회 테스트 (중복 없는 네이티브 쿼리 페이징))
    fun selectRowsNoDuplicatePagingSample(
        httpServletResponse: HttpServletResponse,
        lastItemUid: Long?,
        pageElementsCount: Int
    ): MyServiceTkSampleDatabaseTestController.SelectRowsNoDuplicatePagingSampleOutputVo?


    ////
    // (DB Rows 조회 테스트 (카운팅))
    fun selectRowsCountSample(httpServletResponse: HttpServletResponse): MyServiceTkSampleDatabaseTestController.SelectRowsCountSampleOutputVo?


    ////
    // (DB Rows 조회 테스트 (네이티브 카운팅))
    fun selectRowsCountByNativeQuerySample(httpServletResponse: HttpServletResponse): MyServiceTkSampleDatabaseTestController.SelectRowsCountByNativeQuerySampleOutputVo?


    ////
    // (DB Row 조회 테스트 (네이티브))
    fun selectRowByNativeQuerySample(
        httpServletResponse: HttpServletResponse,
        testTableUid: Long
    ): MyServiceTkSampleDatabaseTestController.SelectRowByNativeQuerySampleOutputVo?


    ////
    // (유니크 테스트 테이블 Row 입력 API)
    fun insertUniqueTestTableRowSample(
        httpServletResponse: HttpServletResponse,
        inputVo: MyServiceTkSampleDatabaseTestController.InsertUniqueTestTableRowSampleInputVo
    ): MyServiceTkSampleDatabaseTestController.InsertUniqueTestTableRowSampleOutputVo?


    ////
    // (유니크 테스트 테이블 Rows 조회 테스트)
    fun selectUniqueTestTableRowsSample(httpServletResponse: HttpServletResponse): MyServiceTkSampleDatabaseTestController.SelectUniqueTestTableRowsSampleOutputVo?


    ////
    // (유니크 테스트 테이블 Row 수정 테스트)
    fun updateUniqueTestTableRowSample(
        httpServletResponse: HttpServletResponse,
        testTableUid: Long,
        inputVo: MyServiceTkSampleDatabaseTestController.UpdateUniqueTestTableRowSampleInputVo
    ): MyServiceTkSampleDatabaseTestController.UpdateUniqueTestTableRowSampleOutputVo?


    ////
    // (유니크 테스트 테이블 Row 삭제 테스트)
    fun deleteUniqueTestTableRowSample(httpServletResponse: HttpServletResponse, index: Long)


    ////
    // (외래키 부모 테이블 Row 입력 API)
    fun insertFkParentRowSample(
        httpServletResponse: HttpServletResponse,
        inputVo: MyServiceTkSampleDatabaseTestController.InsertFkParentRowSampleInputVo
    ): MyServiceTkSampleDatabaseTestController.InsertFkParentRowSampleOutputVo?


    ////
    // (외래키 부모 테이블 아래에 자식 테이블의 Row 입력 API)
    fun insertFkChildRowSample(
        httpServletResponse: HttpServletResponse,
        parentUid: Long,
        inputVo: MyServiceTkSampleDatabaseTestController.InsertFkChildRowSampleInputVo
    ): MyServiceTkSampleDatabaseTestController.InsertFkChildRowSampleOutputVo?


    ////
    // (외래키 관련 테이블 Rows 조회 테스트)
    fun selectFkTestTableRowsSample(httpServletResponse: HttpServletResponse): MyServiceTkSampleDatabaseTestController.SelectFkTestTableRowsSampleOutputVo?


    ////
    // (외래키 관련 테이블 Rows 조회 테스트(Native Join))
    fun selectFkTestTableRowsByNativeQuerySample(httpServletResponse: HttpServletResponse): MyServiceTkSampleDatabaseTestController.SelectFkTestTableRowsByNativeQuerySampleDot1OutputVo?


    ////
    // (Native Query 반환값 테스트)
    fun getNativeQueryReturnValueTest(
        httpServletResponse: HttpServletResponse,
        inputVal: Boolean
    ): MyServiceTkSampleDatabaseTestController.GetNativeQueryReturnValueTestOutputVo?


    ////
    // (SQL Injection 테스트)
    fun sqlInjectionTest(
        httpServletResponse: HttpServletResponse,
        searchKeyword: String
    ): MyServiceTkSampleDatabaseTestController.SqlInjectionTestOutputVo?


    ////
    // (외래키 관련 테이블 Rows 조회 (네이티브 쿼리, 부모 테이블을 자식 테이블의 가장 최근 데이터만 Join))
    fun selectFkTableRowsWithLatestChildSample(httpServletResponse: HttpServletResponse): MyServiceTkSampleDatabaseTestController.SelectFkTableRowsWithLatestChildSampleOutputVo?


    ////
    // (외래키 관련 테이블 Rows 조회 (QueryDsl))
    fun selectFkTableRowsWithQueryDsl(httpServletResponse: HttpServletResponse): MyServiceTkSampleDatabaseTestController.SelectFkTableRowsWithQueryDslOutputVo?


    ////
    // (외래키 관련 테이블 Rows 조회 및 부모 테이블 이름으로 필터링 (QueryDsl))
    fun selectFkTableRowsByParentNameFilterWithQueryDsl(
        httpServletResponse: HttpServletResponse,
        parentName: String
    ): MyServiceTkSampleDatabaseTestController.SelectFkTableRowsByParentNameFilterWithQueryDslOutputVo?


    ////
    // (외래키 관련 테이블 부모 테이블 고유번호로 자식 테이블 리스트 검색 (QueryDsl))
    fun selectFkTableChildListWithQueryDsl(
        httpServletResponse: HttpServletResponse,
        parentUid: Long
    ): MyServiceTkSampleDatabaseTestController.SelectFkTableChildListWithQueryDslOutputVo?


    ////
    // (외래키 자식 테이블 Row 삭제 테스트)
    fun deleteFkChildRowSample(httpServletResponse: HttpServletResponse, index: Long)


    ////
    // (외래키 부모 테이블 Row 삭제 테스트 (Cascade 기능 확인))
    fun deleteFkParentRowSample(httpServletResponse: HttpServletResponse, index: Long)


    ////
    // (외래키 테이블 트랜젝션 동작 테스트)
    fun fkTableTransactionTest(
        httpServletResponse: HttpServletResponse
    )


    ////
    // (외래키 테이블 트랜젝션 비동작 테스트)
    fun fkTableNonTransactionTest(httpServletResponse: HttpServletResponse)
}
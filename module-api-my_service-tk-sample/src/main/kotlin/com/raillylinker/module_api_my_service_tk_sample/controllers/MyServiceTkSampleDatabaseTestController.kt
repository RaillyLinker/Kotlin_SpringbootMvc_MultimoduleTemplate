package com.raillylinker.module_api_my_service_tk_sample.controllers

import com.fasterxml.jackson.annotation.JsonProperty
import com.raillylinker.module_api_my_service_tk_sample.services.MyServiceTkSampleDatabaseTestService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.headers.Header
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Tag(name = "/my-service/tk/sample/database-test APIs", description = "Database 에 대한 테스트 API 컨트롤러")
@Controller
@RequestMapping("/my-service/tk/sample/database-test")
class MyServiceTkSampleDatabaseTestController(
    private val service: MyServiceTkSampleDatabaseTestService
) {
    // <멤버 변수 공간>


    // ---------------------------------------------------------------------------------------------
    // <매핑 함수 공간>
    @Operation(
        summary = "DB Row 입력 테스트 API",
        description = "테스트 테이블에 Row 를 입력합니다.\n\n"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            )
        ]
    )
    @PostMapping(
        path = ["/row"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun insertDataSample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @RequestBody
        inputVo: InsertDataSampleInputVo
    ): InsertDataSampleOutputVo? {
        return service.insertDataSample(httpServletResponse, inputVo)
    }

    data class InsertDataSampleInputVo(
        @Schema(description = "글 본문", required = true, example = "테스트 텍스트입니다.")
        @JsonProperty("content")
        val content: String,
        @Schema(
            description = "원하는 날짜(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
            required = true,
            example = "2024_05_02_T_15_14_49_552_KST"
        )
        @JsonProperty("dateString")
        val dateString: String
    )

    data class InsertDataSampleOutputVo(
        @Schema(description = "글 고유번호", required = true, example = "1234")
        @JsonProperty("uid")
        val uid: Long,
        @Schema(description = "글 본문", required = true, example = "테스트 텍스트입니다.")
        @JsonProperty("content")
        val content: String,
        @Schema(description = "자동 생성 숫자", required = true, example = "21345")
        @JsonProperty("randomNum")
        val randomNum: Int,
        @Schema(
            description = "테스트용 일시 데이터(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
            required = true,
            example = "2024_05_02_T_15_14_49_552_KST"
        )
        @JsonProperty("testDatetime")
        val testDatetime: String,
        @Schema(
            description = "글 작성일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
            required = true,
            example = "2024_05_02_T_15_14_49_552_KST"
        )
        @JsonProperty("createDate")
        val createDate: String,
        @Schema(
            description = "글 수정일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
            required = true,
            example = "2024_05_02_T_15_14_49_552_KST"
        )
        @JsonProperty("updateDate")
        val updateDate: String,
        @Schema(description = "글 삭제일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z, Null 이면 /)", required = true, example = "/")
        @JsonProperty("deleteDate")
        val deleteDate: String
    )


    ////
    @Operation(
        summary = "DB Rows 삭제 테스트 API",
        description = "테스트 테이블의 모든 Row 를 모두 삭제합니다.\n\n"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            )
        ]
    )
    @DeleteMapping(
        path = ["/rows"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.ALL_VALUE]
    )
    @ResponseBody
    fun deleteRowsSample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(name = "deleteLogically", description = "논리적 삭제 여부", example = "true")
        @RequestParam("deleteLogically")
        deleteLogically: Boolean
    ) {
        service.deleteRowsSample(httpServletResponse, deleteLogically)
    }


    ////
    @Operation(
        summary = "DB Row 삭제 테스트",
        description = "테스트 테이블의 Row 하나를 삭제합니다.\n\n"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            ),
            ApiResponse(
                responseCode = "204",
                content = [Content()],
                description = "Response Body 가 없습니다.\n\n" +
                        "Response Headers 를 확인하세요.",
                headers = [
                    Header(
                        name = "api-result-code",
                        description = "(Response Code 반환 원인) - Required\n\n" +
                                "1 : index 에 해당하는 데이터가 데이터베이스에 존재하지 않습니다.\n\n",
                        schema = Schema(type = "string")
                    )
                ]
            )
        ]
    )
    @DeleteMapping(
        path = ["/row/{index}"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.ALL_VALUE]
    )
    @ResponseBody
    fun deleteRowSample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(name = "index", description = "글 인덱스", example = "1")
        @PathVariable("index")
        index: Long,
        @Parameter(name = "deleteLogically", description = "논리적 삭제 여부", example = "true")
        @RequestParam("deleteLogically")
        deleteLogically: Boolean
    ) {
        service.deleteRowSample(httpServletResponse, index, deleteLogically)
    }


    ////
    @Operation(
        summary = "DB Rows 조회 테스트",
        description = "테스트 테이블의 모든 Rows 를 반환합니다.\n\n"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            )
        ]
    )
    @GetMapping(
        path = ["/rows"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun selectRowsSample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse
    ): SelectRowsSampleOutputVo? {
        return service.selectRowsSample(httpServletResponse)
    }

    data class SelectRowsSampleOutputVo(
        @Schema(description = "아이템 리스트", required = true)
        @JsonProperty("testEntityVoList")
        val testEntityVoList: List<TestEntityVo>,

        @Schema(description = "논리적으로 제거된 아이템 리스트", required = true)
        @JsonProperty("logicalDeleteEntityVoList")
        val logicalDeleteEntityVoList: List<TestEntityVo>
    ) {
        @Schema(description = "아이템")
        data class TestEntityVo(
            @Schema(description = "글 고유번호", required = true, example = "1234")
            @JsonProperty("uid")
            val uid: Long,
            @Schema(description = "글 본문", required = true, example = "테스트 텍스트입니다.")
            @JsonProperty("content")
            val content: String,
            @Schema(description = "자동 생성 숫자", required = true, example = "21345")
            @JsonProperty("randomNum")
            val randomNum: Int,
            @Schema(
                description = "테스트용 일시 데이터(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
                required = true,
                example = "2024_05_02_T_15_14_49_552_KST"
            )
            @JsonProperty("testDatetime")
            val testDatetime: String,
            @Schema(
                description = "글 작성일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
                required = true,
                example = "2024_05_02_T_15_14_49_552_KST"
            )
            @JsonProperty("createDate")
            val createDate: String,
            @Schema(
                description = "글 수정일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
                required = true,
                example = "2024_05_02_T_15_14_49_552_KST"
            )
            @JsonProperty("updateDate")
            val updateDate: String,
            @Schema(description = "글 삭제일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z, Null 이면 /)", required = true, example = "/")
            @JsonProperty("deleteDate")
            val deleteDate: String
        )
    }


    ////
    @Operation(
        summary = "DB 테이블의 random_num 컬럼 근사치 기준으로 정렬한 리스트 조회 API",
        description = "테이블의 row 중 random_num 컬럼과 num 파라미터의 값의 근사치로 정렬한 리스트 반환\n\n"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            )
        ]
    )
    @GetMapping(
        path = ["/rows/order-by-random-num-nearest"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun selectRowsOrderByRandomNumSample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(name = "num", description = "근사값 정렬의 기준", example = "1")
        @RequestParam("num")
        num: Int
    ): SelectRowsOrderByRandomNumSampleOutputVo? {
        return service.selectRowsOrderByRandomNumSample(httpServletResponse, num)
    }

    data class SelectRowsOrderByRandomNumSampleOutputVo(
        @Schema(description = "아이템 리스트", required = true)
        @JsonProperty("testEntityVoList")
        val testEntityVoList: List<TestEntityVo>
    ) {
        @Schema(description = "아이템")
        data class TestEntityVo(
            @Schema(description = "글 고유번호", required = true, example = "1234")
            @JsonProperty("uid")
            val uid: Long,
            @Schema(description = "글 본문", required = true, example = "테스트 텍스트입니다.")
            @JsonProperty("content")
            val content: String,
            @Schema(description = "자동 생성 숫자", required = true, example = "21345")
            @JsonProperty("randomNum")
            val randomNum: Int,
            @Schema(
                description = "테스트용 일시 데이터(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
                required = true,
                example = "2024_05_02_T_15_14_49_552_KST"
            )
            @JsonProperty("testDatetime")
            val testDatetime: String,
            @Schema(
                description = "글 작성일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
                required = true,
                example = "2024_05_02_T_15_14_49_552_KST"
            )
            @JsonProperty("createDate")
            val createDate: String,
            @Schema(
                description = "글 수정일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
                required = true,
                example = "2024_05_02_T_15_14_49_552_KST"
            )
            @JsonProperty("updateDate")
            val updateDate: String,
            @Schema(description = "기준과의 절대거리", required = true, example = "34")
            @JsonProperty("distance")
            val distance: Int
        )
    }


    ////
    @Operation(
        summary = "DB 테이블의 row_create_date 컬럼 근사치 기준으로 정렬한 리스트 조회 API",
        description = "테이블의 row 중 row_create_date 컬럼과 dateString 파라미터의 값의 근사치로 정렬한 리스트 반환\n\n"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            )
        ]
    )
    @GetMapping(
        path = ["/rows/order-by-create-date-nearest"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun selectRowsOrderByRowCreateDateSample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(
            name = "dateString",
            description = "원하는 날짜(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
            example = "2024_05_02_T_15_14_49_552_KST"
        )
        @RequestParam("dateString")
        dateString: String
    ): SelectRowsOrderByRowCreateDateSampleOutputVo? {
        return service.selectRowsOrderByRowCreateDateSample(httpServletResponse, dateString)
    }

    data class SelectRowsOrderByRowCreateDateSampleOutputVo(
        @Schema(description = "아이템 리스트", required = true)
        @JsonProperty("testEntityVoList")
        val testEntityVoList: List<TestEntityVo>
    ) {
        @Schema(description = "아이템")
        data class TestEntityVo(
            @Schema(description = "글 고유번호", required = true, example = "1234")
            @JsonProperty("uid")
            val uid: Long,
            @Schema(description = "글 본문", required = true, example = "테스트 텍스트입니다.")
            @JsonProperty("content")
            val content: String,
            @Schema(description = "자동 생성 숫자", required = true, example = "21345")
            @JsonProperty("randomNum")
            val randomNum: Int,
            @Schema(
                description = "테스트용 일시 데이터(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
                required = true,
                example = "2024_05_02_T_15_14_49_552_KST"
            )
            @JsonProperty("testDatetime")
            val testDatetime: String,
            @Schema(
                description = "글 작성일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
                required = true,
                example = "2024_05_02_T_15_14_49_552_KST"
            )
            @JsonProperty("createDate")
            val createDate: String,
            @Schema(
                description = "글 수정일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
                required = true,
                example = "2024_05_02_T_15_14_49_552_KST"
            )
            @JsonProperty("updateDate")
            val updateDate: String,
            @Schema(description = "기준과의 절대차이(마이크로 초)", required = true, example = "34")
            @JsonProperty("timeDiffMicroSec")
            val timeDiffMicroSec: Long
        )
    }


    ////
    @Operation(
        summary = "DB Rows 조회 테스트 (페이징)",
        description = "테스트 테이블의 Rows 를 페이징하여 반환합니다.\n\n"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            )
        ]
    )
    @GetMapping(
        path = ["/rows/paging"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun selectRowsPageSample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(name = "page", description = "원하는 페이지(1 부터 시작)", example = "1")
        @RequestParam("page")
        page: Int,
        @Parameter(name = "pageElementsCount", description = "페이지 아이템 개수", example = "10")
        @RequestParam("pageElementsCount")
        pageElementsCount: Int
    ): SelectRowsPageSampleOutputVo? {
        return service.selectRowsPageSample(httpServletResponse, page, pageElementsCount)
    }

    data class SelectRowsPageSampleOutputVo(
        @Schema(description = "아이템 전체 개수", required = true, example = "100")
        @JsonProperty("totalElements")
        val totalElements: Long,
        @Schema(description = "아이템 리스트", required = true)
        @JsonProperty("testEntityVoList")
        val testEntityVoList: List<TestEntityVo>
    ) {
        @Schema(description = "아이템")
        data class TestEntityVo(
            @Schema(description = "글 고유번호", required = true, example = "1234")
            @JsonProperty("uid")
            val uid: Long,
            @Schema(description = "글 본문", required = true, example = "테스트 텍스트입니다.")
            @JsonProperty("content")
            val content: String,
            @Schema(description = "자동 생성 숫자", required = true, example = "23456")
            @JsonProperty("randomNum")
            val randomNum: Int,
            @Schema(
                description = "테스트용 일시 데이터(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
                required = true,
                example = "2024_05_02_T_15_14_49_552_KST"
            )
            @JsonProperty("testDatetime")
            val testDatetime: String,
            @Schema(
                description = "글 작성일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
                required = true,
                example = "2024_05_02_T_15_14_49_552_KST"
            )
            @JsonProperty("createDate")
            val createDate: String,
            @Schema(
                description = "글 수정일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
                required = true,
                example = "2024_05_02_T_15_14_49_552_KST"
            )
            @JsonProperty("updateDate")
            val updateDate: String
        )
    }


    ////
    @Operation(
        summary = "DB Rows 조회 테스트 (네이티브 쿼리 페이징)",
        description = "테스트 테이블의 Rows 를 네이티브 쿼리로 페이징하여 반환합니다.\n\n" +
                "num 을 기준으로 근사치 정렬도 수행합니다.\n\n"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            )
        ]
    )
    @GetMapping(
        path = ["/rows/native-paging"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun selectRowsNativeQueryPageSample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(name = "page", description = "원하는 페이지(1 부터 시작)", example = "1")
        @RequestParam("page")
        page: Int,
        @Parameter(name = "pageElementsCount", description = "페이지 아이템 개수", example = "10")
        @RequestParam("pageElementsCount")
        pageElementsCount: Int,
        @Parameter(name = "num", description = "근사값의 기준", example = "1")
        @RequestParam("num")
        num: Int
    ): SelectRowsNativeQueryPageSampleOutputVo? {
        return service.selectRowsNativeQueryPageSample(httpServletResponse, page, pageElementsCount, num)
    }

    data class SelectRowsNativeQueryPageSampleOutputVo(
        @Schema(description = "아이템 전체 개수", required = true, example = "100")
        @JsonProperty("totalElements")
        val totalElements: Long,
        @Schema(description = "아이템 리스트", required = true)
        @JsonProperty("testEntityVoList")
        val testEntityVoList: List<TestEntityVo>
    ) {
        @Schema(description = "아이템")
        data class TestEntityVo(
            @Schema(description = "글 고유번호", required = true, example = "1")
            @JsonProperty("uid")
            val uid: Long,
            @Schema(description = "글 본문", required = true, example = "테스트 텍스트입니다.")
            @JsonProperty("content")
            val content: String,
            @Schema(description = "자동 생성 숫자", required = true, example = "21345")
            @JsonProperty("randomNum")
            val randomNum: Int,
            @Schema(
                description = "테스트용 일시 데이터(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
                required = true,
                example = "2024_05_02_T_15_14_49_552_KST"
            )
            @JsonProperty("testDatetime")
            val testDatetime: String,
            @Schema(
                description = "글 작성일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
                required = true,
                example = "2024_05_02_T_15_14_49_552_KST"
            )
            @JsonProperty("createDate")
            val createDate: String,
            @Schema(
                description = "글 수정일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
                required = true,
                example = "2024_05_02_T_15_14_49_552_KST"
            )
            @JsonProperty("updateDate")
            val updateDate: String,
            @Schema(description = "기준과의 절대거리", required = true, example = "34")
            @JsonProperty("distance")
            val distance: Int
        )
    }


    ////
    @Operation(
        summary = "DB Row 수정 테스트",
        description = "테스트 테이블의 Row 하나를 수정합니다.\n\n"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            ),
            ApiResponse(
                responseCode = "204",
                content = [Content()],
                description = "Response Body 가 없습니다.\n\n" +
                        "Response Headers 를 확인하세요.",
                headers = [
                    Header(
                        name = "api-result-code",
                        description = "(Response Code 반환 원인) - Required\n\n" +
                                "1 : testTableUid 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.\n\n",
                        schema = Schema(type = "string")
                    )
                ]
            )
        ]
    )
    @PatchMapping(
        path = ["/row/{testTableUid}"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun updateRowSample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(name = "testTableUid", description = "test 테이블의 uid", example = "1")
        @PathVariable("testTableUid")
        testTableUid: Long,
        @RequestBody
        inputVo: UpdateRowSampleInputVo
    ): UpdateRowSampleOutputVo? {
        return service.updateRowSample(httpServletResponse, testTableUid, inputVo)
    }

    data class UpdateRowSampleInputVo(
        @Schema(description = "글 본문", required = true, example = "테스트 텍스트 수정글입니다.")
        @JsonProperty("content")
        val content: String,
        @Schema(
            description = "원하는 날짜(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
            required = true,
            example = "2024_05_02_T_15_14_49_552_KST"
        )
        @JsonProperty("dateString")
        val dateString: String
    )

    data class UpdateRowSampleOutputVo(
        @Schema(description = "글 고유번호", required = true, example = "1234")
        @JsonProperty("uid")
        val uid: Long,
        @Schema(description = "글 본문", required = true, example = "테스트 텍스트입니다.")
        @JsonProperty("content")
        val content: String,
        @Schema(description = "자동 생성 숫자", required = true, example = "21345")
        @JsonProperty("randomNum")
        val randomNum: Int,
        @Schema(
            description = "테스트용 일시 데이터(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
            required = true,
            example = "2024_05_02_T_15_14_49_552_KST"
        )
        @JsonProperty("testDatetime")
        val testDatetime: String,
        @Schema(
            description = "글 작성일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
            required = true,
            example = "2024_05_02_T_15_14_49_552_KST"
        )
        @JsonProperty("createDate")
        val createDate: String,
        @Schema(
            description = "글 수정일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
            required = true,
            example = "2024_05_02_T_15_14_49_552_KST"
        )
        @JsonProperty("updateDate")
        val updateDate: String
    )


    ////
    @Operation(
        summary = "DB Row 수정 테스트 (네이티브 쿼리)",
        description = "테스트 테이블의 Row 하나를 네이티브 쿼리로 수정합니다.\n\n"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            ),
            ApiResponse(
                responseCode = "204",
                content = [Content()],
                description = "Response Body 가 없습니다.\n\n" +
                        "Response Headers 를 확인하세요.",
                headers = [
                    Header(
                        name = "api-result-code",
                        description = "(Response Code 반환 원인) - Required\n\n" +
                                "1 : testTableUid 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.\n\n",
                        schema = Schema(type = "string")
                    )
                ]
            )
        ]
    )
    @PatchMapping(
        path = ["/row/{testTableUid}/native-query"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun updateRowNativeQuerySample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(name = "testTableUid", description = "test 테이블의 uid", example = "1")
        @PathVariable("testTableUid")
        testTableUid: Long,
        @RequestBody
        inputVo: UpdateRowNativeQuerySampleInputVo
    ) {
        return service.updateRowNativeQuerySample(httpServletResponse, testTableUid, inputVo)
    }

    data class UpdateRowNativeQuerySampleInputVo(
        @Schema(description = "글 본문", required = true, example = "테스트 텍스트 수정글입니다.")
        @JsonProperty("content")
        val content: String,
        @Schema(
            description = "원하는 날짜(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
            required = true,
            example = "2024_05_02_T_15_14_49_552_KST"
        )
        @JsonProperty("dateString")
        val dateString: String
    )


    ////
    @Operation(
        summary = "DB 정보 검색 테스트",
        description = "글 본문 내용중 searchKeyword 가 포함된 rows 를 검색하여 반환합니다.\n\n"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            )
        ]
    )
    @GetMapping(
        path = ["/search-content"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun selectRowWhereSearchingKeywordSample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(name = "page", description = "원하는 페이지(1 부터 시작)", example = "1")
        @RequestParam("page")
        page: Int,
        @Parameter(name = "pageElementsCount", description = "페이지 아이템 개수", example = "10")
        @RequestParam("pageElementsCount")
        pageElementsCount: Int,
        @Parameter(name = "searchKeyword", description = "검색어", example = "테스트")
        @RequestParam("searchKeyword")
        searchKeyword: String
    ): SelectRowWhereSearchingKeywordSampleOutputVo? {
        return service.selectRowWhereSearchingKeywordSample(
            httpServletResponse,
            page,
            pageElementsCount,
            searchKeyword
        )
    }

    data class SelectRowWhereSearchingKeywordSampleOutputVo(
        @Schema(description = "아이템 전체 개수", required = true, example = "100")
        @JsonProperty("totalElements")
        val totalElements: Long,
        @Schema(description = "아이템 리스트", required = true)
        @JsonProperty("testEntityVoList")
        val testEntityVoList: List<TestEntityVo>
    ) {
        @Schema(description = "아이템")
        data class TestEntityVo(
            @Schema(description = "글 고유번호", required = true, example = "1")
            @JsonProperty("uid")
            val uid: Long,
            @Schema(description = "글 본문", required = true, example = "테스트 텍스트입니다.")
            @JsonProperty("content")
            val content: String,
            @Schema(description = "자동 생성 숫자", required = true, example = "21345")
            @JsonProperty("randomNum")
            val randomNum: Int,
            @Schema(
                description = "테스트용 일시 데이터(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
                required = true,
                example = "2024_05_02_T_15_14_49_552_KST"
            )
            @JsonProperty("testDatetime")
            val testDatetime: String,
            @Schema(
                description = "글 작성일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
                required = true,
                example = "2024_05_02_T_15_14_49_552_KST"
            )
            @JsonProperty("createDate")
            val createDate: String,
            @Schema(
                description = "글 수정일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
                required = true,
                example = "2024_05_02_T_15_14_49_552_KST"
            )
            @JsonProperty("updateDate")
            val updateDate: String
        )
    }


    ////
    @Operation(
        summary = "트랜젝션 동작 테스트",
        description = "정보 입력 후 Exception 이 발생했을 때 롤백되어 데이터가 저장되지 않는지를 테스트하는 API\n\n"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            )
        ]
    )
    @PostMapping(
        path = ["/transaction-rollback-sample"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.ALL_VALUE]
    )
    @ResponseBody
    fun transactionTest(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse
    ) {
        service.transactionTest(httpServletResponse)
    }


    ////
    @Operation(
        summary = "트랜젝션 비동작 테스트",
        description = "트랜젝션 처리를 하지 않았을 때, DB 정보 입력 후 Exception 이 발생 했을 때 의 테스트 API\n\n"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            )
        ]
    )
    @PostMapping(
        path = ["/no-transaction-exception-sample"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.ALL_VALUE]
    )
    @ResponseBody
    fun nonTransactionTest(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse
    ) {
        service.nonTransactionTest(httpServletResponse)
    }


    ////
    @Operation(
        summary = "트랜젝션 비동작 테스트(try-catch)",
        description = "에러 발생문이 try-catch 문 안에 있을 때, DB 정보 입력 후 Exception 이 발생 해도 트랜젝션이 동작하지 않는지에 대한 테스트 API\n\n"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            )
        ]
    )
    @PostMapping(
        path = ["/try-catch-no-transaction-exception-sample"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.ALL_VALUE]
    )
    @ResponseBody
    fun tryCatchNonTransactionTest(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse
    ) {
        service.tryCatchNonTransactionTest(httpServletResponse)
    }


    ////
    @Operation(
        summary = "DB Rows 조회 테스트 (중복 없는 네이티브 쿼리 페이징)",
        description = "테스트 테이블의 Rows 를 네이티브 쿼리로 중복없이 페이징하여 반환합니다.\n\n" +
                "num 을 기준으로 근사치 정렬도 수행합니다.\n\n"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            )
        ]
    )
    @GetMapping(
        path = ["/rows/native-paging-no-duplication"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun selectRowsNoDuplicatePagingSample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(name = "lastItemUid", description = "이전 페이지에서 받은 마지막 아이템의 Uid (첫 요청이면 null)", example = "1")
        @RequestParam("lastItemUid")
        lastItemUid: Long?,
        @Parameter(name = "pageElementsCount", description = "페이지 아이템 개수", example = "10")
        @RequestParam("pageElementsCount")
        pageElementsCount: Int
    ): SelectRowsNoDuplicatePagingSampleOutputVo? {
        return service.selectRowsNoDuplicatePagingSample(httpServletResponse, lastItemUid, pageElementsCount)
    }

    data class SelectRowsNoDuplicatePagingSampleOutputVo(
        @Schema(description = "아이템 전체 개수", required = true, example = "100")
        @JsonProperty("totalElements")
        val totalElements: Long,
        @Schema(description = "아이템 리스트", required = true)
        @JsonProperty("testEntityVoList")
        val testEntityVoList: List<TestEntityVo>
    ) {
        @Schema(description = "아이템")
        data class TestEntityVo(
            @Schema(description = "글 고유번호", required = true, example = "1")
            @JsonProperty("uid")
            val uid: Long,
            @Schema(description = "글 본문", required = true, example = "테스트 텍스트입니다.")
            @JsonProperty("content")
            val content: String,
            @Schema(description = "자동 생성 숫자", required = true, example = "21345")
            @JsonProperty("randomNum")
            val randomNum: Int,
            @Schema(
                description = "테스트용 일시 데이터(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
                required = true,
                example = "2024_05_02_T_15_14_49_552_KST"
            )
            @JsonProperty("testDatetime")
            val testDatetime: String,
            @Schema(
                description = "글 작성일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
                required = true,
                example = "2024_05_02_T_15_14_49_552_KST"
            )
            @JsonProperty("createDate")
            val createDate: String,
            @Schema(
                description = "글 수정일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
                required = true,
                example = "2024_05_02_T_15_14_49_552_KST"
            )
            @JsonProperty("updateDate")
            val updateDate: String
        )
    }


    ////
    @Operation(
        summary = "DB Rows 조회 테스트 (카운팅)",
        description = "테스트 테이블의 Rows 를 카운팅하여 반환합니다.\n\n"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            )
        ]
    )
    @GetMapping(
        path = ["/rows/counting"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun selectRowsCountSample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse
    ): SelectRowsCountSampleOutputVo? {
        return service.selectRowsCountSample(httpServletResponse)
    }

    data class SelectRowsCountSampleOutputVo(
        @Schema(description = "아이템 전체 개수", required = true, example = "100")
        @JsonProperty("totalElements")
        val totalElements: Long
    )


    ////
    @Operation(
        summary = "DB Rows 조회 테스트 (네이티브 카운팅)",
        description = "테스트 테이블의 Rows 를 네이티브 쿼리로 카운팅하여 반환합니다.\n\n"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            )
        ]
    )
    @GetMapping(
        path = ["/rows/native-counting"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun selectRowsCountByNativeQuerySample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse
    ): SelectRowsCountByNativeQuerySampleOutputVo? {
        return service.selectRowsCountByNativeQuerySample(httpServletResponse)
    }

    data class SelectRowsCountByNativeQuerySampleOutputVo(
        @Schema(description = "아이템 전체 개수", required = true, example = "100")
        @JsonProperty("totalElements")
        val totalElements: Long
    )


    ////
    @Operation(
        summary = "DB Row 조회 테스트 (네이티브)",
        description = "테스트 테이블의 Row 하나를 네이티브 쿼리로 반환합니다.\n\n"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            ),
            ApiResponse(
                responseCode = "204",
                content = [Content()],
                description = "Response Body 가 없습니다.\n\n" +
                        "Response Headers 를 확인하세요.",
                headers = [
                    Header(
                        name = "api-result-code",
                        description = "(Response Code 반환 원인) - Required\n\n" +
                                "1 : testTableUid 에 해당하는 데이터가 존재하지 않습니다.\n\n",
                        schema = Schema(type = "string")
                    )
                ]
            )
        ]
    )
    @GetMapping(
        path = ["/row/native/{testTableUid}"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun selectRowByNativeQuerySample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(name = "testTableUid", description = "test 테이블의 uid", example = "1")
        @PathVariable("testTableUid")
        testTableUid: Long
    ): SelectRowByNativeQuerySampleOutputVo? {
        return service.selectRowByNativeQuerySample(httpServletResponse, testTableUid)
    }

    data class SelectRowByNativeQuerySampleOutputVo(
        @Schema(description = "글 고유번호", required = true, example = "1234")
        @JsonProperty("uid")
        val uid: Long,
        @Schema(description = "글 본문", required = true, example = "테스트 텍스트입니다.")
        @JsonProperty("content")
        val content: String,
        @Schema(description = "자동 생성 숫자", required = true, example = "21345")
        @JsonProperty("randomNum")
        val randomNum: Int,
        @Schema(
            description = "테스트용 일시 데이터(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
            required = true,
            example = "2024_05_02_T_15_14_49_552_KST"
        )
        @JsonProperty("testDatetime")
        val testDatetime: String,
        @Schema(
            description = "글 작성일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
            required = true,
            example = "2024_05_02_T_15_14_49_552_KST"
        )
        @JsonProperty("createDate")
        val createDate: String,
        @Schema(
            description = "글 수정일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
            required = true,
            example = "2024_05_02_T_15_14_49_552_KST"
        )
        @JsonProperty("updateDate")
        val updateDate: String
    )


    ////
    @Operation(
        summary = "유니크 테스트 테이블 Row 입력 API",
        description = "유니크 테스트 테이블에 Row 를 입력합니다.\n\n" +
                "논리적 삭제를 적용한 본 테이블에서 유니크 값은, 유니크 값 컬럼과 행 삭제일 데이터와의 혼합입니다.\n\n"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            )
        ]
    )
    @PostMapping(
        path = ["/unique-test-table"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun insertUniqueTestTableRowSample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @RequestBody
        inputVo: InsertUniqueTestTableRowSampleInputVo
    ): InsertUniqueTestTableRowSampleOutputVo? {
        return service.insertUniqueTestTableRowSample(httpServletResponse, inputVo)
    }

    data class InsertUniqueTestTableRowSampleInputVo(
        @Schema(description = "유니크 값", required = true, example = "1")
        @JsonProperty("uniqueValue")
        val uniqueValue: Int
    )

    data class InsertUniqueTestTableRowSampleOutputVo(
        @Schema(description = "글 고유번호", required = true, example = "1234")
        @JsonProperty("uid")
        val uid: Long,
        @Schema(description = "유니크 값", required = true, example = "1")
        @JsonProperty("uniqueValue")
        val uniqueValue: Int,
        @Schema(
            description = "글 작성일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
            required = true,
            example = "2024_05_02_T_15_14_49_552_KST"
        )
        @JsonProperty("createDate")
        val createDate: String,
        @Schema(
            description = "글 수정일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
            required = true,
            example = "2024_05_02_T_15_14_49_552_KST"
        )
        @JsonProperty("updateDate")
        val updateDate: String,
        @Schema(description = "글 삭제일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z, Null 이면 /)", required = true, example = "/")
        @JsonProperty("deleteDate")
        val deleteDate: String
    )


    ////
    @Operation(
        summary = "유니크 테스트 테이블 Rows 조회 테스트",
        description = "유니크 테스트 테이블의 모든 Rows 를 반환합니다.\n\n"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            )
        ]
    )
    @GetMapping(
        path = ["/unique-test-table/all"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun selectUniqueTestTableRowsSample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse
    ): SelectUniqueTestTableRowsSampleOutputVo? {
        return service.selectUniqueTestTableRowsSample(httpServletResponse)
    }

    data class SelectUniqueTestTableRowsSampleOutputVo(
        @Schema(description = "아이템 리스트", required = true)
        @JsonProperty("testEntityVoList")
        val testEntityVoList: List<TestEntityVo>,

        @Schema(description = "논리적으로 제거된 아이템 리스트", required = true)
        @JsonProperty("logicalDeleteEntityVoList")
        val logicalDeleteEntityVoList: List<TestEntityVo>
    ) {
        @Schema(description = "아이템")
        data class TestEntityVo(
            @Schema(description = "글 고유번호", required = true, example = "1234")
            @JsonProperty("uid")
            val uid: Long,
            @Schema(description = "유니크 값", required = true, example = "1")
            @JsonProperty("uniqueValue")
            val uniqueValue: Int,
            @Schema(
                description = "글 작성일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
                required = true,
                example = "2024_05_02_T_15_14_49_552_KST"
            )
            @JsonProperty("createDate")
            val createDate: String,
            @Schema(
                description = "글 수정일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
                required = true,
                example = "2024_05_02_T_15_14_49_552_KST"
            )
            @JsonProperty("updateDate")
            val updateDate: String,
            @Schema(description = "글 삭제일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z, Null 이면 /)", required = true, example = "/")
            @JsonProperty("deleteDate")
            val deleteDate: String
        )
    }


    ////
    @Operation(
        summary = "유니크 테스트 테이블 Row 수정 테스트",
        description = "유니크 테스트 테이블의 Row 하나를 수정합니다.\n\n"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            ),
            ApiResponse(
                responseCode = "204",
                content = [Content()],
                description = "Response Body 가 없습니다.\n\n" +
                        "Response Headers 를 확인하세요.",
                headers = [
                    Header(
                        name = "api-result-code",
                        description = "(Response Code 반환 원인) - Required\n\n" +
                                "1 : uniqueTestTableUid 에 해당하는 정보가 데이터베이스에 존재하지 않습니다.\n\n" +
                                "2 : uniqueValue 와 일치하는 정보가 이미 데이터베이스에 존재합니다.\n\n",
                        schema = Schema(type = "string")
                    )
                ]
            )
        ]
    )
    @PatchMapping(
        path = ["/unique-test-table/{uniqueTestTableUid}"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun updateUniqueTestTableRowSample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(name = "uniqueTestTableUid", description = "unique test 테이블의 uid", example = "1")
        @PathVariable("uniqueTestTableUid")
        uniqueTestTableUid: Long,
        @RequestBody
        inputVo: UpdateUniqueTestTableRowSampleInputVo
    ): UpdateUniqueTestTableRowSampleOutputVo? {
        return service.updateUniqueTestTableRowSample(httpServletResponse, uniqueTestTableUid, inputVo)
    }

    data class UpdateUniqueTestTableRowSampleInputVo(
        @Schema(description = "유니크 값", required = true, example = "1")
        @JsonProperty("uniqueValue")
        val uniqueValue: Int
    )

    data class UpdateUniqueTestTableRowSampleOutputVo(
        @Schema(description = "글 고유번호", required = true, example = "1234")
        @JsonProperty("uid")
        val uid: Long,
        @Schema(description = "유니크 값", required = true, example = "1")
        @JsonProperty("uniqueValue")
        val uniqueValue: Int,
        @Schema(
            description = "글 작성일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
            required = true,
            example = "2024_05_02_T_15_14_49_552_KST"
        )
        @JsonProperty("createDate")
        val createDate: String,
        @Schema(
            description = "글 수정일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
            required = true,
            example = "2024_05_02_T_15_14_49_552_KST"
        )
        @JsonProperty("updateDate")
        val updateDate: String
    )


    ////
    @Operation(
        summary = "유니크 테스트 테이블 Row 삭제 테스트",
        description = "유니크 테스트 테이블의 Row 하나를 삭제합니다.\n\n"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            ),
            ApiResponse(
                responseCode = "204",
                content = [Content()],
                description = "Response Body 가 없습니다.\n\n" +
                        "Response Headers 를 확인하세요.",
                headers = [
                    Header(
                        name = "api-result-code",
                        description = "(Response Code 반환 원인) - Required\n\n" +
                                "1 : index 에 해당하는 데이터가 데이터베이스에 존재하지 않습니다.\n\n",
                        schema = Schema(type = "string")
                    )
                ]
            )
        ]
    )
    @DeleteMapping(
        path = ["/unique-test-table/{index}"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.ALL_VALUE]
    )
    @ResponseBody
    fun deleteUniqueTestTableRowSample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(name = "index", description = "글 인덱스", example = "1")
        @PathVariable("index")
        index: Long
    ) {
        service.deleteUniqueTestTableRowSample(httpServletResponse, index)
    }


    ////
    @Operation(
        summary = "외래키 부모 테이블 Row 입력 API",
        description = "외래키 부모 테이블에 Row 를 입력합니다.\n\n"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            )
        ]
    )
    @PostMapping(
        path = ["/fk-parent"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun insertFkParentRowSample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @RequestBody
        inputVo: InsertFkParentRowSampleInputVo
    ): InsertFkParentRowSampleOutputVo? {
        return service.insertFkParentRowSample(httpServletResponse, inputVo)
    }

    data class InsertFkParentRowSampleInputVo(
        @Schema(description = "외래키 테이블 부모 이름", required = true, example = "홍길동")
        @JsonProperty("fkParentName")
        val fkParentName: String
    )

    data class InsertFkParentRowSampleOutputVo(
        @Schema(description = "글 고유번호", required = true, example = "1234")
        @JsonProperty("uid")
        val uid: Long,
        @Schema(description = "외래키 테이블 부모 이름", required = true, example = "홍길동")
        @JsonProperty("fkParentName")
        val fkParentName: String,
        @Schema(
            description = "글 작성일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
            required = true,
            example = "2024_05_02_T_15_14_49_552_KST"
        )
        @JsonProperty("createDate")
        val createDate: String,
        @Schema(
            description = "글 수정일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
            required = true,
            example = "2024_05_02_T_15_14_49_552_KST"
        )
        @JsonProperty("updateDate")
        val updateDate: String
    )


    ////
    @Operation(
        summary = "외래키 부모 테이블 아래에 자식 테이블의 Row 입력 API",
        description = "외래키 부모 테이블의 아래에 자식 테이블의 Row 를 입력합니다.\n\n"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            ),
            ApiResponse(
                responseCode = "204",
                content = [Content()],
                description = "Response Body 가 없습니다.\n\n" +
                        "Response Headers 를 확인하세요.",
                headers = [
                    Header(
                        name = "api-result-code",
                        description = "(Response Code 반환 원인) - Required\n\n" +
                                "1 : parentUid 에 해당하는 데이터가 존재하지 않습니다.\n\n",
                        schema = Schema(type = "string")
                    )
                ]
            )
        ]
    )
    @PostMapping(
        path = ["/fk-parent/{parentUid}"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun insertFkChildRowSample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(name = "parentUid", description = "외래키 부모 테이블 고유번호", example = "1")
        @PathVariable("parentUid")
        parentUid: Long,
        @RequestBody
        inputVo: InsertFkChildRowSampleInputVo
    ): InsertFkChildRowSampleOutputVo? {
        return service.insertFkChildRowSample(httpServletResponse, parentUid, inputVo)
    }

    data class InsertFkChildRowSampleInputVo(
        @Schema(description = "외래키 테이블 자식 이름", required = true, example = "홍길동")
        @JsonProperty("fkChildName")
        val fkChildName: String
    )

    data class InsertFkChildRowSampleOutputVo(
        @Schema(description = "글 고유번호", required = true, example = "1234")
        @JsonProperty("uid")
        val uid: Long,
        @Schema(description = "외래키 테이블 부모 이름", required = true, example = "홍길동")
        @JsonProperty("fkParentName")
        val fkParentName: String,
        @Schema(description = "외래키 테이블 자식 이름", required = true, example = "홍길동")
        @JsonProperty("fkChildName")
        val fkChildName: String,
        @Schema(
            description = "글 작성일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
            required = true,
            example = "2024_05_02_T_15_14_49_552_KST"
        )
        @JsonProperty("createDate")
        val createDate: String,
        @Schema(
            description = "글 수정일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
            required = true,
            example = "2024_05_02_T_15_14_49_552_KST"
        )
        @JsonProperty("updateDate")
        val updateDate: String
    )


    ////
    @Operation(
        summary = "외래키 관련 테이블 Rows 조회 테스트",
        description = "외래키 관련 테이블의 모든 Rows 를 반환합니다.\n\n"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            )
        ]
    )
    @GetMapping(
        path = ["/fk-table/all"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun selectFkTestTableRowsSample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse
    ): SelectFkTestTableRowsSampleOutputVo? {
        return service.selectFkTestTableRowsSample(httpServletResponse)
    }

    data class SelectFkTestTableRowsSampleOutputVo(
        @Schema(description = "부모 아이템 리스트", required = true)
        @JsonProperty("parentEntityVoList")
        val parentEntityVoList: List<ParentEntityVo>
    ) {
        @Schema(description = "부모 아이템")
        data class ParentEntityVo(
            @Schema(description = "글 고유번호", required = true, example = "1234")
            @JsonProperty("uid")
            val uid: Long,
            @Schema(description = "부모 테이블 이름", required = true, example = "1")
            @JsonProperty("parentName")
            val parentName: String,
            @Schema(
                description = "글 작성일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
                required = true,
                example = "2024_05_02_T_15_14_49_552_KST"
            )
            @JsonProperty("createDate")
            val createDate: String,
            @Schema(
                description = "글 수정일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
                required = true,
                example = "2024_05_02_T_15_14_49_552_KST"
            )
            @JsonProperty("updateDate")
            val updateDate: String,
            @Schema(description = "부모 테이블에 속하는 자식 테이블 리스트", required = true)
            @JsonProperty("childEntityList")
            val childEntityList: List<ChildEntityVo>
        ) {
            @Schema(description = "자식 아이템")
            data class ChildEntityVo(
                @Schema(description = "글 고유번호", required = true, example = "1234")
                @JsonProperty("uid")
                val uid: Long,
                @Schema(description = "자식 테이블 이름", required = true, example = "1")
                @JsonProperty("childName")
                val childName: String,
                @Schema(
                    description = "글 작성일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
                    required = true,
                    example = "2024_05_02_T_15_14_49_552_KST"
                )
                @JsonProperty("createDate")
                val createDate: String,
                @Schema(
                    description = "글 수정일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
                    required = true,
                    example = "2024_05_02_T_15_14_49_552_KST"
                )
                @JsonProperty("updateDate")
                val updateDate: String
            )
        }
    }


    ////
    @Operation(
        summary = "외래키 관련 테이블 Rows 조회 테스트(Native Join)",
        description = "외래키 관련 테이블의 모든 Rows 를 Native Query 로 Join 하여 반환합니다.\n\n"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            )
        ]
    )
    @GetMapping(
        path = ["/fk-table-native-join"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun selectFkTestTableRowsByNativeQuerySample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse
    ): SelectFkTestTableRowsByNativeQuerySampleDot1OutputVo? {
        return service.selectFkTestTableRowsByNativeQuerySample(httpServletResponse)
    }

    data class SelectFkTestTableRowsByNativeQuerySampleDot1OutputVo(
        @Schema(description = "자식 아이템 리스트", required = true)
        @JsonProperty("childEntityVoList")
        val childEntityVoList: List<ChildEntityVo>
    ) {
        @Schema(description = "자식 아이템")
        data class ChildEntityVo(
            @Schema(description = "글 고유번호", required = true, example = "1234")
            @JsonProperty("uid")
            val uid: Long,
            @Schema(description = "자식 테이블 이름", required = true, example = "1")
            @JsonProperty("childName")
            val childName: String,
            @Schema(
                description = "글 작성일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
                required = true,
                example = "2024_05_02_T_15_14_49_552_KST"
            )
            @JsonProperty("createDate")
            val createDate: String,
            @Schema(
                description = "글 수정일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
                required = true,
                example = "2024_05_02_T_15_14_49_552_KST"
            )
            @JsonProperty("updateDate")
            val updateDate: String,
            @Schema(description = "부모 테이블 고유번호", required = true)
            @JsonProperty("parentUid")
            val parentUid: Long,
            @Schema(description = "부모 테이블 이름", required = true)
            @JsonProperty("parentName")
            val parentName: String
        )
    }


    ////
    @Operation(
        summary = "Native Query 반환값 테스트",
        description = "Native Query Select 문에서 IF, CASE 등의 문구에서 반환되는 값들을 받는 예시\n\n"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            )
        ]
    )
    @GetMapping(
        path = ["/native-query-return"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun getNativeQueryReturnValueTest(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(name = "inputVal", description = "Native Query 비교문에 사용되는 파라미터", example = "true")
        @RequestParam("inputVal")
        inputVal: Boolean
    ): GetNativeQueryReturnValueTestOutputVo? {
        return service.getNativeQueryReturnValueTest(
            httpServletResponse,
            inputVal
        )
    }

    data class GetNativeQueryReturnValueTestOutputVo(
        @Schema(description = "Select 문에서 직접적으로 true 를 반환한 예시", required = true, example = "true")
        @JsonProperty("normalBoolValue")
        val normalBoolValue: Boolean,
        @Schema(description = "Select 문에서 (1=1) 과 같이 비교한 결과를 반환한 예시", required = true, example = "true")
        @JsonProperty("funcBoolValue")
        val funcBoolValue: Boolean,
        @Schema(description = "Select 문에서 if 문의 결과를 반환한 예시", required = true, example = "true")
        @JsonProperty("ifBoolValue")
        val ifBoolValue: Boolean,
        @Schema(description = "Select 문에서 case 문의 결과를 반환한 예시", required = true, example = "true")
        @JsonProperty("caseBoolValue")
        val caseBoolValue: Boolean,
        @Schema(description = "Select 문에서 테이블의 Boolean 컬럼의 결과를 반환한 예시", required = true, example = "true")
        @JsonProperty("tableColumnBoolValue")
        val tableColumnBoolValue: Boolean
    )


    ////
    @Operation(
        summary = "SQL Injection 테스트",
        description = "각 상황에서 SQL Injection 공격이 유효한지 확인하기 위한 테스트\n\n" +
                "SELECT 문에서, WHERE 에, content = :searchKeyword 를 하여,\n\n" +
                " 인젝션이 일어나는 키워드를 입력시 인젝션이 먹히는지를 확인할 것입니다.\n\n"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            )
        ]
    )
    @GetMapping(
        path = ["/sql-injection-test"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun sqlInjectionTest(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(name = "searchKeyword", description = "Select 문 검색에 사용되는 키워드", example = "test OR 1 = 1")
        @RequestParam("searchKeyword")
        searchKeyword: String
    ): SqlInjectionTestOutputVo? {
        return service.sqlInjectionTest(
            httpServletResponse,
            searchKeyword
        )
    }

    data class SqlInjectionTestOutputVo(
        @Schema(description = "JpaRepository 로 조회했을 때의 아이템 리스트", required = true)
        @JsonProperty("jpaRepositoryResultList")
        val jpaRepositoryResultList: List<TestEntityVo>,
        @Schema(description = "JPQL 로 조회했을 때의 아이템 리스트", required = true)
        @JsonProperty("jpqlResultList")
        val jpqlResultList: List<TestEntityVo>,
        @Schema(description = "Native Query 로 조회했을 때의 아이템 리스트", required = true)
        @JsonProperty("nativeQueryResultList")
        val nativeQueryResultList: List<TestEntityVo>
    ) {
        @Schema(description = "아이템")
        data class TestEntityVo(
            @Schema(description = "글 고유번호", required = true, example = "1234")
            @JsonProperty("uid")
            val uid: Long,
            @Schema(description = "글 본문", required = true, example = "테스트 텍스트입니다.")
            @JsonProperty("content")
            val content: String,
            @Schema(description = "자동 생성 숫자", required = true, example = "21345")
            @JsonProperty("randomNum")
            val randomNum: Int,
            @Schema(
                description = "테스트용 일시 데이터(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
                required = true,
                example = "2024_05_02_T_15_14_49_552_KST"
            )
            @JsonProperty("testDatetime")
            val testDatetime: String,
            @Schema(
                description = "글 작성일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
                required = true,
                example = "2024_05_02_T_15_14_49_552_KST"
            )
            @JsonProperty("createDate")
            val createDate: String,
            @Schema(
                description = "글 수정일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
                required = true,
                example = "2024_05_02_T_15_14_49_552_KST"
            )
            @JsonProperty("updateDate")
            val updateDate: String
        )
    }


    ////
    @Operation(
        summary = "외래키 관련 테이블 Rows 조회 (네이티브 쿼리, 부모 테이블을 자식 테이블의 가장 최근 데이터만 Join)",
        description = "외래키 관련 테이블의 모든 Rows 를 반환합니다.\n\n" +
                "부모 테이블을 Native Query 로 조회할 때, 부모 테이블을 가리키는 자식 테이블들 중 가장 최신 데이터만 Join 하는 예시입니다.\n\n"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            )
        ]
    )
    @GetMapping(
        path = ["/fk-table-latest-join"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun selectFkTableRowsWithLatestChildSample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse
    ): SelectFkTableRowsWithLatestChildSampleOutputVo? {
        return service.selectFkTableRowsWithLatestChildSample(httpServletResponse)
    }

    data class SelectFkTableRowsWithLatestChildSampleOutputVo(
        @Schema(description = "부모 아이템 리스트", required = true)
        @JsonProperty("parentEntityVoList")
        val parentEntityVoList: List<ParentEntityVo>
    ) {
        @Schema(description = "부모 아이템")
        data class ParentEntityVo(
            @Schema(description = "글 고유번호", required = true, example = "1234")
            @JsonProperty("uid")
            val uid: Long,
            @Schema(description = "부모 테이블 이름", required = true, example = "1")
            @JsonProperty("parentName")
            val parentName: String,
            @Schema(
                description = "글 작성일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
                required = true,
                example = "2024_05_02_T_15_14_49_552_KST"
            )
            @JsonProperty("createDate")
            val createDate: String,
            @Schema(
                description = "글 수정일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
                required = true,
                example = "2024_05_02_T_15_14_49_552_KST"
            )
            @JsonProperty("updateDate")
            val updateDate: String,
            @Schema(description = "부모 테이블에 속하는 자식 테이블들 중 가장 최신 데이터", required = false)
            @JsonProperty("latestChildEntity")
            val latestChildEntity: ChildEntityVo?
        ) {
            @Schema(description = "자식 아이템")
            data class ChildEntityVo(
                @Schema(description = "글 고유번호", required = true, example = "1234")
                @JsonProperty("uid")
                val uid: Long,
                @Schema(description = "자식 테이블 이름", required = true, example = "1")
                @JsonProperty("childName")
                val childName: String,
                @Schema(
                    description = "글 작성일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
                    required = true,
                    example = "2024_05_02_T_15_14_49_552_KST"
                )
                @JsonProperty("createDate")
                val createDate: String,
                @Schema(
                    description = "글 수정일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
                    required = true,
                    example = "2024_05_02_T_15_14_49_552_KST"
                )
                @JsonProperty("updateDate")
                val updateDate: String
            )
        }
    }


    ////
    @Operation(
        summary = "외래키 관련 테이블 Rows 조회 (QueryDsl)",
        description = "QueryDsl 을 사용하여 외래키 관련 테이블의 모든 Rows 를 반환합니다.\n\n"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            )
        ]
    )
    @GetMapping(
        path = ["/fk-table-dsl"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun selectFkTableRowsWithQueryDsl(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse
    ): SelectFkTableRowsWithQueryDslOutputVo? {
        return service.selectFkTableRowsWithQueryDsl(httpServletResponse)
    }

    data class SelectFkTableRowsWithQueryDslOutputVo(
        @Schema(description = "부모 아이템 리스트", required = true)
        @JsonProperty("parentEntityVoList")
        val parentEntityVoList: List<ParentEntityVo>
    ) {
        @Schema(description = "부모 아이템")
        data class ParentEntityVo(
            @Schema(description = "글 고유번호", required = true, example = "1234")
            @JsonProperty("uid")
            val uid: Long,
            @Schema(description = "부모 테이블 이름", required = true, example = "1")
            @JsonProperty("parentName")
            val parentName: String,
            @Schema(
                description = "글 작성일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
                required = true,
                example = "2024_05_02_T_15_14_49_552_KST"
            )
            @JsonProperty("createDate")
            val createDate: String,
            @Schema(
                description = "글 수정일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
                required = true,
                example = "2024_05_02_T_15_14_49_552_KST"
            )
            @JsonProperty("updateDate")
            val updateDate: String,
            @Schema(description = "부모 테이블에 속하는 자식 테이블들", required = true)
            @JsonProperty("childEntityList")
            val childEntityList: List<ChildEntityVo>
        ) {
            @Schema(description = "자식 아이템")
            data class ChildEntityVo(
                @Schema(description = "글 고유번호", required = true, example = "1234")
                @JsonProperty("uid")
                val uid: Long,
                @Schema(description = "자식 테이블 이름", required = true, example = "1")
                @JsonProperty("childName")
                val childName: String,
                @Schema(
                    description = "글 작성일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
                    required = true,
                    example = "2024_05_02_T_15_14_49_552_KST"
                )
                @JsonProperty("createDate")
                val createDate: String,
                @Schema(
                    description = "글 수정일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
                    required = true,
                    example = "2024_05_02_T_15_14_49_552_KST"
                )
                @JsonProperty("updateDate")
                val updateDate: String
            )
        }
    }


    ////
    @Operation(
        summary = "외래키 관련 테이블 Rows 조회 및 부모 테이블 이름으로 필터링 (QueryDsl)",
        description = "QueryDsl 을 사용하여 외래키 관련 테이블의 모든 Rows 를 반환합니다.\n\n" +
                "추가로, 부모 테이블에 할당된 이름으로 검색 결과를 필터링합니다.\n\n"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            )
        ]
    )
    @GetMapping(
        path = ["/fk-table-parent-name-filter-dsl"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun selectFkTableRowsByParentNameFilterWithQueryDsl(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(name = "parentName", description = "필터링 할 parentName 변수", example = "홍길동")
        @RequestParam("parentName")
        parentName: String
    ): SelectFkTableRowsByParentNameFilterWithQueryDslOutputVo? {
        return service.selectFkTableRowsByParentNameFilterWithQueryDsl(httpServletResponse, parentName)
    }

    data class SelectFkTableRowsByParentNameFilterWithQueryDslOutputVo(
        @Schema(description = "부모 아이템 리스트", required = true)
        @JsonProperty("parentEntityVoList")
        val parentEntityVoList: List<ParentEntityVo>
    ) {
        @Schema(description = "부모 아이템")
        data class ParentEntityVo(
            @Schema(description = "글 고유번호", required = true, example = "1234")
            @JsonProperty("uid")
            val uid: Long,
            @Schema(description = "부모 테이블 이름", required = true, example = "1")
            @JsonProperty("parentName")
            val parentName: String,
            @Schema(
                description = "글 작성일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
                required = true,
                example = "2024_05_02_T_15_14_49_552_KST"
            )
            @JsonProperty("createDate")
            val createDate: String,
            @Schema(
                description = "글 수정일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
                required = true,
                example = "2024_05_02_T_15_14_49_552_KST"
            )
            @JsonProperty("updateDate")
            val updateDate: String,
            @Schema(description = "부모 테이블에 속하는 자식 테이블들", required = true)
            @JsonProperty("childEntityList")
            val childEntityList: List<ChildEntityVo>
        ) {
            @Schema(description = "자식 아이템")
            data class ChildEntityVo(
                @Schema(description = "글 고유번호", required = true, example = "1234")
                @JsonProperty("uid")
                val uid: Long,
                @Schema(description = "자식 테이블 이름", required = true, example = "1")
                @JsonProperty("childName")
                val childName: String,
                @Schema(
                    description = "글 작성일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
                    required = true,
                    example = "2024_05_02_T_15_14_49_552_KST"
                )
                @JsonProperty("createDate")
                val createDate: String,
                @Schema(
                    description = "글 수정일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
                    required = true,
                    example = "2024_05_02_T_15_14_49_552_KST"
                )
                @JsonProperty("updateDate")
                val updateDate: String
            )
        }
    }


    ////
    @Operation(
        summary = "외래키 관련 테이블 부모 테이블 고유번호로 자식 테이블 리스트 검색 (QueryDsl)",
        description = "부모 테이블 고유번호로 자식 테이블 리스트를 검색하여 반환합니다.\n\n"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            )
        ]
    )
    @GetMapping(
        path = ["/fk-table-child-list-dsl"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun selectFkTableChildListWithQueryDsl(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(name = "parentUid", description = "parent 테이블 고유번호", example = "1")
        @RequestParam("parentUid")
        parentUid: Long
    ): SelectFkTableChildListWithQueryDslOutputVo? {
        return service.selectFkTableChildListWithQueryDsl(httpServletResponse, parentUid)
    }

    data class SelectFkTableChildListWithQueryDslOutputVo(
        @Schema(description = "부모 테이블에 속하는 자식 테이블들", required = true)
        @JsonProperty("childEntityList")
        val childEntityList: List<ChildEntityVo>
    ) {
        @Schema(description = "자식 아이템")
        data class ChildEntityVo(
            @Schema(description = "글 고유번호", required = true, example = "1234")
            @JsonProperty("uid")
            val uid: Long,
            @Schema(description = "자식 테이블 이름", required = true, example = "1")
            @JsonProperty("childName")
            val childName: String,
            @Schema(
                description = "글 작성일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
                required = true,
                example = "2024_05_02_T_15_14_49_552_KST"
            )
            @JsonProperty("createDate")
            val createDate: String,
            @Schema(
                description = "글 수정일(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
                required = true,
                example = "2024_05_02_T_15_14_49_552_KST"
            )
            @JsonProperty("updateDate")
            val updateDate: String
        )
    }


    ////
    @Operation(
        summary = "외래키 자식 테이블 Row 삭제 테스트",
        description = "외래키 자식 테이블의 Row 하나를 삭제합니다.\n\n"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            ),
            ApiResponse(
                responseCode = "204",
                content = [Content()],
                description = "Response Body 가 없습니다.\n\n" +
                        "Response Headers 를 확인하세요.",
                headers = [
                    Header(
                        name = "api-result-code",
                        description = "(Response Code 반환 원인) - Required\n\n" +
                                "1 : index 에 해당하는 데이터가 데이터베이스에 존재하지 않습니다.\n\n",
                        schema = Schema(type = "string")
                    )
                ]
            )
        ]
    )
    @DeleteMapping(
        path = ["/fk-child/{index}"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.ALL_VALUE]
    )
    @ResponseBody
    fun deleteFkChildRowSample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(name = "index", description = "글 인덱스", example = "1")
        @PathVariable("index")
        index: Long
    ) {
        service.deleteFkChildRowSample(httpServletResponse, index)
    }


    ////
    @Operation(
        summary = "외래키 부모 테이블 Row 삭제 테스트 (Cascade 기능 확인)",
        description = "외래키 부모 테이블의 Row 하나를 삭제합니다.\n\n" +
                "Cascade 설정을 했으므로 부모 테이블이 삭제되면 해당 부모 테이블을 참조중인 다른 모든 자식 테이블들이 삭제되어야 합니다.\n\n"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            ),
            ApiResponse(
                responseCode = "204",
                content = [Content()],
                description = "Response Body 가 없습니다.\n\n" +
                        "Response Headers 를 확인하세요.",
                headers = [
                    Header(
                        name = "api-result-code",
                        description = "(Response Code 반환 원인) - Required\n\n" +
                                "1 : index 에 해당하는 데이터가 데이터베이스에 존재하지 않습니다.\n\n",
                        schema = Schema(type = "string")
                    )
                ]
            )
        ]
    )
    @DeleteMapping(
        path = ["/fk-parent/{index}"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.ALL_VALUE]
    )
    @ResponseBody
    fun deleteFkParentRowSample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(name = "index", description = "글 인덱스", example = "1")
        @PathVariable("index")
        index: Long
    ) {
        service.deleteFkParentRowSample(httpServletResponse, index)
    }


    ////
    @Operation(
        summary = "외래키 테이블 트랜젝션 동작 테스트",
        description = "외래키 테이블에 정보 입력 후 Exception 이 발생했을 때 롤백되어 데이터가 저장되지 않는지를 테스트하는 API\n\n"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            )
        ]
    )
    @PostMapping(
        path = ["/fk-transaction-rollback-sample"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.ALL_VALUE]
    )
    @ResponseBody
    fun fkTableTransactionTest(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse
    ) {
        service.fkTableTransactionTest(httpServletResponse)
    }


    ////
    @Operation(
        summary = "외래키 테이블 트랜젝션 비동작 테스트",
        description = "외래키 테이블의 트랜젝션 처리를 하지 않았을 때, DB 정보 입력 후 Exception 이 발생 했을 때 의 테스트 API\n\n"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "정상 동작"
            )
        ]
    )
    @PostMapping(
        path = ["/fk-no-transaction-exception-sample"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.ALL_VALUE]
    )
    @ResponseBody
    fun fkTableNonTransactionTest(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse
    ) {
        service.fkTableNonTransactionTest(httpServletResponse)
    }
}
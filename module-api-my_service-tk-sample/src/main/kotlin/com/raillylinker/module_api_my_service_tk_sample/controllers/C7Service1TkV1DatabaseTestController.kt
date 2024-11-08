package com.raillylinker.module_api_my_service_tk_sample.controllers

import com.fasterxml.jackson.annotation.JsonProperty
import com.raillylinker.module_api_my_service_tk_sample.services.C7Service1TkV1DatabaseTestService
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

@Tag(name = "/service1/tk/v1/database-test APIs", description = "C7 : Database 에 대한 테스트 API 컨트롤러")
@Controller
@RequestMapping("/service1/tk/v1/database-test")
class C7Service1TkV1DatabaseTestController(
    private val service: C7Service1TkV1DatabaseTestService
) {
    // <멤버 변수 공간>


    // ---------------------------------------------------------------------------------------------
    // <매핑 함수 공간>
    @Operation(
        summary = "N1 : DB Row 입력 테스트 API",
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
    fun api1InsertDataSample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @RequestBody
        inputVo: Api1InsertDataSampleInputVo
    ): Api1InsertDataSampleOutputVo? {
        return service.api1InsertDataSample(httpServletResponse, inputVo)
    }

    data class Api1InsertDataSampleInputVo(
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

    data class Api1InsertDataSampleOutputVo(
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
        summary = "N2 : DB Rows 삭제 테스트 API",
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
    fun api2DeleteRowsSample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(name = "deleteLogically", description = "논리적 삭제 여부", example = "true")
        @RequestParam("deleteLogically")
        deleteLogically: Boolean
    ) {
        service.api2DeleteRowsSample(httpServletResponse, deleteLogically)
    }


    ////
    @Operation(
        summary = "N3 : DB Row 삭제 테스트",
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
    fun api3DeleteRowSample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(name = "index", description = "글 인덱스", example = "1")
        @PathVariable("index")
        index: Long,
        @Parameter(name = "deleteLogically", description = "논리적 삭제 여부", example = "true")
        @RequestParam("deleteLogically")
        deleteLogically: Boolean
    ) {
        service.api3DeleteRowSample(httpServletResponse, index, deleteLogically)
    }


    ////
    @Operation(
        summary = "N4 : DB Rows 조회 테스트",
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
    fun api4SelectRowsSample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse
    ): Api4SelectRowsSampleOutputVo? {
        return service.api4SelectRowsSample(httpServletResponse)
    }

    data class Api4SelectRowsSampleOutputVo(
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
        summary = "N5 : DB 테이블의 random_num 컬럼 근사치 기준으로 정렬한 리스트 조회 API",
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
    fun api5SelectRowsOrderByRandomNumSample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(name = "num", description = "근사값 정렬의 기준", example = "1")
        @RequestParam("num")
        num: Int
    ): Api5SelectRowsOrderByRandomNumSampleOutputVo? {
        return service.api5SelectRowsOrderByRandomNumSample(httpServletResponse, num)
    }

    data class Api5SelectRowsOrderByRandomNumSampleOutputVo(
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
        summary = "N6 : DB 테이블의 row_create_date 컬럼 근사치 기준으로 정렬한 리스트 조회 API",
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
    fun api6SelectRowsOrderByRowCreateDateSample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(
            name = "dateString",
            description = "원하는 날짜(yyyy_MM_dd_'T'_HH_mm_ss_SSS_z)",
            example = "2024_05_02_T_15_14_49_552_KST"
        )
        @RequestParam("dateString")
        dateString: String
    ): Api6SelectRowsOrderByRowCreateDateSampleOutputVo? {
        return service.api6SelectRowsOrderByRowCreateDateSample(httpServletResponse, dateString)
    }

    data class Api6SelectRowsOrderByRowCreateDateSampleOutputVo(
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
        summary = "N7 : DB Rows 조회 테스트 (페이징)",
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
    fun api7SelectRowsPageSample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(name = "page", description = "원하는 페이지(1 부터 시작)", example = "1")
        @RequestParam("page")
        page: Int,
        @Parameter(name = "pageElementsCount", description = "페이지 아이템 개수", example = "10")
        @RequestParam("pageElementsCount")
        pageElementsCount: Int
    ): Api7SelectRowsPageSampleOutputVo? {
        return service.api7SelectRowsPageSample(httpServletResponse, page, pageElementsCount)
    }

    data class Api7SelectRowsPageSampleOutputVo(
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
        summary = "N8 : DB Rows 조회 테스트 (네이티브 쿼리 페이징)",
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
    fun api8SelectRowsNativeQueryPageSample(
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
    ): Api8SelectRowsNativeQueryPageSampleOutputVo? {
        return service.api8SelectRowsNativeQueryPageSample(httpServletResponse, page, pageElementsCount, num)
    }

    data class Api8SelectRowsNativeQueryPageSampleOutputVo(
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
        summary = "N9 : DB Row 수정 테스트",
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
    fun api9UpdateRowSample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(name = "testTableUid", description = "test 테이블의 uid", example = "1")
        @PathVariable("testTableUid")
        testTableUid: Long,
        @RequestBody
        inputVo: Api9UpdateRowSampleInputVo
    ): Api9UpdateRowSampleOutputVo? {
        return service.api9UpdateRowSample(httpServletResponse, testTableUid, inputVo)
    }

    data class Api9UpdateRowSampleInputVo(
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

    data class Api9UpdateRowSampleOutputVo(
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
        summary = "N10 : DB Row 수정 테스트 (네이티브 쿼리)",
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
    fun api10UpdateRowNativeQuerySample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(name = "testTableUid", description = "test 테이블의 uid", example = "1")
        @PathVariable("testTableUid")
        testTableUid: Long,
        @RequestBody
        inputVo: Api10UpdateRowNativeQuerySampleInputVo
    ) {
        return service.api10UpdateRowNativeQuerySample(httpServletResponse, testTableUid, inputVo)
    }

    data class Api10UpdateRowNativeQuerySampleInputVo(
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
        summary = "N11 : DB 정보 검색 테스트",
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
    fun api11SelectRowWhereSearchingKeywordSample(
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
    ): Api11SelectRowWhereSearchingKeywordSampleOutputVo? {
        return service.api11SelectRowWhereSearchingKeywordSample(
            httpServletResponse,
            page,
            pageElementsCount,
            searchKeyword
        )
    }

    data class Api11SelectRowWhereSearchingKeywordSampleOutputVo(
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
        summary = "N12 : 트랜젝션 동작 테스트",
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
    fun api12TransactionTest(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse
    ) {
        service.api12TransactionTest(httpServletResponse)
    }


    ////
    @Operation(
        summary = "N13 : 트랜젝션 비동작 테스트",
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
    fun api13NonTransactionTest(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse
    ) {
        service.api13NonTransactionTest(httpServletResponse)
    }


    ////
    @Operation(
        summary = "N13.1 : 트랜젝션 비동작 테스트(try-catch)",
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
    fun api13Dot1TryCatchNonTransactionTest(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse
    ) {
        service.api13Dot1TryCatchNonTransactionTest(httpServletResponse)
    }


    ////
    @Operation(
        summary = "N14 : DB Rows 조회 테스트 (중복 없는 네이티브 쿼리 페이징)",
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
    fun api14SelectRowsNoDuplicatePagingSample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(name = "lastItemUid", description = "이전 페이지에서 받은 마지막 아이템의 Uid (첫 요청이면 null)", example = "1")
        @RequestParam("lastItemUid")
        lastItemUid: Long?,
        @Parameter(name = "pageElementsCount", description = "페이지 아이템 개수", example = "10")
        @RequestParam("pageElementsCount")
        pageElementsCount: Int
    ): Api14SelectRowsNoDuplicatePagingSampleOutputVo? {
        return service.api14SelectRowsNoDuplicatePagingSample(httpServletResponse, lastItemUid, pageElementsCount)
    }

    data class Api14SelectRowsNoDuplicatePagingSampleOutputVo(
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
        summary = "N15 : DB Rows 조회 테스트 (카운팅)",
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
    fun api15SelectRowsCountSample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse
    ): Api15SelectRowsCountSampleOutputVo? {
        return service.api15SelectRowsCountSample(httpServletResponse)
    }

    data class Api15SelectRowsCountSampleOutputVo(
        @Schema(description = "아이템 전체 개수", required = true, example = "100")
        @JsonProperty("totalElements")
        val totalElements: Long
    )


    ////
    @Operation(
        summary = "N16 : DB Rows 조회 테스트 (네이티브 카운팅)",
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
    fun api16SelectRowsCountByNativeQuerySample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse
    ): Api16SelectRowsCountByNativeQuerySampleOutputVo? {
        return service.api16SelectRowsCountByNativeQuerySample(httpServletResponse)
    }

    data class Api16SelectRowsCountByNativeQuerySampleOutputVo(
        @Schema(description = "아이템 전체 개수", required = true, example = "100")
        @JsonProperty("totalElements")
        val totalElements: Long
    )


    ////
    @Operation(
        summary = "N17 : DB Row 조회 테스트 (네이티브)",
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
    fun api17SelectRowByNativeQuerySample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(name = "testTableUid", description = "test 테이블의 uid", example = "1")
        @PathVariable("testTableUid")
        testTableUid: Long
    ): Api17SelectRowByNativeQuerySampleOutputVo? {
        return service.api17SelectRowByNativeQuerySample(httpServletResponse, testTableUid)
    }

    data class Api17SelectRowByNativeQuerySampleOutputVo(
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
        summary = "N18 : 유니크 테스트 테이블 Row 입력 API",
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
    fun api18InsertUniqueTestTableRowSample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @RequestBody
        inputVo: Api18InsertUniqueTestTableRowSampleInputVo
    ): Api18InsertUniqueTestTableRowSampleOutputVo? {
        return service.api18InsertUniqueTestTableRowSample(httpServletResponse, inputVo)
    }

    data class Api18InsertUniqueTestTableRowSampleInputVo(
        @Schema(description = "유니크 값", required = true, example = "1")
        @JsonProperty("uniqueValue")
        val uniqueValue: Int
    )

    data class Api18InsertUniqueTestTableRowSampleOutputVo(
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
        summary = "N19 : 유니크 테스트 테이블 Rows 조회 테스트",
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
    fun api19SelectUniqueTestTableRowsSample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse
    ): Api19SelectUniqueTestTableRowsSampleOutputVo? {
        return service.api19SelectUniqueTestTableRowsSample(httpServletResponse)
    }

    data class Api19SelectUniqueTestTableRowsSampleOutputVo(
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
        summary = "N20 : 유니크 테스트 테이블 Row 수정 테스트",
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
    fun api20UpdateUniqueTestTableRowSample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(name = "uniqueTestTableUid", description = "unique test 테이블의 uid", example = "1")
        @PathVariable("uniqueTestTableUid")
        uniqueTestTableUid: Long,
        @RequestBody
        inputVo: Api20UpdateUniqueTestTableRowSampleInputVo
    ): Api20UpdateUniqueTestTableRowSampleOutputVo? {
        return service.api20UpdateUniqueTestTableRowSample(httpServletResponse, uniqueTestTableUid, inputVo)
    }

    data class Api20UpdateUniqueTestTableRowSampleInputVo(
        @Schema(description = "유니크 값", required = true, example = "1")
        @JsonProperty("uniqueValue")
        val uniqueValue: Int
    )

    data class Api20UpdateUniqueTestTableRowSampleOutputVo(
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
        summary = "N21 : 유니크 테스트 테이블 Row 삭제 테스트",
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
    fun api21DeleteUniqueTestTableRowSample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(name = "index", description = "글 인덱스", example = "1")
        @PathVariable("index")
        index: Long
    ) {
        service.api21DeleteUniqueTestTableRowSample(httpServletResponse, index)
    }


    ////
    @Operation(
        summary = "N22 : 외래키 부모 테이블 Row 입력 API",
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
    fun api22InsertFkParentRowSample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @RequestBody
        inputVo: Api22InsertFkParentRowSampleInputVo
    ): Api22InsertFkParentRowSampleOutputVo? {
        return service.api22InsertFkParentRowSample(httpServletResponse, inputVo)
    }

    data class Api22InsertFkParentRowSampleInputVo(
        @Schema(description = "외래키 테이블 부모 이름", required = true, example = "홍길동")
        @JsonProperty("fkParentName")
        val fkParentName: String
    )

    data class Api22InsertFkParentRowSampleOutputVo(
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
        summary = "N23 : 외래키 부모 테이블 아래에 자식 테이블의 Row 입력 API",
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
    fun api23InsertFkChildRowSample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(name = "parentUid", description = "외래키 부모 테이블 고유번호", example = "1")
        @PathVariable("parentUid")
        parentUid: Long,
        @RequestBody
        inputVo: Api23InsertFkChildRowSampleInputVo
    ): Api23InsertFkChildRowSampleOutputVo? {
        return service.api23InsertFkChildRowSample(httpServletResponse, parentUid, inputVo)
    }

    data class Api23InsertFkChildRowSampleInputVo(
        @Schema(description = "외래키 테이블 자식 이름", required = true, example = "홍길동")
        @JsonProperty("fkChildName")
        val fkChildName: String
    )

    data class Api23InsertFkChildRowSampleOutputVo(
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
        summary = "N24 : 외래키 관련 테이블 Rows 조회 테스트",
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
    fun api24SelectFkTestTableRowsSample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse
    ): Api24SelectFkTestTableRowsSampleOutputVo? {
        return service.api24SelectFkTestTableRowsSample(httpServletResponse)
    }

    data class Api24SelectFkTestTableRowsSampleOutputVo(
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
        summary = "N24.1 : 외래키 관련 테이블 Rows 조회 테스트(Native Join)",
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
    fun api24Dot1SelectFkTestTableRowsByNativeQuerySample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse
    ): Api24SelectFkTestTableRowsByNativeQuerySampleDot1OutputVo? {
        return service.api24Dot1SelectFkTestTableRowsByNativeQuerySample(httpServletResponse)
    }

    data class Api24SelectFkTestTableRowsByNativeQuerySampleDot1OutputVo(
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
        summary = "N25 : Native Query 반환값 테스트",
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
    fun api25GetNativeQueryReturnValueTest(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(name = "inputVal", description = "Native Query 비교문에 사용되는 파라미터", example = "true")
        @RequestParam("inputVal")
        inputVal: Boolean
    ): Api25GetNativeQueryReturnValueTestOutputVo? {
        return service.api25GetNativeQueryReturnValueTest(
            httpServletResponse,
            inputVal
        )
    }

    data class Api25GetNativeQueryReturnValueTestOutputVo(
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
        summary = "N26 : SQL Injection 테스트",
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
    fun api26SqlInjectionTest(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(name = "searchKeyword", description = "Select 문 검색에 사용되는 키워드", example = "test OR 1 = 1")
        @RequestParam("searchKeyword")
        searchKeyword: String
    ): Api26SqlInjectionTestOutputVo? {
        return service.api26SqlInjectionTest(
            httpServletResponse,
            searchKeyword
        )
    }

    data class Api26SqlInjectionTestOutputVo(
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
        summary = "N27 : 외래키 관련 테이블 Rows 조회 (네이티브 쿼리, 부모 테이블을 자식 테이블의 가장 최근 데이터만 Join)",
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
    fun api27SelectFkTableRowsWithLatestChildSample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse
    ): Api27SelectFkTableRowsWithLatestChildSampleOutputVo? {
        return service.api27SelectFkTableRowsWithLatestChildSample(httpServletResponse)
    }

    data class Api27SelectFkTableRowsWithLatestChildSampleOutputVo(
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
        summary = "N28 : 외래키 자식 테이블 Row 삭제 테스트",
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
    fun api28DeleteFkChildRowSample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(name = "index", description = "글 인덱스", example = "1")
        @PathVariable("index")
        index: Long
    ) {
        service.api28DeleteFkChildRowSample(httpServletResponse, index)
    }


    ////
    @Operation(
        summary = "N29 : 외래키 부모 테이블 Row 삭제 테스트 (Cascade 기능 확인)",
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
    fun api29DeleteFkParentRowSample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(name = "index", description = "글 인덱스", example = "1")
        @PathVariable("index")
        index: Long
    ) {
        service.api29DeleteFkParentRowSample(httpServletResponse, index)
    }


    ////
    @Operation(
        summary = "N35 : 외래키 테이블 트랜젝션 동작 테스트",
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
    fun api35FkTableTransactionTest(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse
    ) {
        service.api35FkTableTransactionTest(httpServletResponse)
    }


    ////
    @Operation(
        summary = "N36 : 외래키 테이블 트랜젝션 비동작 테스트",
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
    fun api36FkTableNonTransactionTest(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse
    ) {
        service.api36FkTableNonTransactionTest(httpServletResponse)
    }
}
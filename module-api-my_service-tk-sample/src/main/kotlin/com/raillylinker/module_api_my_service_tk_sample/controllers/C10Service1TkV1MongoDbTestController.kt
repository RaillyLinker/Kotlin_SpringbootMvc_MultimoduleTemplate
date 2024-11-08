package com.raillylinker.module_api_my_service_tk_sample.controllers

import com.fasterxml.jackson.annotation.JsonProperty
import com.raillylinker.module_api_my_service_tk_sample.services.C10Service1TkV1MongoDbTestService
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

/*
    !!!
    테스트를 하고 싶다면, 도커를 설치하고,
    cmd 를 열어,
    프로젝트 폴더 내의 external_files/dockers/mongodb_docker 로 이동 후,
    명령어.txt 에 적힌 명령어를 입력하여 Mongodb 를 실행시킬 수 있습니다.
    !!!
 */
@Tag(name = "/service1/tk/v1/mongodb-test APIs", description = "C10 : MongoDB 에 대한 테스트 API 컨트롤러")
@Controller
@RequestMapping("/service1/tk/v1/mongodb-test")
class C10Service1TkV1MongoDbTestController(
    private val service: C10Service1TkV1MongoDbTestService
) {
    // <멤버 변수 공간>


    // ---------------------------------------------------------------------------------------------
    // <매핑 함수 공간>
    @Operation(
        summary = "N1 : DB document 입력 테스트 API",
        description = "테스트 테이블에 document 를 입력합니다.\n\n"
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
        path = ["/test-document"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun api1InsertDocumentTest(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @RequestBody
        inputVo: Api1InsertDocumentTestInputVo
    ): Api1InsertDocumentTestOutputVo? {
        return service.api1InsertDocumentTest(httpServletResponse, inputVo)
    }

    data class Api1InsertDocumentTestInputVo(
        @Schema(description = "글 본문", required = true, example = "테스트 텍스트입니다.")
        @JsonProperty("content")
        val content: String,
        @Schema(description = "Nullable 값", required = false, example = "Not Null")
        @JsonProperty("nullableValue")
        val nullableValue: String?
    )

    data class Api1InsertDocumentTestOutputVo(
        @Schema(description = "글 고유번호", required = true, example = "1234")
        @JsonProperty("uid")
        val uid: String,
        @Schema(description = "글 본문", required = true, example = "테스트 텍스트입니다.")
        @JsonProperty("content")
        val content: String,
        @Schema(description = "Nullable 값", required = false, example = "Not Null")
        @JsonProperty("nullableValue")
        val nullableValue: String?,
        @Schema(description = "자동 생성 숫자", required = true, example = "21345")
        @JsonProperty("randomNum")
        val randomNum: Int,
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
        path = ["/test-document"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.ALL_VALUE]
    )
    @ResponseBody
    fun api2DeleteAllDocumentTest(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse
    ) {
        service.api2DeleteAllDocumentTest(httpServletResponse)
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
                                "1 : id 에 해당하는 데이터가 데이터베이스에 존재하지 않습니다.\n\n",
                        schema = Schema(type = "string")
                    )
                ]
            )
        ]
    )
    @DeleteMapping(
        path = ["/test-document/{id}"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.ALL_VALUE]
    )
    @ResponseBody
    fun api3DeleteDocumentTest(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(name = "id", description = "글 Id", example = "1")
        @PathVariable("id")
        id: String
    ) {
        service.api3DeleteDocumentTest(httpServletResponse, id)
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
        path = ["/test-document"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun api4SelectAllDocumentsTest(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse
    ): Api4SelectAllDocumentsTestOutputVo? {
        return service.api4SelectAllDocumentsTest(httpServletResponse)
    }

    data class Api4SelectAllDocumentsTestOutputVo(
        @Schema(description = "아이템 리스트", required = true)
        @JsonProperty("testEntityVoList")
        val testEntityVoList: List<TestEntityVo>
    ) {
        @Schema(description = "아이템")
        data class TestEntityVo(
            @Schema(description = "글 고유번호", required = true, example = "1234")
            @JsonProperty("uid")
            val uid: String,
            @Schema(description = "글 본문", required = true, example = "테스트 텍스트입니다.")
            @JsonProperty("content")
            val content: String,
            @Schema(description = "Nullable 값", required = false, example = "Not Null")
            @JsonProperty("nullableValue")
            val nullableValue: String?,
            @Schema(description = "자동 생성 숫자", required = true, example = "21345")
            @JsonProperty("randomNum")
            val randomNum: Int,
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


// todo 아래는 MongoDB 사용이 필요할 때에 추가하기

//    ////
//    @Operation(
//        summary = "N5 : DB 테이블의 random_num 컬럼 근사치 기준으로 정렬한 리스트 조회 API",
//        description = "테이블의 row 중 random_num 컬럼과 num 파라미터의 값의 근사치로 정렬한 리스트 반환\n\n"
//    )
//    @ApiResponses(
//        value = [
//            ApiResponse(
//                responseCode = "200",
//                description = "정상 동작"
//            )
//        ]
//    )
//    @GetMapping(
//        path = ["/test-document/order-by-random-num-nearest"],
//        consumes = [MediaType.ALL_VALUE],
//        produces = [MediaType.APPLICATION_JSON_VALUE]
//    )
//    @ResponseBody
//    fun api5(
//        @Parameter(hidden = true)
//        httpServletResponse: HttpServletResponse,
//        @Parameter(name = "num", description = "근사값 정렬의 기준", example = "1")
//        @RequestParam("num")
//        num: Int
//    ): Api5OutputVo? {
//        return service.api5(httpServletResponse, num)
//    }
//
//    data class Api5OutputVo(
//        @Schema(description = "아이템 리스트", required = true)
//        @JsonProperty("testEntityVoList")
//        val testEntityVoList: List<TestEntityVo>
//    ) {
//        @Schema(description = "아이템")
//        data class TestEntityVo(
//            @Schema(description = "글 고유번호", required = true, example = "1234")
//            @JsonProperty("uid")
//            val uid: String,
//            @Schema(description = "글 본문", required = true, example = "테스트 텍스트입니다.")
//            @JsonProperty("content")
//            val content: String,
//            @Schema(description = "자동 생성 숫자", required = true, example = "21345")
//            @JsonProperty("randomNum")
//            val randomNum: Int,
//            @Schema(description = "글 작성일", required = true, example = "2022-10-11T02:21:36.779")
//            @JsonProperty("createDate")
//            val createDate: String,
//            @Schema(description = "글 수정일", required = true, example = "2022-10-11T02:21:36.779")
//            @JsonProperty("updateDate")
//            val updateDate: String,
//            @Schema(description = "기준과의 절대거리", required = true, example = "34")
//            @JsonProperty("distance")
//            val distance: Int
//        )
//    }
//

    //    ////
//    @Operation(
//        summary = "N6 : DB 테이블의 row_create_date 컬럼 근사치 기준으로 정렬한 리스트 조회 API",
//        description = "테이블의 row 중 row_create_date 컬럼과 dateString 파라미터의 값의 근사치로 정렬한 리스트 반환\n\n"
//    )
//    @ApiResponses(
//        value = [
//            ApiResponse(
//                responseCode = "200",
//                description = "정상 동작"
//            )
//        ]
//    )
//    @GetMapping(
//        path = ["/rows/order-by-create-date-nearest"],
//        consumes = [MediaType.ALL_VALUE],
//        produces = [MediaType.APPLICATION_JSON_VALUE]
//    )
//    @ResponseBody
//    fun api6(
//        @Parameter(hidden = true)
//        httpServletResponse: HttpServletResponse,
//        @Parameter(name = "dateString", description = "원하는 날짜", example = "2022-10-11T02:21:36.779")
//        @RequestParam("dateString")
//        dateString: String
//    ): Api6OutputVo? {
//        return service.api6(httpServletResponse, dateString)
//    }
//
//    data class Api6OutputVo(
//        @Schema(description = "아이템 리스트", required = true)
//        @JsonProperty("testEntityVoList")
//        val testEntityVoList: List<TestEntityVo>
//    ) {
//        @Schema(description = "아이템")
//        data class TestEntityVo(
//            @Schema(description = "글 고유번호", required = true, example = "1234")
//            @JsonProperty("uid")
//            val uid: Long,
//            @Schema(description = "글 본문", required = true, example = "테스트 텍스트입니다.")
//            @JsonProperty("content")
//            val content: String,
//            @Schema(description = "자동 생성 숫자", required = true, example = "21345")
//            @JsonProperty("randomNum")
//            val randomNum: Int,
//            @Schema(description = "글 작성일", required = true, example = "2022-10-11T02:21:36.779")
//            @JsonProperty("createDate")
//            val createDate: String,
//            @Schema(description = "글 수정일", required = true, example = "2022-10-11T02:21:36.779")
//            @JsonProperty("updateDate")
//            val updateDate: String,
//            @Schema(description = "기준과의 절대차이(초)", required = true, example = "34")
//            @JsonProperty("timeDiffSec")
//            val timeDiffSec: Long
//        )
//    }
//
//
//    ////
//    @Operation(
//        summary = "N7 : DB Rows 조회 테스트 (페이징)",
//        description = "테스트 테이블의 Rows 를 페이징하여 반환합니다.\n\n"
//    )
//    @ApiResponses(
//        value = [
//            ApiResponse(
//                responseCode = "200",
//                description = "정상 동작"
//            )
//        ]
//    )
//    @GetMapping(
//        path = ["/rows/paging"],
//        consumes = [MediaType.ALL_VALUE],
//        produces = [MediaType.APPLICATION_JSON_VALUE]
//    )
//    @ResponseBody
//    fun api7(
//        @Parameter(hidden = true)
//        httpServletResponse: HttpServletResponse,
//        @Parameter(name = "page", description = "원하는 페이지(1 부터 시작)", example = "1")
//        @RequestParam("page")
//        page: Int,
//        @Parameter(name = "pageElementsCount", description = "페이지 아이템 개수", example = "10")
//        @RequestParam("pageElementsCount")
//        pageElementsCount: Int
//    ): Api7OutputVo? {
//        return service.api7(httpServletResponse, page, pageElementsCount)
//    }
//
//    data class Api7OutputVo(
//        @Schema(description = "아이템 전체 개수", required = true, example = "100")
//        @JsonProperty("totalElements")
//        val totalElements: Long,
//        @Schema(description = "아이템 리스트", required = true)
//        @JsonProperty("testEntityVoList")
//        val testEntityVoList: List<TestEntityVo>
//    ) {
//        @Schema(description = "아이템")
//        data class TestEntityVo(
//            @Schema(description = "글 고유번호", required = true, example = "1234")
//            @JsonProperty("uid")
//            val uid: Long,
//            @Schema(description = "글 본문", required = true, example = "테스트 텍스트입니다.")
//            @JsonProperty("content")
//            val content: String,
//            @Schema(description = "자동 생성 숫자", required = true, example = "23456")
//            @JsonProperty("randomNum")
//            val randomNum: Int,
//            @Schema(description = "글 작성일", required = true, example = "2022-10-11T02:21:36.779")
//            @JsonProperty("createDate")
//            val createDate: String,
//            @Schema(description = "글 수정일", required = true, example = "2022-10-11T02:21:36.779")
//            @JsonProperty("updateDate")
//            val updateDate: String
//        )
//    }
//
//
//    ////
//    @Operation(
//        summary = "N8 : DB Rows 조회 테스트 (네이티브 쿼리 페이징)",
//        description = "테스트 테이블의 Rows 를 네이티브 쿼리로 페이징하여 반환합니다.\n\n" +
//                "num 을 기준으로 근사치 정렬도 수행합니다.\n\n"
//    )
//    @ApiResponses(
//        value = [
//            ApiResponse(
//                responseCode = "200",
//                description = "정상 동작"
//            )
//        ]
//    )
//    @GetMapping(
//        path = ["/rows/native-paging"],
//        consumes = [MediaType.ALL_VALUE],
//        produces = [MediaType.APPLICATION_JSON_VALUE]
//    )
//    @ResponseBody
//    fun api8(
//        @Parameter(hidden = true)
//        httpServletResponse: HttpServletResponse,
//        @Parameter(name = "page", description = "원하는 페이지(1 부터 시작)", example = "1")
//        @RequestParam("page")
//        page: Int,
//        @Parameter(name = "pageElementsCount", description = "페이지 아이템 개수", example = "10")
//        @RequestParam("pageElementsCount")
//        pageElementsCount: Int,
//        @Parameter(name = "num", description = "근사값의 기준", example = "1")
//        @RequestParam("num")
//        num: Int
//    ): Api8OutputVo? {
//        return service.api8(httpServletResponse, page, pageElementsCount, num)
//    }
//
//    data class Api8OutputVo(
//        @Schema(description = "아이템 전체 개수", required = true, example = "100")
//        @JsonProperty("totalElements")
//        val totalElements: Long,
//        @Schema(description = "아이템 리스트", required = true)
//        @JsonProperty("testEntityVoList")
//        val testEntityVoList: List<TestEntityVo>
//    ) {
//        @Schema(description = "아이템")
//        data class TestEntityVo(
//            @Schema(description = "글 고유번호", required = true, example = "1")
//            @JsonProperty("uid")
//            val uid: Long,
//            @Schema(description = "글 본문", required = true, example = "테스트 텍스트입니다.")
//            @JsonProperty("content")
//            val content: String,
//            @Schema(description = "자동 생성 숫자", required = true, example = "21345")
//            @JsonProperty("randomNum")
//            val randomNum: Int,
//            @Schema(description = "글 작성일", required = true, example = "2022-10-11T02:21:36.779")
//            @JsonProperty("createDate")
//            val createDate: String,
//            @Schema(description = "글 수정일", required = true, example = "2022-10-11T02:21:36.779")
//            @JsonProperty("updateDate")
//            val updateDate: String,
//            @Schema(description = "기준과의 절대거리", required = true, example = "34")
//            @JsonProperty("distance")
//            val distance: Int
//        )
//    }
//
//
//    ////
//    @Operation(
//        summary = "N9 : DB Row 수정 테스트",
//        description = "테스트 테이블의 Row 하나를 수정합니다.\n\n"
//    )
//    @ApiResponses(
//        value = [
//            ApiResponse(
//                responseCode = "200",
//                description = "정상 동작"
//            ),
//            ApiResponse(
//                responseCode = "204",
//                content = [Content()],
//                description = "Response Body 가 없습니다.\n\n" +
//                        "Response Headers 를 확인하세요.",
//                headers = [
//                    Header(
//                        name = "api-result-code",
//                        description = "(Response Code 반환 원인) - Required\n\n" +
//                                "1 : testTableUid 에 해당하는 정보가 DB 에 존재하지 않습니다.\n\n",
//                        schema = Schema(type = "string")
//                    )
//                ]
//            )
//        ]
//    )
//    @PatchMapping(
//        path = ["/row/{testTableUid}"],
//        consumes = [MediaType.APPLICATION_JSON_VALUE],
//        produces = [MediaType.APPLICATION_JSON_VALUE]
//    )
//    @ResponseBody
//    fun api9(
//        @Parameter(hidden = true)
//        httpServletResponse: HttpServletResponse,
//        @Parameter(name = "testTableUid", description = "test 테이블의 uid", example = "1")
//        @PathVariable("testTableUid")
//        testTableUid: Long,
//        @RequestBody
//        inputVo: Api9InputVo
//    ): Api9OutputVo? {
//        return service.api9(httpServletResponse, testTableUid, inputVo)
//    }
//
//    data class Api9InputVo(
//        @Schema(description = "글 본문", required = true, example = "테스트 텍스트 수정글입니다.")
//        @JsonProperty("content")
//        val content: String
//    )
//
//    data class Api9OutputVo(
//        @Schema(description = "글 고유번호", required = true, example = "1234")
//        @JsonProperty("uid")
//        val uid: Long,
//        @Schema(description = "글 본문", required = true, example = "테스트 텍스트입니다.")
//        @JsonProperty("content")
//        val content: String,
//        @Schema(description = "자동 생성 숫자", required = true, example = "21345")
//        @JsonProperty("randomNum")
//        val randomNum: Int,
//        @Schema(description = "글 작성일", required = true, example = "2022-10-11T02:21:36.779")
//        @JsonProperty("createDate")
//        val createDate: String,
//        @Schema(description = "글 수정일", required = true, example = "2022-10-11T02:21:36.779")
//        @JsonProperty("updateDate")
//        val updateDate: String
//    )
//
//
//    ////
//    @Operation(
//        summary = "N10 : DB Row 수정 테스트 (네이티브 쿼리)",
//        description = "테스트 테이블의 Row 하나를 네이티브 쿼리로 수정합니다.\n\n"
//    )
//    @ApiResponses(
//        value = [
//            ApiResponse(
//                responseCode = "200",
//                description = "정상 동작"
//            ),
//            ApiResponse(
//                responseCode = "204",
//                content = [Content()],
//                description = "Response Body 가 없습니다.\n\n" +
//                        "Response Headers 를 확인하세요.",
//                headers = [
//                    Header(
//                        name = "api-result-code",
//                        description = "(Response Code 반환 원인) - Required\n\n" +
//                                "1 : testTableUid 에 해당하는 정보가 DB 에 존재하지 않습니다.\n\n",
//                        schema = Schema(type = "string")
//                    )
//                ]
//            )
//        ]
//    )
//    @PatchMapping(
//        path = ["/row/{testTableUid}/native-query"],
//        consumes = [MediaType.APPLICATION_JSON_VALUE],
//        produces = [MediaType.APPLICATION_JSON_VALUE]
//    )
//    @ResponseBody
//    fun api10(
//        @Parameter(hidden = true)
//        httpServletResponse: HttpServletResponse,
//        @Parameter(name = "testTableUid", description = "test 테이블의 uid", example = "1")
//        @PathVariable("testTableUid")
//        testTableUid: Long,
//        @RequestBody
//        inputVo: Api10InputVo
//    ) {
//        return service.api10(httpServletResponse, testTableUid, inputVo)
//    }
//
//    data class Api10InputVo(
//        @Schema(description = "글 본문", required = true, example = "테스트 텍스트 수정글입니다.")
//        @JsonProperty("content")
//        val content: String
//    )
//
//
//    ////
//    @Operation(
//        summary = "N11 : DB 정보 검색 테스트",
//        description = "글 본문 내용중 searchKeyword 가 포함된 rows 를 검색하여 반환합니다.\n\n"
//    )
//    @ApiResponses(
//        value = [
//            ApiResponse(
//                responseCode = "200",
//                description = "정상 동작"
//            )
//        ]
//    )
//    @GetMapping(
//        path = ["/search-content"],
//        consumes = [MediaType.ALL_VALUE],
//        produces = [MediaType.APPLICATION_JSON_VALUE]
//    )
//    @ResponseBody
//    fun api11(
//        @Parameter(hidden = true)
//        httpServletResponse: HttpServletResponse,
//        @Parameter(name = "page", description = "원하는 페이지(1 부터 시작)", example = "1")
//        @RequestParam("page")
//        page: Int,
//        @Parameter(name = "pageElementsCount", description = "페이지 아이템 개수", example = "10")
//        @RequestParam("pageElementsCount")
//        pageElementsCount: Int,
//        @Parameter(name = "searchKeyword", description = "검색어", example = "테스트")
//        @RequestParam("searchKeyword")
//        searchKeyword: String
//    ): Api11OutputVo? {
//        return service.api11(httpServletResponse, page, pageElementsCount, searchKeyword)
//    }
//
//    data class Api11OutputVo(
//        @Schema(description = "아이템 전체 개수", required = true, example = "100")
//        @JsonProperty("totalElements")
//        val totalElements: Long,
//        @Schema(description = "아이템 리스트", required = true)
//        @JsonProperty("testEntityVoList")
//        val testEntityVoList: List<TestEntityVo>
//    ) {
//        @Schema(description = "아이템")
//        data class TestEntityVo(
//            @Schema(description = "글 고유번호", required = true, example = "1")
//            @JsonProperty("uid")
//            val uid: Long,
//            @Schema(description = "글 본문", required = true, example = "테스트 텍스트입니다.")
//            @JsonProperty("content")
//            val content: String,
//            @Schema(description = "자동 생성 숫자", required = true, example = "21345")
//            @JsonProperty("randomNum")
//            val randomNum: Int,
//            @Schema(description = "글 작성일", required = true, example = "2022-10-11T02:21:36.779")
//            @JsonProperty("createDate")
//            val createDate: String,
//            @Schema(description = "글 수정일", required = true, example = "2022-10-11T02:21:36.779")
//            @JsonProperty("updateDate")
//            val updateDate: String
//        )
//    }
//
//
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
    fun api12TransactionRollbackTest(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse
    ) {
        service.api12TransactionRollbackTest(httpServletResponse)
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
    fun api13NoTransactionRollbackTest(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse
    ) {
        service.api13NoTransactionRollbackTest(httpServletResponse)
    }
//
//
//    ////
//    @Operation(
//        summary = "N14 : DB Rows 조회 테스트 (중복 없는 네이티브 쿼리 페이징)",
//        description = "테스트 테이블의 Rows 를 네이티브 쿼리로 중복없이 페이징하여 반환합니다.\n\n" +
//                "num 을 기준으로 근사치 정렬도 수행합니다.\n\n"
//    )
//    @ApiResponses(
//        value = [
//            ApiResponse(
//                responseCode = "200",
//                description = "정상 동작"
//            )
//        ]
//    )
//    @GetMapping(
//        path = ["/rows/native-paging-no-duplication"],
//        consumes = [MediaType.ALL_VALUE],
//        produces = [MediaType.APPLICATION_JSON_VALUE]
//    )
//    @ResponseBody
//    fun api14(
//        @Parameter(hidden = true)
//        httpServletResponse: HttpServletResponse,
//        @Parameter(name = "lastItemUid", description = "이전 페이지에서 받은 마지막 아이템의 Uid (첫 요청이면 null)", example = "1")
//        @RequestParam("lastItemUid")
//        lastItemUid: Long?,
//        @Parameter(name = "pageElementsCount", description = "페이지 아이템 개수", example = "10")
//        @RequestParam("pageElementsCount")
//        pageElementsCount: Int,
//        @Parameter(name = "num", description = "근사값의 기준", example = "1")
//        @RequestParam("num")
//        num: Int
//    ): Api14OutputVo? {
//        return service.api14(httpServletResponse, lastItemUid, pageElementsCount, num)
//    }
//
//    data class Api14OutputVo(
//        @Schema(description = "아이템 전체 개수", required = true, example = "100")
//        @JsonProperty("totalElements")
//        val totalElements: Long,
//        @Schema(description = "아이템 리스트", required = true)
//        @JsonProperty("testEntityVoList")
//        val testEntityVoList: List<TestEntityVo>
//    ) {
//        @Schema(description = "아이템")
//        data class TestEntityVo(
//            @Schema(description = "글 고유번호", required = true, example = "1")
//            @JsonProperty("uid")
//            val uid: Long,
//            @Schema(description = "글 본문", required = true, example = "테스트 텍스트입니다.")
//            @JsonProperty("content")
//            val content: String,
//            @Schema(description = "자동 생성 숫자", required = true, example = "21345")
//            @JsonProperty("randomNum")
//            val randomNum: Int,
//            @Schema(description = "글 작성일", required = true, example = "2022-10-11T02:21:36.779")
//            @JsonProperty("createDate")
//            val createDate: String,
//            @Schema(description = "글 수정일", required = true, example = "2022-10-11T02:21:36.779")
//            @JsonProperty("updateDate")
//            val updateDate: String,
//            @Schema(description = "기준과의 절대거리", required = true, example = "34")
//            @JsonProperty("distance")
//            val distance: Int
//        )
//    }
}
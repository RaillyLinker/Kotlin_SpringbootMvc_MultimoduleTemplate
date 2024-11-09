package com.raillylinker.module_api_my_service_tk_sample.controllers

import com.fasterxml.jackson.annotation.JsonProperty
import com.raillylinker.module_api_my_service_tk_sample.services.MyServiceTkSampleMongoDbTestService
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
@Tag(name = "/my-service/tk/sample/mongodb-test APIs", description = "MongoDB 에 대한 테스트 API 컨트롤러")
@Controller
@RequestMapping("/my-service/tk/sample/mongodb-test")
class MyServiceTkSampleMongoDbTestController(
    private val service: MyServiceTkSampleMongoDbTestService
) {
    // <멤버 변수 공간>


    // ---------------------------------------------------------------------------------------------
    // <매핑 함수 공간>
    @Operation(
        summary = "DB document 입력 테스트 API",
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
    fun insertDocumentTest(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @RequestBody
        inputVo: InsertDocumentTestInputVo
    ): InsertDocumentTestOutputVo? {
        return service.insertDocumentTest(httpServletResponse, inputVo)
    }

    data class InsertDocumentTestInputVo(
        @Schema(description = "글 본문", required = true, example = "테스트 텍스트입니다.")
        @JsonProperty("content")
        val content: String,
        @Schema(description = "Nullable 값", required = false, example = "Not Null")
        @JsonProperty("nullableValue")
        val nullableValue: String?
    )

    data class InsertDocumentTestOutputVo(
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
        path = ["/test-document"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.ALL_VALUE]
    )
    @ResponseBody
    fun deleteAllDocumentTest(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse
    ) {
        service.deleteAllDocumentTest(httpServletResponse)
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
    fun deleteDocumentTest(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(name = "id", description = "글 Id", example = "1")
        @PathVariable("id")
        id: String
    ) {
        service.deleteDocumentTest(httpServletResponse, id)
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
        path = ["/test-document"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun selectAllDocumentsTest(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse
    ): SelectAllDocumentsTestOutputVo? {
        return service.selectAllDocumentsTest(httpServletResponse)
    }

    data class SelectAllDocumentsTestOutputVo(
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
    fun transactionRollbackTest(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse
    ) {
        service.transactionRollbackTest(httpServletResponse)
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
    fun noTransactionRollbackTest(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse
    ) {
        service.noTransactionRollbackTest(httpServletResponse)
    }
}
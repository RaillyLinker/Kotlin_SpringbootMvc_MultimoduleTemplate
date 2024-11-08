package com.raillylinker.module_api_my_service_tk_sample.controllers

import com.fasterxml.jackson.annotation.JsonProperty
import com.raillylinker.module_api_my_service_tk_sample.services.C8Service1TkV1RedisTestService
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

// Key - Value 형식으로 저장되는 Redis Wrapper 를 사용하여 Database 의 Row 를 모사할 수 있으며,
// 이를 통해 데이터베이스 결과에 대한 캐싱을 구현할 수 있습니다.
/*
    !!!
    테스트를 하고 싶다면, 도커를 설치하고,
    cmd 를 열어,
    프로젝트 폴더 내의 external_files/docker/redis_docker 로 이동 후,
    명령어.txt 에 적힌 명령어를 입력하여 Redis 를 실행시킬 수 있습니다.
    !!!
 */
@Tag(name = "/service1/tk/v1/redis-test APIs", description = "C8 : Redis 에 대한 테스트 API 컨트롤러")
@Controller
@RequestMapping("/service1/tk/v1/redis-test")
class C8Service1TkV1RedisTestController(
    private val service: C8Service1TkV1RedisTestService
) {
    // <멤버 변수 공간>


    // ---------------------------------------------------------------------------------------------
    // <매핑 함수 공간>
    @Operation(
        summary = "N1 : Redis Key-Value 입력 테스트",
        description = "Redis 테이블에 Key-Value 를 입력합니다.\n\n"
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
        path = ["/test"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.ALL_VALUE]
    )
    @ResponseBody
    fun api1InsertRedisKeyValueTest(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @RequestBody inputVo: Api1InsertRedisKeyValueTestInputVo
    ) {
        service.api1InsertRedisKeyValueTest(httpServletResponse, inputVo)
    }

    data class Api1InsertRedisKeyValueTestInputVo(
        @Schema(description = "redis 키", required = true, example = "test_key")
        @JsonProperty("key")
        val key: String,
        @Schema(description = "글 본문", required = true, example = "테스트 텍스트입니다.")
        @JsonProperty("content")
        val content: String,
        @Schema(description = "데이터 만료시간(밀리 초, null 이라면 무한정)", required = false, example = "12000")
        @JsonProperty("expirationMs")
        val expirationMs: Long?
    )


    ////
    @Operation(
        summary = "N2 : Redis Key-Value 조회 테스트",
        description = "Redis Table 에 저장된 Key-Value 를 조회합니다.\n\n"
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
                                "1 : key 에 저장된 데이터가 존재하지 않습니다.\n\n",
                        schema = Schema(type = "string")
                    )
                ]
            )
        ]
    )
    @GetMapping(
        path = ["/test"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun api2SelectRedisValueSample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(name = "key", description = "redis 키", example = "test_key")
        @RequestParam("key")
        key: String
    ): Api2SelectRedisValueSampleOutputVo? {
        return service.api2SelectRedisValueSample(httpServletResponse, key)
    }

    data class Api2SelectRedisValueSampleOutputVo(
        @Schema(description = "Table 이름", required = true, example = "Redis1_Test")
        @JsonProperty("tableName")
        val tableName: String,
        @Schema(description = "Key", required = true, example = "testing")
        @JsonProperty("key")
        val key: String,
        @Schema(description = "글 본문", required = true, example = "테스트 텍스트입니다.")
        @JsonProperty("content")
        val content: String,
        @Schema(description = "데이터 만료시간(밀리 초, -1 이라면 무한정)", required = true, example = "12000")
        @JsonProperty("expirationMs")
        val expirationMs: Long
    )


    ////
    @Operation(
        summary = "N3 : Redis Key-Value 모두 조회 테스트",
        description = "Redis Table 에 저장된 모든 Key-Value 를 조회합니다.\n\n"
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
        path = ["/tests"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun api3SelectAllRedisKeyValueSample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse
    ): Api3SelectAllRedisKeyValueSampleOutputVo? {
        return service.api3SelectAllRedisKeyValueSample(
            httpServletResponse
        )
    }

    data class Api3SelectAllRedisKeyValueSampleOutputVo(
        @Schema(description = "Table 이름", required = true, example = "Redis1_Test")
        @JsonProperty("tableName")
        val tableName: String,

        @Schema(description = "Key-Value 리스트", required = true)
        @JsonProperty("keyValueList")
        val keyValueList: List<KeyValueVo>,
    ) {
        @Schema(description = "Key-Value 객체")
        data class KeyValueVo(
            @Schema(description = "Key", required = true, example = "testing")
            @JsonProperty("key")
            val key: String,
            @Schema(description = "글 본문", required = true, example = "테스트 텍스트입니다.")
            @JsonProperty("content")
            val content: String,
            @Schema(description = "데이터 만료시간(밀리 초, -1 이라면 무한정)", required = true, example = "12000")
            @JsonProperty("expirationMs")
            val expirationMs: Long
        )
    }


    ////
    @Operation(
        summary = "N4 : Redis Key-Value 삭제 테스트",
        description = "Redis Table 에 저장된 Key 를 삭제합니다.\n\n"
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
                                "1 : key 에 저장된 데이터가 존재하지 않습니다.\n\n",
                        schema = Schema(type = "string")
                    )
                ]
            )
        ]
    )
    @DeleteMapping(
        path = ["/test"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.ALL_VALUE]
    )
    @ResponseBody
    fun api4DeleteRedisKeySample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(name = "key", description = "redis 키", example = "test_key")
        @RequestParam("key")
        key: String
    ) {
        return service.api4DeleteRedisKeySample(httpServletResponse, key)
    }


    ////
    @Operation(
        summary = "N5 : Redis Key-Value 모두 삭제 테스트",
        description = "Redis Table 에 저장된 모든 Key 를 삭제합니다.\n\n"
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
        path = ["/test-all"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.ALL_VALUE]
    )
    @ResponseBody
    fun api5DeleteAllRedisKeySample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse
    ) {
        return service.api5DeleteAllRedisKeySample(httpServletResponse)
    }


    ////
    @Operation(
        summary = "N6 : Redis Lock 테스트",
        description = "Redis Lock 을 요청합니다.\n\n"
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
                                "1 : Redis Lock 상태\n\n",
                        schema = Schema(type = "string")
                    )
                ]
            )
        ]
    )
    @GetMapping(
        path = ["/try-redis-lock"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseBody
    fun api6TryRedisLockSample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse
    ): Api6TryRedisLockSampleOutputVo? {
        return service.api6TryRedisLockSample(httpServletResponse)
    }

    data class Api6TryRedisLockSampleOutputVo(
        @Schema(description = "Lock Key", required = true, example = "redisLockKey")
        @JsonProperty("lockKey")
        val lockKey: String
    )


    ////
    @Operation(
        summary = "N7 : Redis unLock 테스트",
        description = "Redis unLock 을 요청합니다.\n\n"
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
        path = ["/unlock-redis-lock"],
        consumes = [MediaType.ALL_VALUE],
        produces = [MediaType.ALL_VALUE]
    )
    @ResponseBody
    fun api7UnLockRedisLockSample(
        @Parameter(hidden = true)
        httpServletResponse: HttpServletResponse,
        @Parameter(name = "lockKey", description = "unLock 할 lockKey", example = "lockKey")
        @RequestParam("lockKey")
        lockKey: String
    ) {
        service.api7UnLockRedisLockSample(httpServletResponse, lockKey)
    }
}
package com.raillylinker.module_api_my_service_tk_sample.services

import com.raillylinker.module_api_my_service_tk_sample.controllers.MyServiceTkSampleRedisTestController
import jakarta.servlet.http.HttpServletResponse

/*
    Redis 는 주로 캐싱에 사용됩니다.
    이러한 특징에 기반하여, 본 프로젝트에서는 Redis 를 쉽고 편하게 사용하기 위하여 Key-Map 형식으로 래핑하여 사용하고 있으며,
    인 메모리 데이터 구조 저장소인 Redis 의 성능을 해치지 않기 위하여, Database 와는 달리 트랜젝션 처리를 따로 하지 않습니다.
    (Redis 는 애초에 고성능, 단순성을 위해 설계되었고, 롤백 기능을 지원하지 않으므로 일반적으로는 어플리케이션 레벨에서 처리합니다.)
    고로 Redis 에 값을 입력/삭제/수정하는 로직은, API 의 별도 다른 알고리즘이 모두 실행된 이후, "코드의 끝자락에서 한꺼번에 변경"하도록 처리하세요.
    그럼에도 트랜젝션 기능이 필요하다고 한다면,
    비동기 실행을 고려하여 Semaphore 등으로 락을 건 후, 기존 데이터를 백업한 후, 에러가 일어나면 복원하는 방식을 사용하면 됩니다.
 */
interface MyServiceTkSampleRedisTestService {
    // (Redis Key-Value 입력 테스트)
    fun insertRedisKeyValueTest(
        httpServletResponse: HttpServletResponse,
        inputVo: MyServiceTkSampleRedisTestController.InsertRedisKeyValueTestInputVo
    )


    ////
    // (Redis Key-Value 조회 테스트)
    fun selectRedisValueSample(
        httpServletResponse: HttpServletResponse,
        key: String
    ): MyServiceTkSampleRedisTestController.SelectRedisValueSampleOutputVo?


    ////
    // (Redis Key-Value 모두 조회 테스트)
    fun selectAllRedisKeyValueSample(httpServletResponse: HttpServletResponse): MyServiceTkSampleRedisTestController.SelectAllRedisKeyValueSampleOutputVo?


    ////
    // (Redis Key-Value 삭제 테스트)
    fun deleteRedisKeySample(httpServletResponse: HttpServletResponse, key: String)


    ////
    // (Redis Key-Value 모두 삭제 테스트)
    fun deleteAllRedisKeySample(httpServletResponse: HttpServletResponse)


    ////
    // (Redis Lock 테스트)
    fun tryRedisLockSample(httpServletResponse: HttpServletResponse): MyServiceTkSampleRedisTestController.TryRedisLockSampleOutputVo?


    ////
    // (Redis unLock 테스트)
    fun unLockRedisLockSample(httpServletResponse: HttpServletResponse, lockKey: String)
}
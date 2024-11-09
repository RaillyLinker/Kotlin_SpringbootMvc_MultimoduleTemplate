package com.raillylinker.module_api_my_service_tk_sample.services.impls

import com.raillylinker.module_api_my_service_tk_sample.controllers.MyServiceTkSampleRedisTestController
import com.raillylinker.module_api_my_service_tk_sample.services.MyServiceTkSampleRedisTestService
import com.raillylinker.module_redis.redis_map_components.redis1_main.Redis1_Lock_Test
import com.raillylinker.module_redis.redis_map_components.redis1_main.Redis1_Map_Test
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

/*
    Redis 는 주로 캐싱에 사용됩니다.
    이러한 특징에 기반하여, 본 프로젝트에서는 Redis 를 쉽고 편하게 사용하기 위하여 Key-Map 형식으로 래핑하여 사용하고 있으며,
    인 메모리 데이터 구조 저장소인 Redis 의 성능을 해치지 않기 위하여, Database 와는 달리 트랜젝션 처리를 따로 하지 않습니다.
    (Redis 는 애초에 고성능, 단순성을 위해 설계되었고, 롤백 기능을 지원하지 않으므로 일반적으로는 어플리케이션 레벨에서 처리합니다.)
    고로 Redis 에 값을 입력/삭제/수정하는 로직은, API 의 별도 다른 알고리즘이 모두 실행된 이후, "코드의 끝자락에서 한꺼번에 변경"하도록 처리하세요.
    그럼에도 트랜젝션 기능이 필요하다고 한다면,
    비동기 실행을 고려하여 Semaphore 등으로 락을 건 후, 기존 데이터를 백업한 후, 에러가 일어나면 복원하는 방식을 사용하면 됩니다.
 */
@Service
class MyServiceTkSampleRedisTestServiceImpl(
    // (프로젝트 실행시 사용 설정한 프로필명 (ex : dev8080, prod80, local8080, 설정 안하면 default 반환))
    @Value("\${spring.profiles.active:default}") private var activeProfile: String,

    private val redis1Test: Redis1_Map_Test,
    private val redis1LockTest: Redis1_Lock_Test
) : MyServiceTkSampleRedisTestService {
    // <멤버 변수 공간>
    private val classLogger: Logger = LoggerFactory.getLogger(this::class.java)


    // ---------------------------------------------------------------------------------------------
    // <공개 메소드 공간>
    override fun insertRedisKeyValueTest(
        httpServletResponse: HttpServletResponse,
        inputVo: MyServiceTkSampleRedisTestController.InsertRedisKeyValueTestInputVo
    ) {
        redis1Test.saveKeyValue(
            inputVo.key,
            Redis1_Map_Test.ValueVo(
                inputVo.content,
                Redis1_Map_Test.ValueVo.InnerVo("testObject", true),
                arrayListOf(
                    Redis1_Map_Test.ValueVo.InnerVo("testObject1", false),
                    Redis1_Map_Test.ValueVo.InnerVo("testObject2", true)
                )
            ),
            inputVo.expirationMs
        )

        httpServletResponse.status = HttpStatus.OK.value()
    }


    ////
    override fun selectRedisValueSample(
        httpServletResponse: HttpServletResponse,
        key: String
    ): MyServiceTkSampleRedisTestController.SelectRedisValueSampleOutputVo? {
        // 전체 조회 테스트
        val keyValue = redis1Test.findKeyValue(key)

        if (keyValue == null) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return null
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return MyServiceTkSampleRedisTestController.SelectRedisValueSampleOutputVo(
            Redis1_Map_Test.MAP_NAME,
            keyValue.key,
            keyValue.value.content,
            keyValue.expireTimeMs
        )
    }


    ////
    override fun selectAllRedisKeyValueSample(httpServletResponse: HttpServletResponse): MyServiceTkSampleRedisTestController.SelectAllRedisKeyValueSampleOutputVo? {
        // 전체 조회 테스트
        val keyValueList = redis1Test.findAllKeyValues()

        val testEntityListVoList =
            ArrayList<MyServiceTkSampleRedisTestController.SelectAllRedisKeyValueSampleOutputVo.KeyValueVo>()
        for (keyValue in keyValueList) {
            testEntityListVoList.add(
                MyServiceTkSampleRedisTestController.SelectAllRedisKeyValueSampleOutputVo.KeyValueVo(
                    keyValue.key,
                    keyValue.value.content,
                    keyValue.expireTimeMs
                )
            )
        }

        httpServletResponse.status = HttpStatus.OK.value()
        return MyServiceTkSampleRedisTestController.SelectAllRedisKeyValueSampleOutputVo(
            Redis1_Map_Test.MAP_NAME,
            testEntityListVoList
        )
    }


    ////
    override fun deleteRedisKeySample(httpServletResponse: HttpServletResponse, key: String) {
        val keyValue = redis1Test.findKeyValue(key)

        if (keyValue == null) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return
        }

        redis1Test.deleteKeyValue(key)

        httpServletResponse.status = HttpStatus.OK.value()
    }


    ////
    override fun deleteAllRedisKeySample(httpServletResponse: HttpServletResponse) {
        redis1Test.deleteAllKeyValues()

        httpServletResponse.status = HttpStatus.OK.value()
    }


    ////
    override fun tryRedisLockSample(httpServletResponse: HttpServletResponse): MyServiceTkSampleRedisTestController.TryRedisLockSampleOutputVo? {
        val lockKey = redis1LockTest.tryLock(100000)
        if (lockKey == null) {
            httpServletResponse.status = HttpStatus.NO_CONTENT.value()
            httpServletResponse.setHeader("api-result-code", "1")
            return null
        } else {
            httpServletResponse.status = HttpStatus.OK.value()
            return MyServiceTkSampleRedisTestController.TryRedisLockSampleOutputVo(lockKey)
        }
    }


    ////
    override fun unLockRedisLockSample(httpServletResponse: HttpServletResponse, lockKey: String) {
        redis1LockTest.unlock(lockKey)
        httpServletResponse.status = HttpStatus.OK.value()
    }
}
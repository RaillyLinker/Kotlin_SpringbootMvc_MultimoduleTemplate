package com.raillylinker.module_redis.redis_map_components.redis1_main

import com.raillylinker.module_redis.abstract_classes.BasicRedisMap
import com.raillylinker.module_redis.configurations.redis_configs.Redis1MainConfig
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

// [RedisMap 컴포넌트]
@Component
class Redis1_Map_Test(
    // !!!RedisConfig 종류 변경!!!
    @Qualifier(Redis1MainConfig.REDIS_TEMPLATE_NAME) val redisTemplate: RedisTemplate<String, String>
) : BasicRedisMap<Redis1_Map_Test.ValueVo>(redisTemplate, MAP_NAME, ValueVo::class.java) {
    // <멤버 변수 공간>
    companion object {
        // !!!중복되지 않도록, 본 클래스명을 MAP_NAME 으로 설정하기!!!
        const val MAP_NAME = "Redis1_Map_Test"
    }

    // !!!본 RedisMAP 의 Value 클래스 설정!!!
    data class ValueVo(
        // 기본 변수 타입 String 사용 예시
        var content: String,
        // Object 변수 타입 사용 예시
        var innerVo: InnerVo,
        // Object List 변수 타입 사용 예시
        var innerVoList: List<InnerVo>
    ) {
        // 예시용 Object 데이터 클래스
        data class InnerVo(
            var testString: String,
            var testBoolean: Boolean
        )
    }
}
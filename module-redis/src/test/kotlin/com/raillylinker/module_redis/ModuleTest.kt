package com.raillylinker.module_redis

import com.raillylinker.module_redis.redis_map_components.redis1_main.Redis1_Lock_Test
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ModuleTest {
    @Autowired
    lateinit var redis1LockTest: Redis1_Lock_Test

    @Test
    fun tryRedisLockAndUnlock() {
        // 첫 번째 tryLock 테스트 (락을 획득)
        val result1: String? = redis1LockTest.tryLock(1000L)
        Assertions.assertNotNull(result1, "첫 번째 tryLock은 UUID를 반환해야 합니다.")

        // 두 번째 tryLock 테스트 (락을 얻지 못함)
        val result2: String? = redis1LockTest.tryLock(1000L)
        Assertions.assertNull(result2, "두 번째 tryLock은 null을 반환해야 합니다.")

        // 락 해제 호출 (unlock)
        if (result1 != null) {
            redis1LockTest.unlock(result1)
        }

        // 다시 tryLock 호출 후 성공해야 함
        val result3: String? = redis1LockTest.tryLock(1000L)
        Assertions.assertNotNull(result3, "unlock 후에는 다시 tryLock이 성공해야 합니다.")

        // 마지막으로 락 해제
        if (result3 != null) {
            redis1LockTest.unlock(result3)
        }
    }
}
package com.raillylinker.module_template

import com.raillylinker.module_template.redis_map_components.redis1_main.Redis1_Map_RuntimeConfigIpList
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ModuleTest {
    private val classLogger: Logger = LoggerFactory.getLogger(this::class.java)

    @Autowired
    lateinit var redis1RuntimeConfigIpList: Redis1_Map_RuntimeConfigIpList

    @Test
    fun tryRedisLockAndUnlock() {
        val loggingDenyIpInfo = try {
            redis1RuntimeConfigIpList.findKeyValue(Redis1_Map_RuntimeConfigIpList.KeyEnum.LOGGING_DENY_IP_LIST.name)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

        classLogger.info(loggingDenyIpInfo.toString())
    }
}
package com.raillylinker.module_quartz.sys_components

import org.quartz.*
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class Test1QuartzConfig {
    companion object {
        const val QUARTZ_NAME = "Test1Quartz"
    }

//    @Bean("${QUARTZ_NAME}_Trigger")
//    fun jobTrigger(): Trigger {
//        return TriggerBuilder
//            .newTrigger()
//            // 트리거 ID 설정(같은 ID 는 주기당 한번씩만 동작함 = 클러스터 설정시 클러스터당 한번)
//            .withIdentity("${QUARTZ_NAME}_Trigger")
//            // Job Detail 설정
//            .forJob(testJobDetail())
//            // 실행 시간 설정(어플리케이션 실행 시점에 실행)
//            .startNow()
//            // 반복 스케쥴 설정
//            // 3초 마다
//            .withSchedule(CronScheduleBuilder.cronSchedule("*/3 * * * * ?"))
//            // 매일 0시
////            .withSchedule(CronScheduleBuilder.cronSchedule("0 * * * * ?"))
//            .build()
//    }
//
//    @Bean("${QUARTZ_NAME}_Job")
//    fun testJobDetail(): JobDetail {
//        return JobBuilder.newJob(TestQuartzJob::class.java)
//            .withIdentity("${QUARTZ_NAME}_Job")
//            .storeDurably()
//            .build()
//    }
//
//    class TestQuartzJob : Job {
//        override fun execute(p0: JobExecutionContext) {
//            println("Run $QUARTZ_NAME")
//        }
//    }
}
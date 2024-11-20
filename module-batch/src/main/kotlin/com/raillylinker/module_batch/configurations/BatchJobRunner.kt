package com.raillylinker.module_batch.configurations

import com.raillylinker.module_batch.configurations.batch_configs.TaskletBatchTestConfig
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

// Batch Job 을 어플리케이션 실행 시점에 실행하도록 해주는 코드
/*
    SpringBatch 에는 meta data 를 저장할 데이터베이스 테이블이 필요한데,
    이러한 테이블들은,
    외부 라이브러리 - Gradle: org.springframework.batch:spring-batch-core:5.1.2 -
    spring-batch-core-5.1.2.jar - org - springframework - batch - core
    안의 schema-{데이터베이스 종류}.sql 안에 저장된 SQL 문을 참고하여 생성하면 됩니다.
 */
@Component
class BatchJobRunner(
    private val jobLauncher: JobLauncher,
    // TaskletBatchTest Job
    @Qualifier(TaskletBatchTestConfig.BATCH_JOB_NAME)
    private val taskletBatchTestJob: Job
) : CommandLineRunner {
    override fun run(vararg args: String) {
        try {
            // (TaskletBatchTest Job 실행)
            jobLauncher.run(
                taskletBatchTestJob,
                JobParametersBuilder()
                    // 매 실행 시 고유한 파라미터를 추가하여 중복 실행 방지
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters()
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
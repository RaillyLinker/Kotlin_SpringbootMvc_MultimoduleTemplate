package com.raillylinker.module_batch.configurations.batch_configs

import com.raillylinker.module_jpa.configurations.jpa_configs.Db1MainConfig
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

@Configuration
@EnableBatchProcessing(
    // Batch 메타 데이터를 저장할 데이터베이스 정보
    dataSourceRef = "${Db1MainConfig.DATABASE_DIRECTORY_NAME}_DataSource",
    transactionManagerRef = Db1MainConfig.TRANSACTION_NAME,
    // Batch 메타 데이터 데이터베이스 테이블 접두사({스키마 명}.{배치 테이블 접두사})
    tablePrefix = "batch_metadata.BATCH_"
)
class TaskletBatchTestConfig(
    private val jobRepository: JobRepository,
    @Qualifier(Db1MainConfig.TRANSACTION_NAME)
    private val transactionManager: PlatformTransactionManager
) {
    companion object {
        // 배치 Job 이름
        const val BATCH_JOB_NAME = "TaskletBatchTest"
    }

    private val classLogger: Logger = LoggerFactory.getLogger(this::class.java)


    // ---------------------------------------------------------------------------------------------
    // [Batch Job 및 하위 작업 작성]
    // BatchConfig 하나당 하나의 Job 을 가져야 합니다.

    // (Batch Job)
    @Bean(BATCH_JOB_NAME)
    fun batchJob(): Job {
        return JobBuilder("myJob1", jobRepository)
            .start(taskletTestStep())
            .build()
    }

    // (Tasklet 테스트 Step)
    fun taskletTestStep(): Step {
        return StepBuilder("myStep1", jobRepository)
            .tasklet(
                { contribution: StepContribution, chunkContext: ChunkContext ->
                    classLogger.info("*********** Tasklet Batch Test ***********")
                    RepeatStatus.FINISHED
                },
                transactionManager
            )
            .build()
    }
}
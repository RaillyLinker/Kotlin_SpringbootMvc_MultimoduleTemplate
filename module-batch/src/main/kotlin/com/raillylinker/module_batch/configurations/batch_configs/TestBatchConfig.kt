package com.raillylinker.module_batch.configurations.batch_configs

import com.raillylinker.module_jpa.configurations.jpa_configs.Db1MainConfig
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

/*
    SpringBatch 에는 meta data 를 저장할 데이터베이스 테이블이 필요한데,
    이러한 테이블들은,
    외부 라이브러리 - Gradle: org.springframework.batch:spring-batch-core:5.1.2 -
    spring-batch-core-5.1.2.jar - org - springframework - batch - core
    안의 schema-{데이터베이스 종류}.sql 안에 저장된 SQL 문을 참고하여 생성하면 됩니다.
 */
@Configuration
@EnableBatchProcessing(
    dataSourceRef = "${Db1MainConfig.DATABASE_DIRECTORY_NAME}_DataSource",
    transactionManagerRef = Db1MainConfig.TRANSACTION_NAME,
    tablePrefix = "batch_metadata.BATCH_"
)
class TestBatchConfig {
    @Bean
    fun myJob(jobRepository: JobRepository, step: Step): Job {
        return JobBuilder("myJob1", jobRepository)
            .start(step)
            .build()
    }

    @Bean
    fun myStep(
        jobRepository: JobRepository,
        tasklet: Tasklet,
        @Qualifier(Db1MainConfig.TRANSACTION_NAME)
        transactionManager: PlatformTransactionManager
    ): Step {
        return StepBuilder("myStep1", jobRepository)
            .tasklet(tasklet, transactionManager)
            .build()
    }

    @Bean
    fun myTasklet(): Tasklet {
        return Tasklet { contribution: StepContribution?, chunkContext: ChunkContext? ->
            println("\n *********** my first spring batch ***********")
            RepeatStatus.FINISHED
        }
    }
}
package com.raillylinker.module_batch.configurations

import com.raillylinker.module_jpa.configurations.jpa_configs.Db1MainConfig
import org.springframework.batch.core.configuration.JobRegistry
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.support.MapJobRegistry
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Role


// [SpringBatch 설정]
@Configuration
@EnableBatchProcessing(
    // Batch 메타 데이터를 저장할 데이터베이스 정보
    dataSourceRef = "${Db1MainConfig.DATABASE_DIRECTORY_NAME}_DataSource",
    transactionManagerRef = Db1MainConfig.TRANSACTION_NAME,
    // Batch 메타 데이터 데이터베이스 테이블 접두사({스키마 명}.{배치 테이블 접두사})
    tablePrefix = "batch_metadata.BATCH_"
)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
class BatchConfig{
    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    fun jobRegistry(): JobRegistry {
        return MapJobRegistry()
    }
}
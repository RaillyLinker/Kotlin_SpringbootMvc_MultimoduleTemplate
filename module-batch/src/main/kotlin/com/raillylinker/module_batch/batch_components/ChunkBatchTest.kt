package com.raillylinker.module_batch.batch_components

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.raillylinker.module_jpa.configurations.jpa_configs.Db1MainConfig
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.ItemWriter
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.transaction.PlatformTransactionManager
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

@Component
class ChunkBatchTest(
    private val jobRepository: JobRepository,
    @Qualifier(Db1MainConfig.TRANSACTION_NAME)
    private val transactionManager: PlatformTransactionManager
) {
    companion object {
        // 배치 Job 이름
        const val BATCH_JOB_NAME = "ChunkBatchTest"
    }

    private val classLogger: Logger = LoggerFactory.getLogger(this::class.java)
    private val gson = Gson()

    // JSON 파일 저장 위치
    private val saveDirectoryPath: Path = Paths.get("./by_product_files/test/batch_test").toAbsolutePath().normalize()

    init {
        // 테스트용 데이터 파일이 없으면 생성
        Files.createDirectories(saveDirectoryPath)
        val files = Files.list(saveDirectoryPath).use { it.toList() }
        if (files.isEmpty()) {
            (1..10).forEach { index ->
                val fileName = "batch_test$index.json"
                val jsonObject = JsonObject().apply {
                    addProperty("name", (1..6).map { ('A'..'Z').random() }.joinToString(""))
                    addProperty("num", Random().nextInt(1000))
                }
                Files.writeString(saveDirectoryPath.resolve(fileName), gson.toJson(jsonObject))
            }

            classLogger.info("ChunkBatchTest : 파일 생성 완료")
        }
    }


    // ---------------------------------------------------------------------------------------------
    // [Batch Job 및 하위 작업 작성]
    // BatchConfig 하나당 하나의 Job 을 가져야 합니다.

    // (Batch Job)
    @Bean(BATCH_JOB_NAME)
    fun batchJob(): Job {
        return JobBuilder("${BATCH_JOB_NAME}_batchJob", jobRepository)
            .start(chunkTestStep())
            .build()
    }

    @Bean
    fun chunkTestStep(): Step {
        return StepBuilder("${BATCH_JOB_NAME}_chunkTestStep", jobRepository)
            .chunk<String, JsonObject>(10, transactionManager)
            .reader(jsonFileReader())
            .processor(jsonProcessor())
            .writer(jsonWriter())
            .build()
    }

    @Bean
    fun jsonFileReader(): ItemReader<String> {
        val files = Files.list(saveDirectoryPath).use { it.toList() }
        val iterator = files.map { it.toString() }.iterator()
        return ItemReader {
            if (iterator.hasNext()) iterator.next() else null
        }
    }

    @Bean
    fun jsonProcessor(): ItemProcessor<String, JsonObject> {
        return ItemProcessor { filePath ->
            val jsonString = Files.readString(Paths.get(filePath))
            val jsonObject = gson.fromJson(jsonString, JsonObject::class.java)
            jsonObject.addProperty("name", (1..6).map { ('A'..'Z').random() }.joinToString(""))
            jsonObject.addProperty("num", jsonObject.get("num").asInt + 1)
            jsonObject.addProperty("fileName", Paths.get(filePath).fileName.toString()) // 현재 파일 이름 추가
            jsonObject
        }
    }

    @Bean
    fun jsonWriter(): ItemWriter<JsonObject> {
        return ItemWriter { items ->
            items.forEach { jsonObject ->
                val fileName = jsonObject.get("fileName").asString
                jsonObject.remove("fileName")
                val filePath = saveDirectoryPath.resolve(fileName)
                Files.writeString(filePath, gson.toJson(jsonObject))
                classLogger.info("ChunkBatchTest : Updated $filePath")
            }
        }
    }
}
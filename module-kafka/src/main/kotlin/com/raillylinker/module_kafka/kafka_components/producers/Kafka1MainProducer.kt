package com.raillylinker.module_kafka.kafka_components.producers

import com.raillylinker.module_kafka.configurations.kafka_configs.Kafka1MainConfig
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class Kafka1MainProducer(
    @Qualifier(Kafka1MainConfig.PRODUCER_BEAN_NAME) private val kafka1MainProducerTemplate: KafkaTemplate<String, Any>,
) {
    // <멤버 변수 공간>
    private val classLogger: Logger = LoggerFactory.getLogger(this::class.java)

    // ---------------------------------------------------------------------------------------------
    // <공개 메소드 공간>
    // (testTopic1 에 메시지 발송)
    fun sendMessageToTestTopic1(message: SendMessageToTestTopic1InputVo) {
        // kafkaProducer1 에 토픽 메세지 발행
        kafka1MainProducerTemplate.send("testTopic1", message)
    }

    data class SendMessageToTestTopic1InputVo(
        val test: String,
        val test1: Int
    )
}
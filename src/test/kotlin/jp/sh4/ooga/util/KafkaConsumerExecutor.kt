package jp.sh4.ooga.util

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.IntegerDeserializer
import org.apache.kafka.common.serialization.StringDeserializer
import java.time.Duration
import java.util.Properties

class KafkaConsumerExecutor(private val topics: List<String>) {

    fun consumerRecord(): ConsumerRecords<Int, String> {
        val props = Properties()
        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:19092"
        props[ConsumerConfig.GROUP_ID_CONFIG] = "verify"
        props[ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG] = "false"
        props[ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG] = "30000"
        props[ConsumerConfig.ISOLATION_LEVEL_CONFIG] = "read_committed"
        props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
        val consumer = KafkaConsumer(props, IntegerDeserializer(), StringDeserializer()).apply {
            subscribe(topics)
        }
        var records: ConsumerRecords<Int, String> = ConsumerRecords.empty()
        consumer.use {
            var count = 0
            while (count < 5) {
                records = consumer.poll(Duration.ofSeconds(1))
                if (!records.isEmpty) {
                    consumer.commitSync()
                    return@use records
                } else {
                    count++
                }
            }
        }
        return records
    }
}

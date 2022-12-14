package jp.sh4.ooga

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.IntegerSerializer
import org.apache.kafka.common.serialization.StringSerializer
import java.util.*

object ProducerFactory {
    fun createInstance(): KafkaProducer<Int, String> {
        val props = Properties()
        props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:19092"
        props[ProducerConfig.BATCH_SIZE_CONFIG] = 1
        props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = IntegerSerializer::class.java
        props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        props[ProducerConfig.TRANSACTIONAL_ID_CONFIG] = "tx-"
        return KafkaProducer<Int, String>(props)
    }
}

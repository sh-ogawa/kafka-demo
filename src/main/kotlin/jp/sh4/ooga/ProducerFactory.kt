package jp.sh4.ooga

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.IntegerSerializer
import org.apache.kafka.common.serialization.StringSerializer
import org.slf4j.LoggerFactory
import java.util.*

object ProducerFactory {
    private val logger = LoggerFactory.getLogger(ProducerFactory::class.java)
    
    fun createInstance(): KafkaProducer<Int, String> {
        logger.info("Creating new KafkaProducer instance")
        val props = Properties()
        props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:19092"
        props[ProducerConfig.BATCH_SIZE_CONFIG] = 1
        props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = IntegerSerializer::class.java
        props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        props[ProducerConfig.TRANSACTIONAL_ID_CONFIG] = "tx-"
        
        logger.debug("Producer configuration: bootstrap.servers={}, batch.size={}, transactional.id={}",
            props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG],
            props[ProducerConfig.BATCH_SIZE_CONFIG],
            props[ProducerConfig.TRANSACTIONAL_ID_CONFIG])
            
        val producer = KafkaProducer<Int, String>(props)
        logger.info("KafkaProducer instance created successfully")
        return producer
    }
}

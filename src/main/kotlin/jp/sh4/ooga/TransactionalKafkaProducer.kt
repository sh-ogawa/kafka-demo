package jp.sh4.ooga

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory

class TransactionalKafkaProducer(
    producer: KafkaProducer<Int, String>? = null
) {
    private val logger = LoggerFactory.getLogger(TransactionalKafkaProducer::class.java)
    private var producer: KafkaProducer<Int, String>
    
    init {
        logger.info("Initializing TransactionalKafkaProducer")
        this.producer = producer ?: ProducerFactory.createInstance()
        logger.debug("TransactionalKafkaProducer initialized with producer: {}", this.producer)
    }

    @Suppress("TooGenericExceptionCaught")
    fun messageSend(topic: String, messages: List<String>) {
        logger.info("Starting to send {} messages to topic '{}'", messages.size, topic)
        producer.use {
            try {
                logger.debug("Initializing transaction")
                it.initTransactions()
                
                logger.debug("Beginning transaction")
                it.beginTransaction()
                
                logger.debug("Sending messages in transaction")
                messages.forEachIndexed { index, message ->
                    logger.trace("Sending message {}/{}: {}", index + 1, messages.size, message)
                    it.send(ProducerRecord(
                        topic,
                        Math.random().toInt(),
                        message,
                    ))
                }
                
                logger.debug("Committing transaction")
                it.commitTransaction()
                logger.info("Successfully sent {} messages to topic '{}'", messages.size, topic)
            } catch (e: Exception) {
                logger.error("Error while sending messages to topic '{}': {}", topic, e.message)
                logger.debug("Transaction failed, aborting", e)
                it.abortTransaction()
                logger.debug("Transaction aborted")
                throw e
            } finally {
                logger.debug("Message sending operation completed")
            }
        }
    }
}

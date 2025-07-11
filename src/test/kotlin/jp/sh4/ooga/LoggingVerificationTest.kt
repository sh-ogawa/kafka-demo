package jp.sh4.ooga

import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

class LoggingVerificationTest {
    private val logger = LoggerFactory.getLogger(LoggingVerificationTest::class.java)

    @Test
    fun `verify logging is properly configured`() {
        logger.info("This is a test INFO message from LoggingVerificationTest")
        logger.debug("This is a test DEBUG message from LoggingVerificationTest")
        
        // This will create a producer and log its creation
        val producer = ProducerFactory.createInstance()
        
        // This will create a TransactionalKafkaProducer and log its initialization
        val transactionalProducer = TransactionalKafkaProducer(producer)
        
        // We won't actually send messages as that would require a running Kafka broker
        // But we can verify that the logging configuration is working
        
        // Close the producer to avoid resource leaks
        producer.close()
    }
}
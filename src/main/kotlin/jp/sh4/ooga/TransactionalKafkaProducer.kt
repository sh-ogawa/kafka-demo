package jp.sh4.ooga

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord

class TransactionalKafkaProducer(
    producer: KafkaProducer<Int, String>? = null
) {
    private var producer: KafkaProducer<Int, String>
    init {
        this.producer = producer ?: ProducerFactory.createInstance()
    }

    @Suppress("TooGenericExceptionCaught")
    fun messageSend(topic: String, messages: List<String>) {
        producer.use {
            try {
                it.initTransactions()
                it.beginTransaction()
                messages.forEach { message ->
                    it.send(ProducerRecord(
                        topic,
                        Math.random().toInt(),
                        message,
                    ))
                }
                it.commitTransaction()
            } catch (e: Exception) {
                it.abortTransaction()
                println(e.stackTraceToString())
                throw e
            }
        }
    }
}

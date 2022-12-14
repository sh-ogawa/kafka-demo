package jp.sh4.ooga

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.spyk
import jp.sh4.ooga.util.KafkaConsumerExecutor
import org.apache.kafka.clients.admin.Admin
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.errors.TimeoutException
import java.util.Properties

private const val TEST_TOPIC = "TEST"

class TransactionalKafkaProducerTests: AnnotationSpec() {

    @BeforeClass
    fun setup() {
        val props = Properties()
        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:19092"
        val adminClient = Admin.create(props)
        adminClient.createTopics(listOf(
            NewTopic(TEST_TOPIC, 50, 1)
        ))
    }

    @Test
    fun confirmSendCompleted() {
        val testTarget = TransactionalKafkaProducer()
        val messages = listOf("message1", "message2")

        shouldNotThrowAny {
            testTarget.messageSend(TEST_TOPIC, messages)
        }

        val consumer = KafkaConsumerExecutor(listOf(TEST_TOPIC))
        val records = consumer.consumerRecord()
        records.count() shouldBe messages.size
        records.forEachIndexed { index, record ->
            record.value() shouldBe messages[index]
        }
    }

    @Test
    fun confirmRollback() {

        val producer = spyk(ProducerFactory.createInstance())
        val messages = listOf("message1", "message2")
        val testTarget = TransactionalKafkaProducer(producer)
        every {
            producer.commitTransaction()
        } throws (TimeoutException("thrown by mock"))
        shouldThrow<TimeoutException> {
            testTarget.messageSend(TEST_TOPIC, messages)
        }

        val consumer = KafkaConsumerExecutor(listOf(TEST_TOPIC))
        val records = consumer.consumerRecord()
        records.count() shouldBe 0
    }
}

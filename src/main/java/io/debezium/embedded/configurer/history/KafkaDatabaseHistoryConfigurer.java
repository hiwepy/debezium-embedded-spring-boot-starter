package io.debezium.embedded.configurer.history;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumDatabaseHistoryProperties;
import io.debezium.relational.history.KafkaDatabaseHistory;
import org.springframework.boot.context.properties.PropertyMapper;

import static io.debezium.relational.history.DatabaseHistory.CONFIGURATION_FIELD_PREFIX_STRING;

/**
 * Kafka 数据库历史记录配置器。
 */
public class KafkaDatabaseHistoryConfigurer extends AbstractDatabaseHistoryConfigurer {

    private static final String CONSUMER_PREFIX = CONFIGURATION_FIELD_PREFIX_STRING + "consumer.";
    private static final String PRODUCER_PREFIX = CONFIGURATION_FIELD_PREFIX_STRING + "producer.";
    @Override
    public String getDatabaseHistory(){
        return KafkaDatabaseHistory.class.getName();
    }

    /**
     * 应用数据库历史记录配置。
     *
     * @param map PropertyMapper 实例
     * @param builder 配置构建器
     * @param properties 数据库历史记录配置属性
     */
    @Override
    public void apply(PropertyMapper map, Configuration.Builder builder, DebeziumDatabaseHistoryProperties properties) {

        DebeziumDatabaseHistoryProperties.Kafka kafka = properties.getKafka();

        // 基础配置
        map.from(kafka::getTopic).whenHasText().to(value -> builder.with(KafkaDatabaseHistory.TOPIC.name(), value));
        map.from(kafka::getBootstrapServers).whenHasText().to(value -> builder.with(KafkaDatabaseHistory.BOOTSTRAP_SERVERS.name(), value));
        
        // 恢复配置
        map.from(kafka::getRecoveryPollIntervalMs).to(value -> builder.with(KafkaDatabaseHistory.RECOVERY_POLL_INTERVAL_MS.name(), value));
        map.from(kafka::getRecoveryAttempts).to(value -> builder.with(KafkaDatabaseHistory.RECOVERY_POLL_ATTEMPTS.name(), value));
        
        // 查询超时配置
        map.from(kafka::getQueryTimeoutMs).to(value -> builder.with(KafkaDatabaseHistory.KAFKA_QUERY_TIMEOUT_MS.name(), value));

        // 生产者配置
        if (kafka.getProducer() != null) {
            DebeziumDatabaseHistoryProperties.Kafka.Producer producer = kafka.getProducer();
            
            map.from(producer::getAcks).whenHasText().to(value -> builder.with(PRODUCER_PREFIX + "acks", value));
            map.from(producer::getRetries).to(value -> builder.with(PRODUCER_PREFIX + "retries", value));
            map.from(producer::getBatchSize).to(value -> builder.with(PRODUCER_PREFIX + "batch.size", value));
            map.from(producer::getLingerMs).to(value -> builder.with(PRODUCER_PREFIX + "linger.ms", value));
            map.from(producer::getBufferMemory).to(value -> builder.with(PRODUCER_PREFIX + "buffer.memory", value));
            map.from(producer::getCompressionType).whenHasText().to(value -> builder.with(PRODUCER_PREFIX + "compression.type", value));
            map.from(producer::getMaxRequestSize).to(value -> builder.with(PRODUCER_PREFIX + "max.request.size", value));
            map.from(producer::getRequestTimeoutMs).to(value -> builder.with(PRODUCER_PREFIX + "request.timeout.ms", value));
            map.from(producer::getMetadataMaxAgeMs).to(value -> builder.with(PRODUCER_PREFIX + "metadata.max.age.ms", value));
            map.from(producer::getConnectionsMaxIdleMs).to(value -> builder.with(PRODUCER_PREFIX + "connections.max.idle.ms", value));
            map.from(producer::getReconnectBackoffMs).to(value -> builder.with(PRODUCER_PREFIX + "reconnect.backoff.ms", value));
            map.from(producer::getRetryBackoffMs).to(value -> builder.with(PRODUCER_PREFIX + "retry.backoff.ms", value));
        }
        
        // 消费者配置
        if (kafka.getConsumer() != null) {
            DebeziumDatabaseHistoryProperties.Kafka.Consumer consumer = kafka.getConsumer();

            map.from(consumer::getAutoOffsetReset).whenHasText().to(value -> builder.with(CONSUMER_PREFIX + "auto.offset.reset", value));
            map.from(consumer::getEnableAutoCommit).to(value -> builder.with(CONSUMER_PREFIX + "enable.auto.commit", value));
            map.from(consumer::getSessionTimeoutMs).to(value -> builder.with(CONSUMER_PREFIX + "session.timeout.ms", value));
            map.from(consumer::getHeartbeatIntervalMs).to(value -> builder.with(CONSUMER_PREFIX + "heartbeat.interval.ms", value));
            map.from(consumer::getMaxPollRecords).to(value -> builder.with(CONSUMER_PREFIX + "max.poll.records", value));
            map.from(consumer::getMaxPollIntervalMs).to(value -> builder.with(CONSUMER_PREFIX + "max.poll.interval.ms", value));
            map.from(consumer::getRequestTimeoutMs).to(value -> builder.with(CONSUMER_PREFIX + "request.timeout.ms", value));
            map.from(consumer::getFetchMinBytes).to(value -> builder.with(CONSUMER_PREFIX + "fetch.min.bytes", value));
            map.from(consumer::getFetchMaxWaitMs).to(value -> builder.with(CONSUMER_PREFIX + "fetch.max.wait.ms", value));
            map.from(consumer::getConnectionsMaxIdleMs).to(value -> builder.with(CONSUMER_PREFIX + "connections.max.idle.ms", value));
            map.from(consumer::getReconnectBackoffMs).to(value -> builder.with(CONSUMER_PREFIX + "reconnect.backoff.ms", value));
            map.from(consumer::getRetryBackoffMs).to(value -> builder.with(CONSUMER_PREFIX + "retry.backoff.ms", value));
        }
        
        // 安全配置
        if (kafka.getSecurity() != null) {
            DebeziumDatabaseHistoryProperties.Kafka.Security security = kafka.getSecurity();
            
            map.from(security::getSecurityProtocol).whenHasText().to(value -> builder.with("database.history.kafka.security.protocol", value));
            map.from(security::getSaslMechanism).whenHasText().to(value -> builder.with("database.history.kafka.sasl.mechanism", value));
            map.from(security::getSaslUsername).whenHasText().to(value -> builder.with("database.history.kafka.sasl.username", value));
            map.from(security::getSaslPassword).whenHasText().to(value -> builder.with("database.history.kafka.sasl.password", value));
            map.from(security::getSslTruststoreLocation).whenHasText().to(value -> builder.with("database.history.kafka.ssl.truststore.location", value));
            map.from(security::getSslTruststorePassword).whenHasText().to(value -> builder.with("database.history.kafka.ssl.truststore.password", value));
            map.from(security::getSslKeystoreLocation).whenHasText().to(value -> builder.with("database.history.kafka.ssl.keystore.location", value));
            map.from(security::getSslKeystorePassword).whenHasText().to(value -> builder.with("database.history.kafka.ssl.keystore.password", value));
            map.from(security::getSslKeyPassword).whenHasText().to(value -> builder.with("database.history.kafka.ssl.key.password", value));
            map.from(security::getSslEndpointIdentificationAlgorithm).whenHasText().to(value -> builder.with("database.history.kafka.ssl.endpoint.identification.algorithm", value));
        }
    }

}

package io.debezium.embedded.history;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumSchemaHistoryProperties;
import org.springframework.boot.context.properties.PropertyMapper;

/**
 * Redis 数据库历史记录配置器。
 * @see <a href="https://debezium.io/documentation/reference/3.2/configuration/storage.html">storage.html</a>
 */
public class RedisSchemaHistoryConfigurer implements SchemaHistoryConfigurer {
    
    /**
     * 应用数据库历史记录配置。
     *
     * @param builder 配置构建器
     * @param properties 数据库历史记录配置属性
     */
    @Override
    public void apply(Configuration.Builder builder, DebeziumSchemaHistoryProperties properties) {
        DebeziumSchemaHistoryProperties.Redis redis = properties.getRedis();
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        
        builder.with("schema.history.internal", "io.debezium.storage.redis.history.RedisSchemaHistory");
        
        // 严格按照官方文档配置参数
        map.from(redis::getAddress).whenHasText().to(value -> builder.with("schema.history.internal.storage.redis.address", value));
        map.from(redis::getDatabase).to(value -> builder.with("schema.history.internal.storage.redis.database", value));
        map.from(redis::getKey).whenHasText().to(value -> builder.with("schema.history.internal.storage.redis.key", value));
        map.from(redis::getPassword).whenHasText().to(value -> builder.with("schema.history.internal.storage.redis.password", value));
        map.from(redis::getUsername).whenHasText().to(value -> builder.with("schema.history.internal.storage.redis.username", value));
        map.from(redis::getClientName).whenHasText().to(value -> builder.with("schema.history.internal.storage.redis.client.name", value));
        map.from(redis::getConnectionTimeout).to(value -> builder.with("schema.history.internal.storage.redis.connection.timeout.ms", value));
        map.from(redis::getSocketTimeout).to(value -> builder.with("schema.history.internal.storage.redis.socket.timeout.ms", value));
        map.from(redis::getRetryInitialDelay).to(value -> builder.with("schema.history.internal.storage.redis.retry.initial.delay.ms", value));
        map.from(redis::getRetryMaxDelay).to(value -> builder.with("schema.history.internal.storage.redis.retry.max.delay.ms", value));
        map.from(redis::getRetryMaxAttempts).to(value -> builder.with("schema.history.internal.storage.redis.retry.max.attempts", value));
        map.from(redis::getWaitEnabled).to(value -> builder.with("schema.history.internal.storage.redis.wait.enabled", value));
        map.from(redis::getWaitTimeout).to(value -> builder.with("schema.history.internal.storage.redis.wait.timeout.ms", value));
        map.from(redis::getWaitRetryEnabled).to(value -> builder.with("schema.history.internal.storage.redis.wait.retry.enabled", value));
        map.from(redis::getWaitRetryDelay).to(value -> builder.with("schema.history.internal.storage.redis.wait.retry.delay.ms", value));
        map.from(redis::getSsl).to(value -> builder.with("schema.history.internal.storage.redis.ssl.enabled", value));
        map.from(redis::getSslCertPath).whenHasText().to(value -> builder.with("schema.history.internal.storage.redis.ssl.cert.path", value));
        map.from(redis::getSslKeyPath).whenHasText().to(value -> builder.with("schema.history.internal.storage.redis.ssl.key.path", value));
        map.from(redis::getSslCaPath).whenHasText().to(value -> builder.with("schema.history.internal.storage.redis.ssl.ca.path", value));
        map.from(redis::getSslKeystorePath).whenHasText().to(value -> builder.with("schema.history.internal.storage.redis.ssl.keystore.path", value));
        map.from(redis::getSslKeystorePassword).whenHasText().to(value -> builder.with("schema.history.internal.storage.redis.ssl.keystore.password", value));
        map.from(redis::getSslKeystoreType).whenHasText().to(value -> builder.with("schema.history.internal.storage.redis.ssl.keystore.type", value));
    }
}

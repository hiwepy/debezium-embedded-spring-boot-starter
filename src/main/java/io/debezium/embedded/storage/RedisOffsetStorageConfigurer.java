package io.debezium.embedded.storage;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumOffsetStorageProperties;
import org.springframework.boot.context.properties.PropertyMapper;

/**
 * Redis 型 Offset 存储配置。
 * @see <a href="https://debezium.io/documentation/reference/3.2/configuration/storage.html">storage.html</a>
 */
public class RedisOffsetStorageConfigurer implements OffsetStorageConfigurer {

    /**
     * 应用存储配置。
     *
     * @param builder 配置构建器
     * @param properties 存储配置属性
     */
    @Override
    public void apply(Configuration.Builder builder, DebeziumOffsetStorageProperties properties) {
        DebeziumOffsetStorageProperties.Redis redis = properties.getRedis();
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();

        // Offset Store - 严格按照官方文档配置
        builder.with("offset.storage", "io.debezium.storage.redis.RedisOffsetBackingStore");
        map.from(redis::getAddress).whenHasText().to(value -> builder.with("offset.storage.redis.address", value));
        map.from(redis::getDatabase).to(value -> builder.with("offset.storage.redis.database", value));
        map.from(redis::getPassword).whenHasText().to(value -> builder.with("offset.storage.redis.password", value));
        map.from(redis::getUsername).whenHasText().to(value -> builder.with("offset.storage.redis.username", value));
        map.from(redis::getClientName).whenHasText().to(value -> builder.with("offset.storage.redis.client.name", value));
        map.from(redis::getConnectionTimeout).to(value -> builder.with("offset.storage.redis.connection.timeout.ms", value));
        map.from(redis::getReadTimeout).to(value -> builder.with("offset.storage.redis.read.timeout.ms", value));
        map.from(redis::getPoolSize).to(value -> builder.with("offset.storage.redis.pool.size", value));
        map.from(redis::getSsl).to(value -> builder.with("offset.storage.redis.ssl.enabled", value));
        map.from(redis::getSslCertPath).whenHasText().to(value -> builder.with("offset.storage.redis.ssl.cert.path", value));
        map.from(redis::getSslKeyPath).whenHasText().to(value -> builder.with("offset.storage.redis.ssl.key.path", value));
        map.from(redis::getSslCaPath).whenHasText().to(value -> builder.with("offset.storage.redis.ssl.ca.path", value));
        map.from(redis::getMaxRetries).to(value -> builder.with("offset.storage.redis.max.retries", value));
        map.from(redis::getRetryDelayMs).to(value -> builder.with("offset.storage.redis.retry.delay.ms", value));
        map.from(redis::getFlushIntervalMs).to(value -> builder.with("offset.flush.interval.ms", value));
    }
}

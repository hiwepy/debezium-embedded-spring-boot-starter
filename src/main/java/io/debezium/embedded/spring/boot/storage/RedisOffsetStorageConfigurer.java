package io.debezium.embedded.spring.boot.storage;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumOffsetStorageProperties;
import org.springframework.boot.context.properties.PropertyMapper;

/**
 * Redis 型 Offset 存储配置。
 */
public class RedisOffsetStorageConfigurer implements OffsetStorageConfigurer {
    @Override
    public void apply(Configuration.Builder builder, DebeziumOffsetStorageProperties properties) {
        DebeziumOffsetStorageProperties.Redis redis = properties.getRedis();
        
        builder.with("offset.storage", "io.debezium.storage.redis.RedisOffsetBackingStore");
        
        /*
         * 批量设置参数
         */
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        
        // 基础连接配置
        map.from(redis::getHost).whenHasText().to(value -> builder.with("offset.storage.redis.host", value));
        map.from(redis::getPort).to(value -> builder.with("offset.storage.redis.port", value));
        map.from(redis::getDatabase).to(value -> builder.with("offset.storage.redis.database", value));
        map.from(redis::getKeyPrefix).whenHasText().to(value -> builder.with("offset.storage.redis.key.prefix", value));
        map.from(redis::getPassword).whenHasText().to(value -> builder.with("offset.storage.redis.password", value));
        
        // 认证配置
        map.from(redis::getUsername).whenHasText().to(value -> builder.with("offset.storage.redis.username", value));
        map.from(redis::getClientName).whenHasText().to(value -> builder.with("offset.storage.redis.client.name", value));
        
        // 超时和连接池配置
        map.from(redis::getConnectionTimeout).to(value -> builder.with("offset.storage.redis.connection.timeout.ms", value));
        map.from(redis::getReadTimeout).to(value -> builder.with("offset.storage.redis.read.timeout.ms", value));
        map.from(redis::getPoolSize).to(value -> builder.with("offset.storage.redis.pool.size", value));
        
        // SSL/TLS 配置
        map.from(redis::getSsl).to(value -> builder.with("offset.storage.redis.ssl", value));
        map.from(redis::getSslCertPath).whenHasText().to(value -> builder.with("offset.storage.redis.ssl.cert.path", value));
        map.from(redis::getSslKeyPath).whenHasText().to(value -> builder.with("offset.storage.redis.ssl.key.path", value));
        map.from(redis::getSslCaPath).whenHasText().to(value -> builder.with("offset.storage.redis.ssl.ca.path", value));
        
        // 重试配置
        map.from(redis::getMaxRetries).to(value -> builder.with("offset.storage.redis.max.retries", value));
        map.from(redis::getRetryDelayMs).to(value -> builder.with("offset.storage.redis.retry.delay.ms", value));
        
        // 刷新配置
        map.from(redis::getFlushIntervalMs).to(value -> builder.with("offset.flush.interval.ms", value));
    }
}

package io.debezium.embedded.spring.boot.storage;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumOffsetStorageProperties;

/**
 * Redis 型 Offset 存储配置。
 */
public class RedisOffsetStorageConfigurer implements OffsetStorageConfigurer {
    @Override
    public void apply(Configuration.Builder builder, DebeziumOffsetStorageProperties properties) {
        DebeziumOffsetStorageProperties.Redis r = properties.getRedis();
        builder
                .with("offset.storage", "io.debezium.storage.redis.RedisOffsetBackingStore")
                .with("offset.storage.redis.host", r.getHost())
                .with("offset.storage.redis.port", r.getPort())
                .with("offset.storage.redis.password", r.getPassword())
                .with("offset.storage.redis.database", r.getDatabase())
                .with("offset.storage.redis.key.prefix", r.getKeyPrefix())
                .with("offset.flush.interval.ms", r.getFlushIntervalMs());
    }
}

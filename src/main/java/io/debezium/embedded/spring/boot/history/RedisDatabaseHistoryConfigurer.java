package io.debezium.embedded.spring.boot.history;

import io.debezium.config.Configuration;
import org.springframework.boot.context.properties.PropertyMapper;

/**
 * Redis 数据库历史记录配置器。
 */
public class RedisDatabaseHistoryConfigurer implements DatabaseHistoryConfigurer {
    
    @Override
    public void apply(Configuration.Builder builder, DebeziumDatabaseHistoryProperties properties) {
        DebeziumDatabaseHistoryProperties.Redis redis = properties.getRedis();
        
        builder.with("database.history", "io.debezium.relational.history.RedisDatabaseHistory");
        
        /*
         * 批量设置参数
         */
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        map.from(redis::getHost).whenHasText().to(value -> builder.with("database.history.redis.host", value));
        map.from(redis::getPort).to(value -> builder.with("database.history.redis.port", value));
        map.from(redis::getDatabase).to(value -> builder.with("database.history.redis.database", value));
        map.from(redis::getKeyPrefix).whenHasText().to(value -> builder.with("database.history.redis.key.prefix", value));
        map.from(redis::getPassword).whenHasText().to(value -> builder.with("database.history.redis.password", value));
        map.from(redis::getConnectionTimeout).to(value -> builder.with("database.history.redis.connection.timeout.ms", value));
        map.from(redis::getReadTimeout).to(value -> builder.with("database.history.redis.read.timeout.ms", value));
        map.from(redis::getPoolSize).to(value -> builder.with("database.history.redis.pool.size", value));
    }
}

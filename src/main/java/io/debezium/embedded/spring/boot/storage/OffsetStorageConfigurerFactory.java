package io.debezium.embedded.spring.boot.storage;

import io.debezium.embedded.spring.boot.DebeziumOffsetStorageProperties;

/**
 * Offset 存储配置器工厂。
 */
public class OffsetStorageConfigurerFactory {
    
    /**
     * 根据存储类型创建对应的配置器。
     *
     * @param properties 存储配置属性
     * @return 存储配置器
     */
    public static OffsetStorageConfigurer from(DebeziumOffsetStorageProperties properties) {
        return switch (properties.getType()) {
            case FILE -> new FileOffsetStorageConfigurer();
            case KAFKA -> new KafkaOffsetStorageConfigurer();
            case JDBC -> new JdbcOffsetStorageConfigurer();
            case REDIS -> new RedisOffsetStorageConfigurer();
            case CUSTOM -> new CustomOffsetStorageConfigurer();
        };
    }
}



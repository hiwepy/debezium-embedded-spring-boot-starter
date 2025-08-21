package io.debezium.embedded.configurer.storage;

import io.debezium.embedded.spring.boot.DebeziumOffsetStorageProperties;

/**
 * Offset 存储配置器工厂。
 * @see <a href="https://debezium.io/documentation//reference/1.9/development/engine.html">engine.html</a>
 */
public class OffsetStorageConfigurerFactory {
    
    /**
     * 根据存储类型创建对应的配置器。
     *
     * @param properties 存储配置属性
     * @return 存储配置器
     */
    public static OffsetStorageConfigurer from(DebeziumOffsetStorageProperties properties) {
        switch (properties.getType()) {
            case FILE:
                return new FileOffsetStorageConfigurer();
            case KAFKA:
                return new KafkaOffsetStorageConfigurer();
            case CUSTOM:
                return new CustomOffsetStorageConfigurer();
            default:
                throw new IllegalArgumentException("Unsupported offset storage type: " + properties.getType());
        }
    }
}



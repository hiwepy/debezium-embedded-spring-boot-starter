package io.debezium.embedded.spring.boot.storage;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumOffsetStorageProperties;

/**
 * 自定义 Offset 存储配置。
 */
public class CustomOffsetStorageConfigurer implements OffsetStorageConfigurer {
    @Override
    public void apply(Configuration.Builder builder, DebeziumOffsetStorageProperties properties) {
        DebeziumOffsetStorageProperties.Custom c = properties.getCustom();
        builder.with("offset.storage", c.getClassName());
        if (c.getProps() != null) {
            c.getProps().forEach(builder::with);
        }
    }
}



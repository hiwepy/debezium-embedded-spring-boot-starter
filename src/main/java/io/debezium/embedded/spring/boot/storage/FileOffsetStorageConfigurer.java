package io.debezium.embedded.spring.boot.storage;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumOffsetStorageProperties;
import org.springframework.boot.context.properties.PropertyMapper;

/**
 * File 型 Offset 存储配置。
 */
public class FileOffsetStorageConfigurer implements OffsetStorageConfigurer {
    @Override
    public void apply(Configuration.Builder builder, DebeziumOffsetStorageProperties properties) {
        DebeziumOffsetStorageProperties.File file = properties.getFile();
        
        builder.with("offset.storage", "org.apache.kafka.connect.storage.FileOffsetBackingStore");
        
        /*
         * 批量设置参数
         */
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        map.from(file::getFileName).whenHasText().to(value -> builder.with("offset.storage.file.filename", value));
        map.from(file::getFlushIntervalMs).to(value -> builder.with("offset.flush.interval.ms", value));
        map.from(file::getFlushTimeoutMs).to(value -> builder.with("offset.flush.timeout.ms", value));
    }
}



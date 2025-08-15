package io.debezium.embedded.spring.boot.history;

import io.debezium.config.Configuration;
import org.springframework.boot.context.properties.PropertyMapper;

/**
 * S3 数据库历史记录配置器。
 */
public class S3DatabaseHistoryConfigurer implements DatabaseHistoryConfigurer {
    
    @Override
    public void apply(Configuration.Builder builder, DebeziumDatabaseHistoryProperties properties) {
        DebeziumDatabaseHistoryProperties.S3 s3 = properties.getS3();
        
        builder.with("database.history", "io.debezium.relational.history.S3DatabaseHistory");
        
        /*
         * 批量设置参数
         */
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        
        // 必需的 S3 配置
        map.from(s3::getBucketName).whenHasText().to(value -> builder.with("database.history.s3.bucket.name", value));
        map.from(s3::getRegion).whenHasText().to(value -> builder.with("database.history.s3.region", value));
        
        // 认证配置
        map.from(s3::getAccessKeyId).whenHasText().to(value -> builder.with("database.history.s3.access.key.id", value));
        map.from(s3::getSecretAccessKey).whenHasText().to(value -> builder.with("database.history.s3.secret.access.key", value));
        
        // 可选配置
        map.from(s3::getKeyPrefix).whenHasText().to(value -> builder.with("database.history.s3.key.prefix", value));
        map.from(s3::getEndpointUrl).whenHasText().to(value -> builder.with("database.history.s3.endpoint.url", value));
        map.from(s3::getConnectionTimeout).to(value -> builder.with("database.history.s3.connection.timeout.ms", value));
        map.from(s3::getReadTimeout).to(value -> builder.with("database.history.s3.read.timeout.ms", value));
        
        // 额外的 S3 配置属性
        map.from(s3::getMaxRetries).to(value -> builder.with("database.history.s3.max.retries", value));
        map.from(s3::getRetryDelayMs).to(value -> builder.with("database.history.s3.retry.delay.ms", value));
        map.from(s3::getPathStyleAccessEnabled).to(value -> builder.with("database.history.s3.path.style.access.enabled", value));
        map.from(s3::getSigningRegion).whenHasText().to(value -> builder.with("database.history.s3.signing.region", value));
        map.from(s3::getProxyHost).whenHasText().to(value -> builder.with("database.history.s3.proxy.host", value));
        map.from(s3::getProxyPort).to(value -> builder.with("database.history.s3.proxy.port", value));
        map.from(s3::getProxyUsername).whenHasText().to(value -> builder.with("database.history.s3.proxy.username", value));
        map.from(s3::getProxyPassword).whenHasText().to(value -> builder.with("database.history.s3.proxy.password", value));
    }
}

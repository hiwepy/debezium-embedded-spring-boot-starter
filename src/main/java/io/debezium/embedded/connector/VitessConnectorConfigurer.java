package io.debezium.embedded.connector;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumConnectorProperties;
import org.springframework.boot.context.properties.PropertyMapper;

/**
 * Vitess 连接器配置器。
 */
public class VitessConnectorConfigurer implements ConnectorConfigurer {
    @Override
    public void apply(Configuration.Builder builder, DebeziumConnectorProperties properties) {
        builder
                .with("connector.class", "io.debezium.connector.vitess.VitessConnector")
                .with("database.server.name", properties.getServerName());

        /*
         * 批量设置参数
         */
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        
        // Vitess 特定配置
        builder.with("vitess.hosts", properties.getHost() + ":" + properties.getPort());
        map.from(properties::getUsername).whenHasText().to(value -> builder.with("vitess.user", value));
        map.from(properties::getPassword).whenHasText().to(value -> builder.with("vitess.password", value));

        // 数据库和表过滤
        map.from(properties::getDatabaseIncludeList).whenHasText().to(value -> builder.with("keyspace.include.list", value));
        map.from(properties::getTableIncludeList).whenHasText().to(value -> builder.with("table.include.list", value));
    }
}

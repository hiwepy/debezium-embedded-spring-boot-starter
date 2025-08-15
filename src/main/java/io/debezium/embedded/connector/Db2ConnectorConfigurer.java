package io.debezium.embedded.connector;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumConnectorProperties;
import org.springframework.boot.context.properties.PropertyMapper;

/**
 * DB2 连接器配置器。
 */
public class Db2ConnectorConfigurer implements ConnectorConfigurer {
    @Override
    public void apply(Configuration.Builder builder, DebeziumConnectorProperties properties) {
        builder
                .with("connector.class", "io.debezium.connector.db2.Db2Connector")
                .with("database.hostname", properties.getHost())
                .with("database.port", properties.getPort())
                .with("database.user", properties.getUsername())
                .with("database.password", properties.getPassword())
                .with("database.server.name", properties.getServerName());

        /*
         * 批量设置参数
         */
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        
        // 数据库和表过滤
        map.from(properties::getDatabaseIncludeList).whenHasText().to(value -> builder.with("database.include.list", value));
        map.from(properties::getTableIncludeList).whenHasText().to(value -> builder.with("table.include.list", value));
        map.from(properties::getSchemaIncludeList).whenHasText().to(value -> builder.with("schema.include.list", value));
    }
}

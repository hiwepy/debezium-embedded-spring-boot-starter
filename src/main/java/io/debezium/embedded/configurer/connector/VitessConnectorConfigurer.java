package io.debezium.embedded.configurer.connector;

import io.debezium.config.Configuration;
import io.debezium.connector.vitess.VitessConnector;
import io.debezium.embedded.spring.boot.DebeziumConnectorProperties;
import org.springframework.boot.context.properties.PropertyMapper;

/**
 * Vitess 连接器配置器。
 */
public class VitessConnectorConfigurer  extends AbstractConnectorConfigurer {

    @Override
    public String getConnectorClass() {
        return VitessConnector.class.getName();
    }

    @Override
    public void apply(PropertyMapper map, Configuration.Builder builder, DebeziumConnectorProperties properties) {

        // Vitess 特定配置
        map.from(properties::getHost).whenHasText().to(host -> 
            map.from(properties::getPort).whenNonNull().to(port -> 
                builder.with("vitess.hosts", host + ":" + port)
            )
        );
        map.from(properties::getUsername).whenHasText().to(value -> builder.with("vitess.user", value));
        map.from(properties::getPassword).whenHasText().to(value -> builder.with("vitess.password", value));

        // 数据库和表过滤
        map.from(properties::getDatabaseIncludeList).whenHasText().to(value -> builder.with("keyspace.include.list", value));
        map.from(properties::getTableIncludeList).whenHasText().to(value -> builder.with("table.include.list", value));
    }
}

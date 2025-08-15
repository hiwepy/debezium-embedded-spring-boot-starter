package io.debezium.embedded.spring.boot.connector;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumEmbeddedProperties;

/**
 * 自定义连接器配置器。
 */
public class CustomConnectorConfigurer implements ConnectorConfigurer {
    @Override
    public void apply(Configuration.Builder builder, DebeziumEmbeddedProperties properties) {
        if (properties.getCustom() != null && properties.getCustom().getConnectorClass() != null) {
            builder.with("connector.class", properties.getCustom().getConnectorClass());
            
            // 添加自定义配置属性
            if (properties.getCustom().getProps() != null) {
                properties.getCustom().getProps().forEach(builder::with);
            }
        }
    }
}

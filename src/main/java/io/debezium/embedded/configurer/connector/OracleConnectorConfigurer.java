package io.debezium.embedded.configurer.connector;

import io.debezium.config.Configuration;
import io.debezium.connector.oracle.OracleConnector;
import io.debezium.embedded.spring.boot.DebeziumConnectorProperties;
import org.springframework.boot.context.properties.PropertyMapper;

/**
 * Oracle 连接器配置器。
 */
public class OracleConnectorConfigurer extends AbstractConnectorConfigurer {

    @Override
    public String getConnectorClass() {
        return OracleConnector.class.getName();
    }

    @Override
    public void apply(PropertyMapper map, Configuration.Builder builder, DebeziumConnectorProperties properties) {

        // Oracle 特定配置
        if (properties.getOracle() != null) {
            DebeziumConnectorProperties.Oracle oracle = properties.getOracle();
            
            map.from(oracle::getDatabase).whenHasText().to(value -> builder.with("database.dbname", value));
            map.from(oracle::getPdbName).whenHasText().to(value -> builder.with("database.pdb.name", value));
            map.from(oracle::getSnapshotMode).whenHasText().to(value -> builder.with("snapshot.mode", value));
            map.from(oracle::getLogMiningStrategy).whenHasText().to(value -> builder.with("log.mining.strategy", value));

        }
    }
}

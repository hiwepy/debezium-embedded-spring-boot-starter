package io.debezium.embedded.configurer.connector;

import io.debezium.embedded.spring.boot.DebeziumConnectorProperties;

/**
 * 连接器配置器工厂。
 * 根据配置的连接器类型返回相应的配置器实现。
 */
public class ConnectorConfigurerFactory {

    /**
     * 根据配置的连接器类型返回相应的配置器实现。
     *
     * @param properties 连接器配置属性
     * @return 连接器配置器
     */
    public static ConnectorConfigurer from(DebeziumConnectorProperties properties) {
        return switch (properties.getType()) {
            case MYSQL -> new MySqlConnectorConfigurer();
            case MARIADB -> new MariaDbConnectorConfigurer();
            case POSTGRESQL -> new PostgreSqlConnectorConfigurer();
            case MONGODB -> new MongoDbConnectorConfigurer();
            case ORACLE -> new OracleConnectorConfigurer();
            case SQLSERVER -> new SqlServerConnectorConfigurer();
            case DB2 -> new Db2ConnectorConfigurer();
            case CASSANDRA -> new CassandraConnectorConfigurer();
            case VITESS -> new VitessConnectorConfigurer();
            case SPANNER -> new SpannerConnectorConfigurer();
            case INFORMIX -> new InformixConnectorConfigurer();
            case CUSTOM -> new CustomConnectorConfigurer();
        };
    }
}

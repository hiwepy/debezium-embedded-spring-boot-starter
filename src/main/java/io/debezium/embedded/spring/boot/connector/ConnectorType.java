package io.debezium.embedded.spring.boot.connector;

/**
 * 数据库连接器类型。
 * 支持主流数据库的 Debezium 连接器。
 */
public enum ConnectorType {
    MYSQL,
    MARIADB,
    MONGODB,
    POSTGRESQL,
    ORACLE,
    SQLSERVER,
    DB2,
    CASSANDRA,
    VITESS,
    SPANNER,
    INFORMIX,
    CUSTOM
}

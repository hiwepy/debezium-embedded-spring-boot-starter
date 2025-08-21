package io.debezium.embedded.configurer.connector;

/**
 * 数据库连接器类型。
 * 支持主流数据库的 Debezium 连接器。
 */
public enum ConnectorType {
    /**
     * MySQL 连接器
     */
    MYSQL,
    /**
     * MongoDB 连接器
     */
    MONGODB,
    /**
     * Oracle 连接器
     */
    ORACLE,
    /**
     * PostgreSQL 连接器
     */
    POSTGRESQL,
    /**
     * SqlServer 连接器
     */
    SQLSERVER,
    /**
     * DB2 连接器
     */
    DB2,
    /**
     * Vitess 连接器
     */
    VITESS,
    /**
     * 自定义连接器
     */
    CUSTOM
}

package io.debezium.embedded.spring.boot;

import io.debezium.embedded.configurer.connector.ConnectorType;
import lombok.Data;

/**
 * Debezium Connector 属性
 */
@Data
public class DebeziumConnectorProperties {

    /**
     * 连接器类型
     */
    private ConnectorType type = ConnectorType.MYSQL;

    /**
     * 连接器名称
     */
    private String destination;

    /**
     * 数据库主机
     */
    private String host;

    /**
     * 数据库端口
     */
    private Integer port;

    /**
     * 数据库用户名
     */
    private String username;

    /**
     * 数据库密码
     */
    private String password;

    /**
     * 数据库名称（PostgreSQL 等使用）
     */
    private String databaseName;

    /**
     * 服务器 ID（MySQL 使用）
     */
    private String serverId;

    /**
     * 服务器名称
     */
    private String serverName;

    /**
     * 包含的 Schema 列表（PostgreSQL 等使用）
     */
    private String schemaIncludeList;

    /**
     * 排除的 Schema 列表
     */
    private String schemaExcludeList;

    /**
     * 数据库白名单
     */
    private String databaseWhitelist;

    /**
     * 数据库黑名单
     */
    private String databaseBlacklist;

    /**
     * 包含的数据库列表
     */
    private String databaseIncludeList;

    /**
     * 排除的数据库列表
     */
    private String databaseExcludeList;

    /**
     * 数据表白名单
     */
    private String tableWhitelist;

    /**
     * 数据表黑名单
     */
    private String tableBlacklist;

    /**
     * 包含的表列表
     */
    private String tableIncludeList;

    /**
     * 排除的表列表
     */
    private String tableExcludeList;

    /**
     * MySQL 特定配置
     */
    private MySql mySql = new MySql();

    /**
     * PostgreSQL 特定配置
     */
    private PostgreSql postgreSql = new PostgreSql();

    /**
     * MongoDB 特定配置
     */
    private MongoDb mongoDb = new MongoDb();

    /**
     * Oracle 特定配置
     */
    private Oracle oracle = new Oracle();

    /**
     * SQL Server 特定配置
     */
    private SqlServer sqlServer = new SqlServer();

    /**
     * 自定义连接器配置
     */
    private Custom custom = new Custom();
    public enum SnapshotNewTables {
        parallel, off ;
    }
    @Data
    public static class MySql {


    }

    @Data
    public static class PostgreSql {

    }

    @Data
    public static class MongoDb {

    }

    @Data
    public static class Oracle {
        /**
         * 数据库名称
         */
        private String database;
        /**
         * PDB 名称
         */
        private String pdbName;
        /**
         * 快照模式
         */
        private String snapshotMode = "initial";
        /**
         * 日志挖掘策略
         */
        private String logMiningStrategy = "online_catalog";
    }

    @Data
    public static class SqlServer {
        /**
         * 数据库名称
         */
        private String database;
        /**
         * 快照模式
         */
        private String snapshotMode = "initial";
        /**
         * 快照隔离级别
         */
        private String snapshotIsolationMode = "snapshot";
    }

    @Data
    public static class Custom {
        /**
         * 自定义连接器类名
         */
        private String connectorClass;
        /**
         * 自定义配置属性
         */
        private java.util.Map<String, String> props = new java.util.HashMap<>();
    }
}

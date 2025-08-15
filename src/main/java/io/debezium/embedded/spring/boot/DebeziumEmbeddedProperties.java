package io.debezium.embedded.spring.boot;

import io.debezium.embedded.spring.boot.connector.ConnectorType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Debezium Embedded 配置属性
 */
@ConfigurationProperties(DebeziumEmbeddedProperties.PREFIX)
@Data
public class DebeziumEmbeddedProperties {

    public static final String PREFIX = "debezium.embedded";

    /**
     * 是否启用 Embedded 模式
     */
    private Boolean enabled = false;

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
     * 数据库历史文件名
     */
    private String historyFileName = "/tmp/dbhistory.dat";

    /**
     * 包含的数据库列表
     */
    private String databaseIncludeList;

    /**
     * 排除的数据库列表
     */
    private String databaseExcludeList;

    /**
     * 包含的表列表
     */
    private String tableIncludeList;

    /**
     * 排除的表列表
     */
    private String tableExcludeList;

    /**
     * 包含的 Schema 列表（PostgreSQL 等使用）
     */
    private String schemaIncludeList;

    /**
     * 排除的 Schema 列表
     */
    private String schemaExcludeList;

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

    @Data
    public static class MySql {
        /**
         * 快照模式
         */
        private String snapshotMode = "initial";
        /**
         * 快照锁定模式
         */
        private String snapshotLockingMode = "minimal";
        /**
         * 连接超时时间（毫秒）
         */
        private Integer connectTimeoutMs = 30000;
        /**
         * GTID 源过滤 DML 事件
         */
        private Boolean gtidSourceFilterDmlEvents = true;
    }

    @Data
    public static class PostgreSql {
        /**
         * 插件名称
         */
        private String pluginName = "pgoutput";
        /**
         * 复制槽名称
         */
        private String slotName;
        /**
         * 发布名称
         */
        private String publicationName;
        /**
         * 快照模式
         */
        private String snapshotMode = "initial";
    }

    @Data
    public static class MongoDb {
        /**
         * 连接字符串
         */
        private String connectionString;
        /**
         * 数据库列表
         */
        private String databaseList;
        /**
         * 集合列表
         */
        private String collectionList;
        /**
         * 快照模式
         */
        private String snapshotMode = "initial";
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

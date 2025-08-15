package io.debezium.embedded.spring.boot;

import io.debezium.config.CommonConnectorConfig;
import io.debezium.config.Configuration;
import io.debezium.engine.spi.OffsetCommitPolicy;
import io.debezium.relational.RelationalDatabaseConnectorConfig;
import lombok.Data;
import org.apache.kafka.connect.storage.FileOffsetBackingStore;
import org.apache.kafka.connect.storage.KafkaOffsetBackingStore;
import org.apache.kafka.connect.storage.OffsetBackingStore;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Objects;

/**
 * @see RelationalDatabaseConnectorConfig
 * @see CommonConnectorConfig
 */
@ConfigurationProperties(DebeziumEmbeddedProperties.PREFIX)
@Data
public class DebeziumEmbeddedProperties {

    public static final String PREFIX = "debezium.embedded";

    /**
     * A required field for an embedded connector that specifies the unique name for the connector instance.
     * 嵌入式连接器中的一个必填字段，用于指定该连接器实例的唯一名称。
     */
    private String destination;
    /**
     * A required field for an embedded connector that specifies the name of the normal Debezium connector's Java class.
     * 嵌入式连接器中的一个必填字段，用于指定Debezium连接器的Java类名称。如：io.debezium.connector.mysql.MySqlConnector
     */
    public String connectorClass;
    /**
     * An optional field that specifies the name of the class that implements the {@link OffsetBackingStore} interface,
     * and that will be used to store offsets recorded by the connector.
     * 这是一个可选字段，指定实现 {@link OffsetBackingStore} 接口的类的名称，该类将用于存储由连接器记录的偏移量。
     */
    private Class<? extends OffsetBackingStore> offsetStorageClass;
    /**
     * An optional field that specifies the file location for the {@link FileOffsetBackingStore}.
     * 这是一个可选字段，指定 {@link FileOffsetBackingStore} 的文件位置，默认 /tmp/offsets.dat。
     * 如果路径配置不正确可能导致无法存储偏移量 可能会导致重复消费变更
     * 如果连接器重新启动，它将使用最后记录的偏移量来知道它应该恢复读取源信息中的哪个位置。
     */
    private String offsetStorageFileName;
    /**
     * An optional field that specifies the topic name for the {@link KafkaOffsetBackingStore}.
     * 这个字段允许您指定一个自定义的偏移存储主题名称，而不是使用默认的偏移存储主题名称。
     */
    private String offsetStorageTopic;
    /**
     * An optional field that specifies the number of partitions for the {@link KafkaOffsetBackingStore}.
     * 这个字段允许您指定一个自定义的偏移存储主题的分区数，而不是使用默认的偏移存储主题的分区数。
     */
    private Integer offsetStoragePartitions;
    /**
     * An optional field that specifies the replication factor for the {@link KafkaOffsetBackingStore}.
     * 这个字段允许您指定一个自定义的偏移存储主题的副本数，而不是使用默认的偏移存储主题的副本数。
     */
    private Integer offsetStorageReplicationFactor;
    /**
     * An optional advanced field that specifies the maximum amount of time that the embedded connector should wait
     * for an offset commit to complete.
     * 这是一个可选高级字段，指定偏移提交完成所需的最大时间。
     */
    private Integer offsetFlushIntervalMs;
    /**
     * An optional advanced field that specifies the maximum amount of time that the embedded connector should wait
     * for an offset commit to complete.
     */
    private Integer offsetFlushTimeoutMs;
    /**
     * The fully-qualified class name of the commit policy type.
     * This class must implement the interface "io.debezium.engine.spi.OffsetCommitPolicy".
     * The default is a periodic commit policy based upon time intervals.
     * 这类必须实现接口 "io.debezium.engine.spi.OffsetCommitPolicy"
     */
    private Class<? extends OffsetCommitPolicy> offsetCommitPolicyClass;
    /**
     * Optional list of predicates that can be assigned to transformations.
     * The predicates are defined using '<predicate.prefix>.type' config option and configured using options '<predicate.prefix>.<option>'
     */
    private String predicates;
    /**
     * Optional list of single message transformations applied on the messages.
     * The transforms are defined using '<transform.prefix>.type' config option and configured using options '<transform.prefix>.<option>'
     */
    private String transforms;
    /**
     * Initial delay (in ms) for retries when encountering connection errors.
     * This value will be doubled upon every retry but won't exceed 'errors.retry.delay.max.ms'
     */
    private Integer errorsRetryDelayInitialMs = 5000;
    /**
     * Max delay (in ms) between retries when encountering connection errors.
     */
    private Integer errorsRetryDelayMaxMs = 10000;
    /**
     * How long we wait before forcefully stopping the connector thread when shutting down.
     * Must be bigger than the time it takes two polling loops to finish (executor.shutdown.timeout.ms)
     */
    private Integer shutdownPauseBeforeInterruptMs = 5000;
    /**
     * 数据库 host
     */
    private String host;
    /**
     * 数据库 Server 端口
     */
    private Integer port;
    /**
     * 数据库 Server 账号
     */
    private String username;
    /**
     * 数据库 Server 密码
     */
    private String password;
    private String schemaIncludeList;
    private String schemaExcludeList;
    private String databaseIncludeList;
    private String databaseExcludeList;
    private String tableIncludeList;
    private String tableExcludeList;

    public Configuration toConfiguration() {
        return Configuration.create()
                // 连接器的Java类名称
                .with("connector.class", this.connectorClass)
                // 偏移量持久化，用来容错 默认值
                .with("offset.storage",  Objects.nonNull(this.offsetStorageClass) ? this.offsetStorageClass.getName() : "org.apache.kafka.connect.storage.FileOffsetBackingStore")
                // 偏移量持久化文件路径 默认/tmp/offsets.dat  如果路径配置不正确可能导致无法存储偏移量 可能会导致重复消费变更
                // 如果连接器重新启动，它将使用最后记录的偏移量来知道它应该恢复读取源信息中的哪个位置。
                .with("offset.storage.file.filename", this.offsetStorageFileName)
                // 捕获偏移量的周期
                .with("offset.flush.interval.ms", this.offsetFlushIntervalMs)
                //               连接器的唯一名称
                .with("name", this.destination)
                //                数据库的hostname
                .with("database.hostname", this.host)
                //                端口
                .with("database.port", this.port)
                //                用户名
                .with("database.user", this.username)
                //                密码
                .with("database.password", this.password)

                //                 包含的数据库列表
                .with( RelationalDatabaseConnectorConfig.COLUMN_INCLUDE_LIST, includeDb)
                //                是否包含数据库表结构层面的变更，建议使用默认值true
                .with("include.schema.changes", "false")
                //                mysql.cnf 配置的 server-id
                .with("database.server.id", serverId)
                //                 MySQL 服务器或集群的逻辑名称
                .with("database.server.name", logicName)
                //                历史变更记录
                .with("database.history", "io.debezium.relational.history.FileDatabaseHistory")
                //                历史变更记录存储位置，存储DDL
                .with("database.history.file.filename", historyFileName)
                .build();
    }

}

package io.debezium.embedded.spring.boot;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(DebeziumSimpleProperties.PREFIX)
@Getter
@Setter
@ToString
public class DebeziumSimpleProperties {

    public static final int DEFAULT_PORT = 11111;
    private static final int DEFAULT_MAX_RETRIES = 3;
    private static final int DEFAULT_MAX_SLEEP_MS = Integer.MAX_VALUE;
    public static final String PREFIX = "debezium.simple";

    /**
     * 配置信息
     */
    private List<DebeziumSimpleProperties.Engine> instances = new ArrayList<>();

    @Data
    public static class Engine extends DebeziumEmbeddedProperties {
/*
        Properties props = new Properties();
                props.setProperty("name", dbd.getName());
                props.setProperty("database.dbType", dbd.getDbType());
                props.setProperty("connector.class", "io.debezium.connector.mysql.MySqlConnector");
                props.setProperty("offset.storage", "com.tianyin.edu.datasync.debezium.config.MyFileOffsetBackingStore");
                props.setProperty("offset.storage.file.filename", dbd.getOffsetPath());
                props.setProperty("offset.flush.interval.ms", "600000");
                props.setProperty("database.hostname", dbd.getHost());
                props.setProperty("database.port", dbd.getPort());
                props.setProperty("database.user", dbd.getUsername());
                props.setProperty("database.password", dbd.getPassword());
                props.setProperty("database.server.id", dbd.getServerId());
                props.setProperty("database.server.name", "my_mysql_connector" + dbd.getName());
                props.setProperty("database.history",
                        "io.debezium.relational.history.FileDatabaseHistory");
                props.setProperty("database.history.file.filename", dbd.getHistoryPath());
        String tableList = dbd.getTable().stream().map(item -> item.indexOf("&") > -1 ? item.substring(0, item.indexOf("&")) : item).collect(Collectors.joining(","));
                props.setProperty("table.include.list", tableList);

                // 连接器的Java类名称
                .with("connector.class", MySqlConnector.class.getName())
                // 偏移量持久化，用来容错 默认值
                .with("offset.storage", "org.apache.kafka.connect.storage.FileOffsetBackingStore")
                // 偏移量持久化文件路径 默认/tmp/offsets.dat  如果路径配置不正确可能导致无法存储偏移量 可能会导致重复消费变更
                // 如果连接器重新启动，它将使用最后记录的偏移量来知道它应该恢复读取源信息中的哪个位置。
                .with("offset.storage.file.filename", offsetFileName)
                // 捕获偏移量的周期
                .with("offset.flush.interval.ms", offsetTime)
                //               连接器的唯一名称
                .with("name", instanceName)
                //                数据库的hostname
                .with("database.hostname", ip)
                //                端口
                .with("database.port", port)
                //                用户名
                .with("database.user", username)
                //                密码
                .with("database.password", password)
                //                 包含的数据库列表
                .with("database.include.list", includeDb)
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

                */


    }

}

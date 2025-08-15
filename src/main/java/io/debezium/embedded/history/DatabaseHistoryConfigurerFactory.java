package io.debezium.embedded.history;

/**
 * 数据库历史记录配置器工厂。
 */
public class DatabaseHistoryConfigurerFactory {
    
    /**
     * 根据历史记录类型创建对应的配置器。
     *
     * @param type 历史记录类型
     * @return 历史记录配置器
     */
    public static DatabaseHistoryConfigurer from(DatabaseHistoryType type) {
        return switch (type) {
            case FILE -> new FileDatabaseHistoryConfigurer();
            case KAFKA -> new KafkaDatabaseHistoryConfigurer();
            case JDBC -> new JdbcDatabaseHistoryConfigurer();
            case REDIS -> new RedisDatabaseHistoryConfigurer();
            case S3 -> new S3DatabaseHistoryConfigurer();
            case CUSTOM -> new CustomDatabaseHistoryConfigurer();
        };
    }
}

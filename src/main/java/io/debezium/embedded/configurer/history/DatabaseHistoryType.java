package io.debezium.embedded.configurer.history;

/**
 * 数据库历史记录类型枚举。
 */
public enum DatabaseHistoryType {

    /**
     * 内存历史记录
     */
    MEMORY,

    /**
     * 文件历史记录
     */
    FILE,
    
    /**
     * Kafka 历史记录
     */
    KAFKA,

    /**
     * 自定义历史记录
     */
    CUSTOM
}

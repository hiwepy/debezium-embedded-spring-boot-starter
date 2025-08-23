package io.debezium.embedded.model;


import io.debezium.data.Envelope;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * debezium 消息模型
 */
@Setter
@Getter
@NoArgsConstructor
public class DebeziumModel {

    /**
     * 消息id
     */
    private String id;
    /**
     * 库名
     */
    private String destination;
    /**
     * 数据库类型
     */
    private String dbType;
    /**
     * 库名
     */
    private String database;
    /**
     * 表名
     */
    private String table;
    /**
     * 偏移量
     */
    private Long offset;
    /**
     * 事件类型
     */
    private Envelope.Operation operation;
    /**
     * 之前数据
     */
    private String beforeData;
    /**
     * 现数据
     */
    private String afterData;

    private List<Column> beforeColumns;
    private List<Column> afterColumns;

    /**
     * binlog changeTime
     */
    private Long changeTime;
    /**
     * dml build timeStamp
     */
    private Long createTime;

    @Setter
    @Getter
    @NoArgsConstructor
    public static class Column{
        private String name;
        private Object value;
        private String type;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DebeziumModel{");
        sb.append("id=").append(id);
        sb.append(", database='").append(database).append('\'');
        sb.append(", table='").append(table).append('\'');
        sb.append(", operation='").append(operation).append('\'');
        sb.append(", changeTime=").append(changeTime);
        sb.append(", createTime=").append(createTime);
        sb.append('}');
        return sb.toString();
    }

}

package io.debezium.embedded.protocol;

import io.debezium.data.Envelope;
import lombok.Data;
import lombok.Getter;

public class DebeziumEntry {


    /**
     * Protobuf enum {@code io.debezium.protocol.EntryType}
     *
     * <pre>
     **打散后的事件类型，主要用于标识事务的开始，变更数据，结束*
     * </pre>
     */
    public enum EntryType  {
        /**
         * <code>TRANSACTIONBEGIN = 1;</code>
         */
        TRANSACTIONBEGIN(0, 1),
        /**
         * <code>ROWDATA = 2;</code>
         */
        ROWDATA(1, 2),
        /**
         * <code>TRANSACTIONEND = 3;</code>
         */
        TRANSACTIONEND(2, 3),
        /**
         * <code>HEARTBEAT = 4;</code>
         *
         * <pre>
         ** 心跳类型，内部使用，外部暂不可见，可忽略 *
         * </pre>
         */
        HEARTBEAT(3, 4),
        /**
         * <code>GTIDLOG = 5;</code>
         */
        GTIDLOG(4, 5),
        ;

        /**
         * <code>TRANSACTIONBEGIN = 1;</code>
         */
        public static final int TRANSACTIONBEGIN_VALUE = 1;
        /**
         * <code>ROWDATA = 2;</code>
         */
        public static final int ROWDATA_VALUE = 2;
        /**
         * <code>TRANSACTIONEND = 3;</code>
         */
        public static final int TRANSACTIONEND_VALUE = 3;
        /**
         * <code>HEARTBEAT = 4;</code>
         *
         * <pre>
         ** 心跳类型，内部使用，外部暂不可见，可忽略 *
         * </pre>
         */
        public static final int HEARTBEAT_VALUE = 4;
        /**
         * <code>GTIDLOG = 5;</code>
         */
        public static final int GTIDLOG_VALUE = 5;


        public final int getNumber() { return value; }

        public static EntryType valueOf(int value) {
            return switch (value) {
                case 1 -> TRANSACTIONBEGIN;
                case 2 -> ROWDATA;
                case 3 -> TRANSACTIONEND;
                case 4 -> HEARTBEAT;
                case 5 -> GTIDLOG;
                default -> null;
            };
        }


        private static final EntryType[] VALUES = values();

        @Getter
        private final int index;
        @Getter
        private final int value;

        EntryType(int index, int value) {
            this.index = index;
            this.value = value;
        }

    }

    /**
     **/
    public enum EventTypeEnum {
        /**
         * 增
         */
        CREATE(1),
        /**
         * 删
         */
        UPDATE(2),
        /**
         * 改
         */
        DELETE(3),
        ;
        @Getter
        private final int type;


        EventTypeEnum(int type) {
            this.type = type;
        }
    }

    /**
     * <pre>
     ** 事件类型 *
     * </pre>
     */
    public enum EventType {

        /**
         * 创建了新的记录
         */
        CREATE(1, Envelope.Operation.CREATE),
        /**
         * 更新了现有记录
         */
        UPDATE(2, Envelope.Operation.UPDATE),
        /**
         * 将现有记录移除或删除
         */
        DELETE(3, Envelope.Operation.DELETE),
        /**
         * 对现有表执行截断操作（清空表数据）的操作
         */
        TRUNCATE(4, Envelope.Operation.TRUNCATE),

        ;

        public static EventType valueOf(int value) {
            return switch (value) {
                case 1 -> CREATE;
                case 2 -> UPDATE;
                case 3 -> DELETE;
                case 4 -> TRUNCATE;
                default -> null;
            };
        }

        private static final EventType[] VALUES = values();

        @Getter
        private final int index;
        @Getter
        private final Envelope.Operation operation;

        EventType(int index, Envelope.Operation operation) {
            this.index = index;
            this.operation = operation;
        }

    }

    @Data
    public final class Column  {

        /**
         * 字段下标
         */
        int index;

        /**
         * 字段java中类型
         */
        int sqlType;

        /**
         * 字段名称(忽略大小写)，在mysql中是没有的
         */
        String name;

        /**
         * 是否是主键
         */
        boolean isKey;

        /**
         * 如果EventType=UPDATE,用于标识这个字段值是否有修改
         */
        Boolean updated;

        /**
         * 标识是否为空
         */
        boolean isNull;

        /**
         * 字段值,timestamp,Datetime是一个时间格式的文本
         */
        String value;

        /**
         * 对应数据对象原始长度
         */
        int length;

        /**
         * 字段mysql类型
         */
        String mysqlType;

    }

    @Data
    public static class Pair{
        String key;
        Object value;
    }

}

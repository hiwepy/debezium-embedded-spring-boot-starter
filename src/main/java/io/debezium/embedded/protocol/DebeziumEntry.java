package io.debezium.embedded.protocol;

import io.debezium.data.Envelope;
import lombok.Data;
import lombok.Getter;

public class DebeziumEntry {

    @Data
    public static class RowChange {

        Envelope.Operation operation;
        String before;
        String after;
        String change;
        String schema;
        String table;
        String destination;
        Long changeTime;
        Long createTime;

    }


    public static class RowData {
        public String key;
    }

    public static class Column {
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


}

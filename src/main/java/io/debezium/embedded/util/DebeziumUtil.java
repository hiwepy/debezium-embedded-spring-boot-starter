package io.debezium.embedded.util;

import com.alibaba.fastjson2.JSON;
import io.debezium.data.Envelope;
import io.debezium.embedded.handler.RowEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Struct;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

@Slf4j
public class DebeziumUtil {

    public static final class FieldName {
        public static final String KEY_ID = "id";
        public static final String DATABASE = "db";
        public static final String TABLE = "table";
        public static final String OFFSET = "pos";
        public static final String PAYLOAD = "payload";
    }

    public enum TableFieldName {

        /**
         * 数据库实例
         */
        db,
        /**
         * 表名
         */
        table,
        /**
         * 操作时间
         */
        ts_ms
        ;

        public static Boolean filterJsonField(String fieldName) {
            return Stream.of(values()).map(Enum::name).collect(Collectors.toSet()).contains(fieldName);
        }
    }

    public static Map<String, Object> getChangeTableInfo(Struct sourceRecordChangeValue) {
        Struct struct = (Struct) sourceRecordChangeValue.get(Envelope.FieldName.SOURCE);
        Map<String, Object> map = struct.schema().fields().stream()
                .map(Field::name)
                .filter(fieldName -> struct.get(fieldName) != null && TableFieldName.filterJsonField(fieldName))
                .map(fieldName -> Pair.of(fieldName, struct.get(fieldName)))
                .collect(toMap(Pair::getKey, Pair::getValue));
        if (map.containsKey(Envelope.FieldName.TIMESTAMP)) {
            map.put("changeTime", map.get(Envelope.FieldName.TIMESTAMP));
            map.remove(Envelope.FieldName.TIMESTAMP);
        }
        return map;
    }

    public static void setChangeDataInfo(RowEvent rowEvent, Struct sourceRecordChangeValue) {
        // 操作类型过滤,只处理增删改
        String op = sourceRecordChangeValue.getString(Envelope.FieldName.OPERATION);
        Envelope.Operation operation = Envelope.Operation.forCode(op);
        rowEvent.setOperation(operation);
        if (operation != Envelope.Operation.READ) {
            if (operation == Envelope.Operation.CREATE) {
                Map<String, Object> afterMap = getChangeDataMap(sourceRecordChangeValue, Envelope.FieldName.AFTER);
                rowEvent.setAfterColumns(afterMap.keySet().stream().map(key -> new RowEvent.Column(key, afterMap.get(key))).collect(Collectors.toList()));
                rowEvent.setAfterData(JSON.toJSONString(afterMap));
            }
            // 修改需要特殊处理，拿到前后的数据
            if (operation == Envelope.Operation.UPDATE) {
                Map<String, Object> afterMap = getChangeDataMap(sourceRecordChangeValue, Envelope.FieldName.AFTER);
                Map<String, Object> beforeMap = getChangeDataMap(sourceRecordChangeValue, Envelope.FieldName.BEFORE);
                rowEvent.setAfterData(JSON.toJSONString(afterMap));
                rowEvent.setAfterColumns(afterMap.keySet().stream().map(key -> new RowEvent.Column(key, afterMap.get(key))).collect(Collectors.toList()));
                rowEvent.setBeforeData(JSON.toJSONString(beforeMap));
                rowEvent.setBeforeColumns(beforeMap.keySet().stream().map(key -> new RowEvent.Column(key, beforeMap.get(key))).collect(Collectors.toList()));
            }
            if (operation == Envelope.Operation.DELETE) {
                Map<String, Object> beforeMap = getChangeDataMap(sourceRecordChangeValue, Envelope.FieldName.BEFORE);
                rowEvent.setBeforeData(JSON.toJSONString(beforeMap));
                rowEvent.setBeforeColumns(beforeMap.keySet().stream().map(key -> new RowEvent.Column(key, beforeMap.get(key))).collect(Collectors.toList()));
            }
        }
    }

    public static String getChangeData(Struct sourceRecordChangeValue, String record) {
        Map<String, Object> changeDataMap = getChangeDataMap(sourceRecordChangeValue, record);
        if (CollectionUtils.isEmpty(changeDataMap)) {
            return null;
        }
        return JSON.toJSONString(changeDataMap);
    }

    public static Map<String, Object> getChangeDataMap(Struct sourceRecordChangeValue, String record) {
        Struct struct = (Struct) sourceRecordChangeValue.get(record);
        // 将变更的行封装为Map
        return struct.schema().fields().stream()
                .map(Field::name)
                .filter(fieldName -> struct.get(fieldName) != null)
                .map(fieldName -> Pair.of(fieldName, struct.get(fieldName)))
                .collect(toMap(Pair::getKey, Pair::getValue));
    }

}

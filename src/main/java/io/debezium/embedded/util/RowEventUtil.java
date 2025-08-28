package io.debezium.embedded.util;

import io.debezium.embedded.handler.RowEvent;

import java.util.List;
import java.util.Objects;

public class RowEventUtil {

    public static String getBeforeValue(RowEvent rowEvent, String columnName) {
        if(Objects.isNull(rowEvent)){
            return null;
        }
        List<RowEvent.Column> beforeColumnsList = rowEvent.getBeforeColumns();
        if(Objects.isNull(beforeColumnsList)){
            return null;
        }
        for (RowEvent.Column column : beforeColumnsList) {
            if (column.getName().equalsIgnoreCase(columnName)) {
                return Objects.toString(column.getValue(), null);
            }
        }
        return null;
    }

    public static String getAfterValue(RowEvent rowEvent, String columnName) {
        if(Objects.isNull(rowEvent)){
            return null;
        }
        List<RowEvent.Column> afterColumnsList = rowEvent.getAfterColumns();
        if(Objects.isNull(afterColumnsList)){
            return null;
        }
        for (RowEvent.Column column : afterColumnsList) {
            if (column.getName().equalsIgnoreCase(columnName)) {
                return  Objects.toString(column.getValue(), null);
            }
        }
        return null;
    }

    public static String getValue(RowEvent rowEvent, String columnName) {
        String value = getBeforeValue(rowEvent, columnName);
        if(Objects.isNull(value)){
            return getAfterValue(rowEvent, columnName);
        }
        return value;
    }

}

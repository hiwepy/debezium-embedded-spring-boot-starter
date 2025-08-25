package io.debezium.embedded.enums;

import lombok.Getter;

import java.util.StringJoiner;

@Getter
public enum TableNameEnum {

    ALL("*", "*", "*");

    public static final CharSequence DELIMITER = ".";

    final String destination;
    final String schema;
    final String table;

    TableNameEnum(String destination, String schema, String table) {
        this.destination = destination;
        this.schema = schema;
        this.table = table;
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(".").add(schema).add(table);
        return joiner.toString();
    }

}

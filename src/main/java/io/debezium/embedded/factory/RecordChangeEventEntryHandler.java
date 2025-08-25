package io.debezium.embedded.factory;

/**
 * 处理 Entry
 * @param <R> Entry
 */
public interface RecordChangeEventEntryHandler<R> {

    default void insert(R t) {

    }


    default void update(R before, R after) {

    }


    default void delete(R t) {

    }

    default void truncate(R t) {

    }
}

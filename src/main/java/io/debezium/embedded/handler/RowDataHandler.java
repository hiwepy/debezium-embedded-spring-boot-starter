package io.debezium.embedded.handler;

import io.debezium.data.Envelope;
import io.debezium.embedded.factory.RecordChangeEventEntryHandler;
import io.debezium.embedded.model.DebeziumModel;

/**
 * 处理行数据
 */
public interface RowDataHandler {

    <R> void handlerRowData(DebeziumModel rowModel, RecordChangeEventEntryHandler<R> entryHandler, Envelope.Operation operation) throws Exception;

}

package io.debezium.embedded.handler;

import io.debezium.data.Envelope;

/**
 * 处理行数据
 */
public interface RowEventHandler {

    /**
     * 处理行事件
     * 
     * @param rowEvent 行事件
     * @param entryHandler 行事件处理器
     * @param operation 操作类型
     * @throws Exception 异常
     */
    <R> void handleRowEvent(RowEvent rowEvent, RowEntryHandler<R> entryHandler, Envelope.Operation operation) throws Exception;

}
package io.debezium.embedded.handler.impl;


import io.debezium.embedded.handler.AbstractChangeEventHandler;
import io.debezium.embedded.handler.EntryHandler;
import io.debezium.embedded.handler.RowDataHandler;
import io.debezium.embedded.protocol.DebeziumEntry;
import io.debezium.engine.ChangeEvent;
import io.debezium.protocol.Message;

import java.util.List;

/**
 * 同步处理 Message
 */
public class SyncChangeEventHandlerImplAbstract extends AbstractChangeEventHandler<ChangeEvent<String, String>> {


    public SyncChangeEventHandlerImplAbstract(List<? extends EntryHandler> entryHandlers,
                                              RowDataHandler<ChangeEvent<String, String>> rowDataHandler) {
        super(null, entryHandlers, rowDataHandler);
    }

    public SyncChangeEventHandlerImplAbstract(List<DebeziumEntry.EntryType> subscribeTypes,
                                              List<? extends EntryHandler> entryHandlers,
                                              RowDataHandler<DebeziumEntry.RowData> rowDataHandler) {
        super(subscribeTypes, entryHandlers, rowDataHandler);
    }

    @Override
    public void handleMessage(String destination, Message message) {
        super.handleMessage(destination, message);
    }


}

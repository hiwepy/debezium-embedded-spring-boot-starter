package io.debezium.embedded.handler.impl;


import io.debezium.embedded.handler.AbstractRecordChangeEventHandler;
import io.debezium.embedded.handler.EntryHandler;
import io.debezium.embedded.handler.RowDataHandler;
import io.debezium.embedded.protocol.DebeziumEntry;
import io.debezium.protocol.FlatMessage;

import java.util.List;
import java.util.Map;

/**
 * 同步处理 FlatMessage
 */
public class SyncRecordChangeEventHandlerImpl extends AbstractRecordChangeEventHandler {

    public SyncRecordChangeEventHandlerImpl(List<? extends EntryHandler> entryHandlers,
                                            RowDataHandler<List<Map<String, String>>> rowDataHandler) {
        super(null, entryHandlers, rowDataHandler);
    }

    public SyncRecordChangeEventHandlerImpl(List<DebeziumEntry.EntryType> subscribeTypes,
                                            List<? extends EntryHandler> entryHandlers,
                                            RowDataHandler<List<Map<String, String>>> rowDataHandler) {
        super(subscribeTypes, entryHandlers, rowDataHandler);
    }

    @Override
    public void handleMessage(String destination, FlatMessage flatMessage) {
        super.handleMessage(destination, flatMessage);
    }
}

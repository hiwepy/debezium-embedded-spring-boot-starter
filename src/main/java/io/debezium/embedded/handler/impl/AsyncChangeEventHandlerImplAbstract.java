package io.debezium.embedded.handler.impl;


import io.debezium.embedded.handler.AbstractChangeEventHandler;
import io.debezium.embedded.handler.EntryHandler;
import io.debezium.embedded.handler.RowDataHandler;
import io.debezium.embedded.protocol.DebeziumEntry;
import io.debezium.protocol.Message;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;

/**
 *
 */
public class AsyncChangeEventHandlerImplAbstract extends AbstractChangeEventHandler {

    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    public AsyncChangeEventHandlerImplAbstract(List<? extends EntryHandler> entryHandlers,
                                               RowDataHandler<DebeziumEntry.RowData> rowDataHandler,
                                               ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        super(null, entryHandlers, rowDataHandler);
        this.threadPoolTaskExecutor = threadPoolTaskExecutor;
    }

    public AsyncChangeEventHandlerImplAbstract(List<DebeziumEntry.EntryType> subscribeTypes,
                                               List<? extends EntryHandler> entryHandlers,
                                               RowDataHandler<DebeziumEntry.RowData> rowDataHandler,
                                               ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        super(subscribeTypes, entryHandlers, rowDataHandler);
        this.threadPoolTaskExecutor = threadPoolTaskExecutor;
    }

    @Override
    public void handleMessage(String destination, Message message) {
        threadPoolTaskExecutor.execute(() -> super.handleMessage(destination, message));
    }

}

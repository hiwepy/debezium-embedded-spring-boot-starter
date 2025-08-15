package io.debezium.embedded.client;

import io.debezium.config.Configuration;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.RecordChangeEvent;
import org.apache.kafka.connect.source.SourceRecord;

import java.util.List;

/**
 * Simple 模式 Debezium 客户端
 */
public class SimpleDebeziumClient extends AbstractDebeziumClient {

    private SimpleDebeziumClient(List<Configuration> configurations) {
        super(configurations);
    }

    @Override
    public void handle(boolean success, String message, Throwable error) {

    }

    public static final class Builder extends AbstractClientBuilder<SimpleDebeziumClient, RecordChangeEvent<SourceRecord>> {

        @Override
        public SimpleDebeziumClient build(List<DebeziumEngine<RecordChangeEvent<SourceRecord>>> connectors) {
            SimpleDebeziumClient debeziumClient = new SimpleDebeziumClient(connectors);
            debeziumClient.setBatchSize(batchSize);
            debeziumClient.setFilter(filter);
            debeziumClient.setMessageHandler(messageHandler);
            debeziumClient.setTimeout(timeout);
            debeziumClient.setUnit(unit);
            debeziumClient.setSubscribeTypes(subscribeTypes);
            return debeziumClient;
        }
    }


}

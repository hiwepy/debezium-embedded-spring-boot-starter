package io.debezium.spring.boot;

import io.debezium.data.Envelope;
import io.debezium.embedded.annotation.DebeziumEventHandler;
import io.debezium.embedded.annotation.event.OnDeleteEvent;
import io.debezium.embedded.annotation.event.OnInsertEvent;
import io.debezium.embedded.annotation.event.OnTruncateTableEvent;
import io.debezium.embedded.annotation.event.OnUpdateEvent;
import io.debezium.embedded.model.DebeziumModel;
import lombok.extern.slf4j.Slf4j;

@DebeziumEventHandler
@Slf4j
public class DebeziumMessageEventHandler {

    @OnTruncateTableEvent(schema = "my_auth", table = "user_info")
    public void onTruncateTableEvent(DebeziumModel model) {
        log.info("onTruncateTableEvent");
    }

    @OnInsertEvent(schema = "my_auth", table = "user_info")
    public void onEventInsertData(DebeziumModel model) {

        // 1，获取当前事件的操作类型
        Envelope.Operation eventType = model.getOperation();
        // 2,获取数据集
        System.out.println("Table:" + model.getTable() +
                ",EventType:" + eventType +
                ",Before:" + model.getAfterData() +
                ",After:" + model.getAfterData());

    }

    @OnUpdateEvent(schema = "my_auth", table = "user_info")
    public void onEventUpdateData(DebeziumModel model) {
        log.info("onEventUpdateData");
    }

    @OnDeleteEvent(schema = "my_auth", table = "user_info")
    public void onEventDeleteData(DebeziumModel model) {
        log.info("onEventDeleteData");
    }

}

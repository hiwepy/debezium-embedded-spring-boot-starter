package io.debezium.embedded.client;

import io.debezium.embedded.handler.ChangeEventHandler;
import io.debezium.embedded.handler.RecordChangeEventHandler;
import io.debezium.embedded.protocol.DebeziumEntry;
import io.debezium.engine.DebeziumEngine;
import lombok.experimental.Accessors;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Accessors(chain = true)
public abstract class AbstractClientBuilder<D extends DebeziumClient, E> {

    /**
     * 指定订阅的事件类型，主要用于标识事务的开始，变更数据，结束
     */
    protected Set<DebeziumEntry.EntryType> subscribeTypes = Collections.singleton(DebeziumEntry.EntryType.ROWDATA);
    /**
     * 消息处理器
     */
    protected ChangeEventHandler changeEventHandler;
    protected RecordChangeEventHandler recordChangeEventHandler;

    public AbstractClientBuilder<D, E> setSubscribeTypes(Set<DebeziumEntry.EntryType> subscribeTypes) {
        this.subscribeTypes = subscribeTypes;
        return this;
    }

    public AbstractClientBuilder<D, E> changeEventHandler(ChangeEventHandler changeEventHandler) {
        this.changeEventHandler = changeEventHandler;
        return this;
    }

    public AbstractClientBuilder<D, E> recordChangeEventHandler(RecordChangeEventHandler recordChangeEventHandler) {
        this.recordChangeEventHandler = recordChangeEventHandler;
        return this;
    }

    public abstract D build(List<DebeziumEngine<E>> debeziumEngines);

}

package io.debezium.embedded.client;

import io.debezium.embedded.handler.ChangeEventHandler;
import io.debezium.embedded.handler.RecordChangeEventHandler;
import io.debezium.embedded.protocol.DebeziumEntry;
import io.debezium.engine.DebeziumEngine;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Accessors(chain = true)
public abstract class AbstractClientBuilder<D extends DebeziumClient, E> {

    /**
     * 消息过滤
     */
    protected String filter = StringUtils.EMPTY;
    /**
     * 批处理大小
     */
    protected Integer batchSize = 1;
    /**
     * 获取数据超时时间
     */
    protected Long timeout = 1L;
    /**
     * 获取数据超时时间单位
     */
    protected TimeUnit unit = TimeUnit.SECONDS;
    /**
     * 指定订阅的事件类型，主要用于标识事务的开始，变更数据，结束
     */
    protected List<DebeziumEntry.EntryType> subscribeTypes = Arrays.asList(DebeziumEntry.EntryType.ROWDATA);
    /**
     * 消息处理器
     */
    protected ChangeEventHandler changeEventHandler;
    protected RecordChangeEventHandler recordChangeEventHandler;

    public AbstractClientBuilder<D, E> filter(String filter) {
        this.filter = filter;
        return this;
    }

    public AbstractClientBuilder<D, E> batchSize(Integer batchSize) {
        this.batchSize = batchSize;
        return this;
    }

    public AbstractClientBuilder<D, E> timeout(Long timeout) {
        this.timeout = timeout;
        return this;
    }

    public AbstractClientBuilder<D, E> unit(TimeUnit unit) {
        this.unit = unit;
        return this;
    }

    public AbstractClientBuilder<D, E> setSubscribeTypes(List<DebeziumEntry.EntryType> subscribeTypes) {
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

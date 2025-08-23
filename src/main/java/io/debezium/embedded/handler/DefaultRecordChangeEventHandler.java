package io.debezium.embedded.handler;

import com.alibaba.fastjson2.JSON;
import io.debezium.data.Envelope;
import io.debezium.embedded.annotation.DebeziumEventHandler;
import io.debezium.embedded.annotation.DebeziumEventHolder;
import io.debezium.embedded.annotation.OnDebeziumEvent;
import io.debezium.embedded.model.DebeziumModel;
import io.debezium.embedded.util.DebeziumUtil;
import io.debezium.embedded.util.GenericUtil;
import io.debezium.embedded.util.HandlerUtil;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.RecordChangeEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.runtime.ConnectorConfig;
import org.apache.kafka.connect.source.SourceRecord;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Slf4j
public class DefaultRecordChangeEventHandler implements RecordChangeEventHandler, ApplicationContextAware {

    protected static final String DATABASE_DB_TYPE = "database.dbType";

    /**
     * 通过注解方式的表数据变更处理器
     */
    private Map<String, List<DebeziumEventHolder>> tableEventHolderMap;
    /**
     * 表数据变更处理器
     */
    private final Map<String, RecordChangeEventEntryHandler<?>> tableHandlerMap;
    /**
     * 行数据处理器
     */
    private final RowDataHandler rowDataHandler;

    public DefaultRecordChangeEventHandler(List<RecordChangeEventEntryHandler<?>> entryHandlers, RowDataHandler rowDataHandler) {
        this.tableHandlerMap = HandlerUtil.getTableHandlerMap(entryHandlers);
        this.rowDataHandler = rowDataHandler;
    }

    @Override
    public void handleEvent(List<RecordChangeEvent<SourceRecord>> recordChangeEvents,
                            DebeziumEngine.RecordCommitter<RecordChangeEvent<SourceRecord>> recordCommitter,
                            Properties props) {
        if(CollectionUtils.isEmpty(recordChangeEvents)){
            log.warn("Not found record change events, skip");
            return;
        }
        log.info("Received {} record change events", recordChangeEvents.size());
        for (RecordChangeEvent<SourceRecord> event: recordChangeEvents) {
            SourceRecord sourceRecord = event.record();
            // 等同 DefaultChangeEventHandler#handleEvent 的 jsonValue
            Struct sourceRecordChangeValue = (Struct) sourceRecord.value();
            if (Objects.isNull(sourceRecordChangeValue)) {
                log.warn("Source record value is null, skip");
                continue;
            }
            Struct source = (Struct) sourceRecordChangeValue.get(Envelope.FieldName.SOURCE);
            if (Objects.isNull(source)) {
                log.warn("未找到source字段, 跳过此记录的处理：{}", event);
                continue;
            }
            // 获取当前事件的操作类型
            String op = source.getString(Envelope.FieldName.OPERATION);
            Envelope.Operation operation = Envelope.Operation.forCode(op);
            if (operation != Envelope.Operation.READ) {
                log.warn("当前事件为{}，跳过此记录的处理：{}", operation, event);
                continue;
            }
            DebeziumModel rowModel = new DebeziumModel();
            rowModel.setId(sourceRecord.topic());
            rowModel.setOperation(operation);
            // 设置数据库名称和表名称
            String databaseName = source.getString(DebeziumUtil.FieldName.DATABASE);
            if (!StringUtils.hasText(databaseName)) {
                log.warn("未找到database字段, 跳过此记录的处理：{}", event);
                continue;
            }
            String tableName = source.getString(DebeziumUtil.FieldName.TABLE);
            if (!StringUtils.hasText(tableName)) {
                log.warn("未找到table字段, 跳过此记录的处理：{}", event);
                continue;
            }
            rowModel.setDatabase(databaseName);
            rowModel.setTable(tableName);
            // 设置偏移量
            Long offset = source.getInt64(DebeziumUtil.FieldName.OFFSET);
            if (Objects.nonNull(offset)) {
                rowModel.setOffset(offset);
            }
            // 设置变更时间
            Long timestamp = source.getInt64(Envelope.FieldName.TIMESTAMP);
            if (Objects.isNull(timestamp)) {
                timestamp = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
            }
            rowModel.setChangeTime(timestamp);
            // 设置数据库类型
            rowModel.setDbType(props.getProperty(DATABASE_DB_TYPE));
            DebeziumUtil.setChangeDataInfo(rowModel, sourceRecordChangeValue);

            try {
                // 获取表对应的注解处理器
                String destination = props.getProperty(ConnectorConfig.NAME_CONFIG);
                rowModel.setDestination(destination);
                // 获取表对应的注解处理器
                List<DebeziumEventHolder> eventHolders = HandlerUtil.getEventHolders(tableEventHolderMap, destination, databaseName, tableName, operation);
                if(!CollectionUtils.isEmpty(eventHolders)){
                    for (DebeziumEventHolder eventHolder : eventHolders) {
                        this.handlerRowData(rowModel, eventHolder);
                    }
                    continue;
                }
                // 获取表对应的处理器
                RecordChangeEventEntryHandler<?> entryHandler = HandlerUtil.getEntryHandler(tableHandlerMap, databaseName, tableName);
                // 判断是否有对应的处理器
                if(Objects.nonNull(entryHandler)){
                    this.handlerRowData(rowModel, entryHandler, operation);
                }
            } catch (Exception e) {
                throw new RuntimeException("parse event has an error , data:" + JSON.toJSONString(rowModel), e);
            }
        }
        try {
            recordCommitter.markBatchFinished();
        } catch (Exception e) {
            log.error("Commit batch has an error", e);
        }
    }

    protected void handlerRowData(DebeziumModel rowModel, DebeziumEventHolder eventHolder) throws Exception {
        try {
            Method method = eventHolder.getMethod();
            ReflectionUtils.makeAccessible(method);
            Object[] args = GenericUtil.getInvokeArgs(method, rowModel);
            method.invoke(eventHolder.getTarget(), args);
        } catch (Exception e) {
            log.error("handlerRowData error", e);
        }
    }

    protected void handlerRowData(DebeziumModel rowModel, RecordChangeEventEntryHandler<?> entryHandler, Envelope.Operation operation) throws Exception {
        try {
            // 逐行调用Handler处理
            rowDataHandler.handlerRowData(rowModel, entryHandler, operation);
        } catch (Exception e) {
            log.error("handlerRowData error", e);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        log.info("{}: annotation event handler is initializing....", Thread.currentThread().getName());
        // 获取所有的处理器
        Map<String, Object> eventHandlerMap = applicationContext.getBeansWithAnnotation(DebeziumEventHandler.class);
        if(CollectionUtils.isEmpty(eventHandlerMap)){
            log.info("{}: not found annotation event handler.", Thread.currentThread().getName());
            return;
        }
        // 注解处理器对象
        List<DebeziumEventHolder> eventHolders = new ArrayList<>();
        for (Object target : eventHandlerMap.values()) {
            // 获取对象声明的方法
            Method[] methods = ReflectionUtils.getDeclaredMethods(target.getClass());
            for (Method method : methods) {
                OnDebeziumEvent debeziumEvent = AnnotatedElementUtils.findMergedAnnotation(method, OnDebeziumEvent.class);
                if (Objects.nonNull(debeziumEvent)) {
                    eventHolders.add(new DebeziumEventHolder(target, method, debeziumEvent));
                }
            }
        }
        this.tableEventHolderMap = HandlerUtil.getEventHolderMap(eventHolders);
        log.info("{}: annotation event handler initialized finish.", Thread.currentThread().getName());
    }

}

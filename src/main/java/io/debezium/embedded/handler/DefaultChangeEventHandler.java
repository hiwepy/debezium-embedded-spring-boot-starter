package io.debezium.embedded.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import io.debezium.data.Envelope;
import io.debezium.embedded.annotation.DebeziumEventHandler;
import io.debezium.embedded.annotation.DebeziumEventHolder;
import io.debezium.embedded.annotation.OnDebeziumEvent;
import io.debezium.embedded.util.DebeziumUtil;
import io.debezium.embedded.util.GenericUtil;
import io.debezium.embedded.util.HandlerUtil;
import io.debezium.engine.ChangeEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.connect.runtime.ConnectorConfig;
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
import java.util.stream.Collectors;


@Slf4j
public class DefaultChangeEventHandler implements ChangeEventHandler, ApplicationContextAware {

    protected static final String DATABASE_DB_TYPE = "database.dbType";
    protected static final JSONObject JSON_OBJECT = new JSONObject();
    /**
     * 通过注解方式的表数据变更处理器
     */
    private Map<String, List<DebeziumEventHolder>> tableEventHolderMap;
    /**
     * 表数据变更处理器
     */
    private final Map<String, RowEntryHandler<?>> tableHandlerMap;
    /**
     * 行数据处理器
     */
    private final RowEventHandler rowEventHandler;

    public DefaultChangeEventHandler(List<RowEntryHandler<?>> entryHandlers, RowEventHandler rowEventHandler) {
        this.tableHandlerMap = HandlerUtil.getTableHandlerMap(entryHandlers);
        this.rowEventHandler = rowEventHandler;
    }

    @Override
    public void handleEvent(ChangeEvent<String, String> event, Properties props) {
        if (Objects.nonNull(event.value())) {
            try {
                // 解析JSON字符串
                JSONObject jsonKey = JSON.parseObject(event.key());
                JSONObject jsonValue = JSON.parseObject(event.value());
                if (Objects.isNull(jsonKey) || Objects.isNull(jsonValue)) {
                    log.error("数据格式错误, 跳过此记录的处理：{}", event);
                    return;
                }
                JSONObject jsonPayload = jsonValue.getJSONObject(DebeziumUtil.FieldName.PAYLOAD);
                if (Objects.nonNull(jsonPayload)) {
                    // 获取当前事件的操作类型
                    String op = jsonPayload.getString(Envelope.FieldName.OPERATION);
                    Envelope.Operation operation = Envelope.Operation.forCode(op);
                    if (operation != Envelope.Operation.READ) {
                        log.warn("当前事件为{}，跳过此记录的处理：{}", operation, event);
                        return;
                    }
                    JSONObject jsonKeyPayload = jsonKey.getJSONObject(DebeziumUtil.FieldName.PAYLOAD);
                    String id = Objects.nonNull(jsonKeyPayload) ? jsonKeyPayload.getString(DebeziumUtil.FieldName.KEY_ID) : "";
                    RowEvent rowEvent = new RowEvent();
                    rowEvent.setId(id);
                    rowEvent.setOperation(operation);
                    // 设置数据库名称和表名称
                    JSONObject source = jsonPayload.getJSONObject(Envelope.FieldName.SOURCE);
                    if (Objects.isNull(source)) {
                        log.error("未找到source字段, 跳过此记录的处理：{}", event);
                        return;
                    }
                    String databaseName = source.getString(DebeziumUtil.FieldName.DATABASE);
                    if (!StringUtils.hasText(databaseName)) {
                        log.error("未找到database字段, 跳过此记录的处理：{}", event);
                        return;
                    }
                    String tableName = source.getString(DebeziumUtil.FieldName.TABLE);
                    if (!StringUtils.hasText(tableName)) {
                        log.error("未找到table字段, 跳过此记录的处理：{}", event);
                        return;
                    }
                    rowEvent.setDatabase(databaseName);
                    rowEvent.setTable(tableName);
                    // 设置偏移量
                    Long offset = source.getLong(DebeziumUtil.FieldName.OFFSET);
                    if (Objects.nonNull(offset)) {
                        rowEvent.setOffset(offset);
                    }
                    // 设置变更时间
                    Long timestamp = source.getLongValue(Envelope.FieldName.TIMESTAMP, LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli());
                    rowEvent.setChangeTime(timestamp);
                    // 设置数据库类型
                    rowEvent.setDbType(props.getProperty(DATABASE_DB_TYPE));

                    // 设置变更前的数据
                    JSONObject beforeData = jsonPayload.getJSONObject(Envelope.FieldName.BEFORE);
                    if (Objects.nonNull(beforeData)) {
                        rowEvent.setBeforeData(beforeData.toJSONString());
                        rowEvent.setBeforeColumns(beforeData.keySet().stream().map(key -> new RowEvent.Column(key, beforeData.get(key))).collect(Collectors.toList()));
                    } else {
                        rowEvent.setBeforeData(JSON_OBJECT.toJSONString());
                        rowEvent.setBeforeColumns(Collections.emptyList());
                    }
                    // 设置变更后的数据
                    JSONObject afterData = jsonPayload.getJSONObject(Envelope.FieldName.AFTER);
                    if (Objects.nonNull(afterData)) {
                        rowEvent.setAfterData(afterData.toJSONString());
                        rowEvent.setBeforeColumns(afterData.keySet().stream().map(key -> new RowEvent.Column(key, afterData.get(key))).collect(Collectors.toList()));
                    } else {
                        rowEvent.setAfterData(JSON_OBJECT.toJSONString());
                        rowEvent.setAfterColumns(Collections.emptyList());
                    }
                    // 获取表对应的注解处理器
                    String destination = props.getProperty(ConnectorConfig.NAME_CONFIG);
                    rowEvent.setDestination(destination);
                    List<DebeziumEventHolder> eventHolders = HandlerUtil.getEventHolders(tableEventHolderMap, destination, databaseName, tableName, operation);
                    if(!CollectionUtils.isEmpty(eventHolders)){
                        for (DebeziumEventHolder eventHolder : eventHolders) {
                            rowEvent.setChangeTime(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli());
                            this.handleRowEvent(rowEvent, eventHolder);
                        }
                    }
                    // 获取表对应的处理器
                    RowEntryHandler<?> entryHandler = HandlerUtil.getEntryHandler(tableHandlerMap, databaseName, tableName);
                    // 判断是否有对应的处理器
                    if(Objects.nonNull(entryHandler)){
                        rowEvent.setChangeTime(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli());
                        this.handleRowEvent(rowEvent, entryHandler, operation);
                    }
                }
            } catch (Exception e) {
                log.error("解析变更事件失败: {}", e.getMessage());
                throw new RuntimeException("parse event has an error , data:" + JSON.toJSONString(event), e);
            }
        }
    }

    protected void handleRowEvent(RowEvent rowEvent, DebeziumEventHolder eventHolder) throws Exception {
        try {
            Method method = eventHolder.getMethod();
            ReflectionUtils.makeAccessible(method);
            Object[] args = GenericUtil.getInvokeArgs(method, rowEvent);
            method.invoke(eventHolder.getTarget(), args);
        } catch (Exception e) {
            log.error("handleRowEvent error", e);
        }
    }

    protected void handleRowEvent(RowEvent rowEvent, RowEntryHandler<?> entryHandler, Envelope.Operation operation) throws Exception {
        try {
            // 逐行调用Handler处理
            rowEventHandler.handleRowEvent(rowEvent, entryHandler, operation);
        } catch (Exception e) {
            log.error("handleRowEvent error", e);
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

package io.debezium.embedded.handler;

import com.alibaba.fastjson.JSON;
import io.debezium.data.Envelope;
import io.debezium.embedded.annotation.DebeziumEventHolder;
import io.debezium.embedded.context.DebeziumContext;
import io.debezium.embedded.model.DebeziumModel;
import io.debezium.embedded.protocol.DebeziumEntry;
import io.debezium.embedded.util.GenericUtil;
import io.debezium.embedded.util.HandlerUtil;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.RecordChangeEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.debezium.data.Envelope.FieldName.*;
import static io.debezium.data.Envelope.FieldName.AFTER;
import static io.debezium.data.Envelope.FieldName.BEFORE;
import static java.util.stream.Collectors.toMap;

@Slf4j
public abstract class AbstractRecordChangeEventHandler implements RecordChangeEventHandler, ApplicationContextAware {

    /**
     * 指定订阅的事件类型，主要用于标识事务的开始，变更数据，结束
     */
    private List<DebeziumEntry.EntryType> subscribeTypes = Arrays.asList(DebeziumEntry.EntryType.ROWDATA);
    /**
     * 通过注解方式的表数据变更处理器
     */
    private Map<String, List<DebeziumEventHolder>> tableEventHolderMap;
    /**
     * 表处理器
     */
    private Map<String, EntryHandler> tableHandlerMap;
    /**
     * 行数据处理器
     */
    private RowDataHandler<List<Map<String, String>>> rowDataHandler;

    public AbstractRecordChangeEventHandler(List<DebeziumEntry.EntryType> subscribeTypes,
                                            List<? extends EntryHandler> entryHandlers,
                                            RowDataHandler<List<Map<String, String>>> rowDataHandler) {
        if(Objects.nonNull(subscribeTypes)){
            this.subscribeTypes = subscribeTypes;
        }
        this.tableHandlerMap = HandlerUtil.getTableHandlerMap(entryHandlers);
        this.rowDataHandler = rowDataHandler;
    }

    @Override
    public void handleEvent(List<RecordChangeEvent<SourceRecord>> recordChangeEvents,
                            DebeziumEngine.RecordCommitter<RecordChangeEvent<SourceRecord>> recordCommitter,
                            Properties props) {
        // 判断是否有数据
        if(CollectionUtils.isEmpty(recordChangeEvents)){
            return;
        }
        for (RecordChangeEvent<SourceRecord> r : recordChangeEvents) {
            SourceRecord sourceRecord = r.record();
            Struct sourceRecordChangeValue = (Struct) sourceRecord.value();
            if (Objects.isNull(sourceRecordChangeValue)) {
                continue;
            }
            // 获取变更表数据
            Map<String, Object> changeMap = getChangeTableInfo(sourceRecordChangeValue);
            if (CollectionUtils.isEmpty(changeMap)) {
                continue;
            }
            DebeziumModel.ChangeListenerModel changeListenerModel = getChangeDataInfo(sourceRecordChangeValue, changeMap);
            if (changeListenerModel == null) {
                continue;
            }
            String jsonString = JSON.toJSONString(changeListenerModel);
            log.info("发送变更数据：{}", jsonString);
        }
        recordCommitter.markBatchFinished();


        // 遍历 Data，单条解析
        for (int i = 0; i < data.size(); i++) {
            // 获取数据库实例
            String schemaName = flatMessage.getDatabase();
            // 获取表名
            String tableName = flatMessage.getTable();
            // 获取类型
            DebeziumEntry.EventType eventType = DebeziumEntry.EventType.valueOf(flatMessage.getType());
            // 获取当前行数据
            List<Map<String, String>> maps;
            if (eventType.equals(DebeziumEntry.EventType.UPDATE)) {
                // 更新后的数据
                Map<String, String> map = data.get(i);
                // 更新前的数据
                Map<String, String> oldMap = flatMessage.getOld().get(i);
                // 合并新旧数据
                maps = Stream.of(map, oldMap).collect(Collectors.toList());
            } else {
                maps = Stream.of(data.get(i)).collect(Collectors.toList());
            }
            try {
                // 获取表对应的注解处理器
                List<DebeziumEventHolder> eventHolders = HandlerUtil.getEventHolders(tableEventHolderMap, destination, schemaName, tableName, eventType);
                if(!CollectionUtils.isEmpty(eventHolders)){
                    DebeziumModel model = DebeziumModel.builder()
                            .id(flatMessage.getId())
                            .schema(schemaName)
                            .table(tableName)
                            .eventType(eventType)
                            .executeTime(flatMessage.getEs())
                            .createTime(flatMessage.getTs()).build();
                    for (DebeziumEventHolder eventHolder : eventHolders) {
                        this.handlerRowData(model, maps, eventHolder, eventType);
                    }
                    continue;
                }
                // 获取表对应的处理器
                EntryHandler<?> entryHandler = HandlerUtil.getEntryHandler(tableHandlerMap, schemaName, tableName);
                // 判断是否有对应的处理器
                if(Objects.nonNull(entryHandler)){
                    DebeziumModel model = DebeziumModel.builder()
                            .id(flatMessage.getId())
                            .schema(schemaName)
                            .table(tableName)
                            .eventType(eventType)
                            .executeTime(flatMessage.getEs())
                            .createTime(flatMessage.getTs()).build();
                   this.handlerRowData(model, maps, entryHandler, eventType);
                }
            } catch (Exception e) {
                throw new RuntimeException("parse event has an error , data:" + maps.toString(), e);
            }
        }
    }



    private DebeziumModel.ChangeListenerModel getChangeDataInfo(Struct sourceRecordChangeValue, Map<String, Object> changeMap) {
        // 操作类型过滤,只处理增删改
        Envelope.Operation operation = Envelope.Operation.forCode((String) sourceRecordChangeValue.get(OPERATION));
        if (operation != Envelope.Operation.READ) {
            Integer eventType = null;
            Map<String, Object> result = new HashMap<>(4);
            if (operation == Envelope.Operation.CREATE) {
                eventType = DebeziumEntry.EventTypeEnum.CREATE.getType();
                result.put(DATA, getChangeData(sourceRecordChangeValue, AFTER));
                result.put(BEFORE_DATA, null);
            }
            // 修改需要特殊处理，拿到前后的数据
            if (operation == Envelope.Operation.UPDATE) {
                if (!changeMap.containsKey(TABLE)) {
                    return null;
                }
                eventType = DebeziumEntry.EventTypeEnum.UPDATE.getType();
                String currentTableName = String.valueOf(changeMap.get(TABLE).toString());
                // 忽略非重要属性变更
                Map<String, String> resultMap = filterChangeData(sourceRecordChangeValue, currentTableName);
                if (CollectionUtils.isEmpty(resultMap)) {
                    return null;
                }
                result.put(DATA, resultMap.get(AFTER));
                result.put(BEFORE_DATA, resultMap.get(BEFORE));
            }
            if (operation == Envelope.Operation.DELETE) {
                eventType = DebeziumEntry.EventTypeEnum.DELETE.getType();
                result.put(DATA, getChangeData(sourceRecordChangeValue, BEFORE));
                result.put(BEFORE_DATA, getChangeData(sourceRecordChangeValue, BEFORE));
            }
            result.put(EVENT_TYPE, eventType);
            result.putAll(changeMap);
            return BeanUtils.copyProperties(result, DebeziumModel.ChangeListenerModel.class);
        }
        return null;
    }


    /**
     * 过滤非重要变更数据
     *
     * @param sourceRecordChangeValue
     * @param currentTableName
     * @return
     */
    private Map<String, String> filterChangeData(Struct sourceRecordChangeValue, String currentTableName) {
        Map<String, String> resultMap = new HashMap<>(4);
        Map<String, Object> afterMap = getChangeDataMap(sourceRecordChangeValue, AFTER);
        Map<String, Object> beforeMap = getChangeDataMap(sourceRecordChangeValue, BEFORE);
        //todo 根据表过滤字段
        resultMap.put(AFTER, JSON.toJSONString(afterMap));
        resultMap.put(BEFORE, JSON.toJSONString(beforeMap));
        return resultMap;
    }


    /**
     * 校验是否仅仅是非重要字段属性变更
     * @param currentTableName
     * @param afterMap
     * @param beforeMap
     * @param filterColumnList
     * @return
     */
    private boolean checkNonEssentialData(String currentTableName, Map<String, Object> afterMap,
                                          Map<String, Object> beforeMap, List<String> filterColumnList) {
        Map<String, Boolean> filterMap = new HashMap<>(16);
        for (String key : afterMap.keySet()) {
            Object afterValue = afterMap.get(key);
            Object beforeValue = beforeMap.get(key);
            filterMap.put(key, !Objects.equals(beforeValue, afterValue));
        }
        filterColumnList.parallelStream().forEach(filterMap::remove);
        if (filterMap.values().stream().noneMatch(x -> x)) {
            log.info("表：{}无核心资料变更，忽略此次操作!", currentTableName);
            return true;
        }
        return false;
    }

    public String getChangeData(Struct sourceRecordChangeValue, String record) {
        Map<String, Object> changeDataMap = getChangeDataMap(sourceRecordChangeValue, record);
        if (CollectionUtils.isEmpty(changeDataMap)) {
            return null;
        }
        return JSON.toJSONString(changeDataMap);
    }


    public Map<String, Object> getChangeDataMap(Struct sourceRecordChangeValue, String record) {
        Struct struct = (Struct) sourceRecordChangeValue.get(record);
        // 将变更的行封装为Map
        Map<String, Object> changeData = struct.schema().fields().stream()
                .map(Field::name)
                .filter(fieldName -> struct.get(fieldName) != null)
                .map(fieldName -> Pair.of(fieldName, struct.get(fieldName)))
                .collect(toMap(Pair::getKey, Pair::getValue));
        if (CollectionUtils.isEmpty(changeData)) {
            return null;
        }
        return changeData;
    }


    private enum FilterJsonFieldEnum {
        /**
         * 表
         */
        table,
        /**
         * 库
         */
        db,
        /**
         * 操作时间
         */
        ts_ms,
        ;


        public static Boolean filterJsonField(String fieldName) {
            return Stream.of(values()).map(Enum::name).collect(Collectors.toSet()).contains(fieldName);
        }
    }
    private Map<String, Object> getChangeTableInfo(Struct sourceRecordChangeValue) {
        Struct struct = (Struct) sourceRecordChangeValue.get(SOURCE);
        Map<String, Object> map = struct.schema().fields().stream()
                .map(Field::name)
                .filter(fieldName -> struct.get(fieldName) != null && FilterJsonFieldEnum.filterJsonField(fieldName))
                .map(fieldName -> Pair.of(fieldName, struct.get(fieldName)))
                .collect(toMap(Pair::getKey, Pair::getValue));
        if (map.containsKey(FilterJsonFieldEnum.ts_ms.name())) {
            map.put("changeTime", map.get(FilterJsonFieldEnum.ts_ms.name()));
            map.remove(FilterJsonFieldEnum.ts_ms.name());
        }
        return map;
    }

    public void handlerRowData(DebeziumModel model, List<Map<String, String>> rowData, DebeziumEventHolder eventHolder, DebeziumEntry.EventType eventType) throws Exception {
        Method method = eventHolder.getMethod();
        try {
            DebeziumContext.setModel(model);
            ReflectionUtils.makeAccessible(method);
            Object[] args = GenericUtil.getInvokeArgs(method, model, rowData, eventType);
            method.invoke(eventHolder.getTarget(), args);
        } finally {
            // 移除上下文
            DebeziumContext.removeModel();
        }
    }

    public void handlerRowData(DebeziumModel model, List<Map<String, String>> rowData, EntryHandler entryHandler, DebeziumEntry.EventType eventType) throws Exception {
        try {
            // 设置上下文
            DebeziumContext.setModel(model);
            // 逐行调用Handler处理
            rowDataHandler.handlerRowData(rowData, entryHandler, eventType);
        } finally {
            // 移除上下文
            DebeziumContext.removeModel();
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

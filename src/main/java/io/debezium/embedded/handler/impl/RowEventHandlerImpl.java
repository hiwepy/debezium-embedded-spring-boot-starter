package io.debezium.embedded.handler.impl;


import com.alibaba.fastjson2.JSON;
import io.debezium.data.Envelope;
import io.debezium.embedded.factory.IModelFactory;
import io.debezium.embedded.handler.RowEntryHandler;
import io.debezium.embedded.handler.RowEvent;
import io.debezium.embedded.handler.RowEventHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;

/**
 * 将 Debezium 的 RowData 转换为 Map 类型，并调用 entryHandler 处理
 */
@Slf4j
public class RowEventHandlerImpl implements RowEventHandler {

    private final IModelFactory<Map<String,Object>> modelFactory;

    public RowEventHandlerImpl(IModelFactory<Map<String, Object>> modelFactory) {
        this.modelFactory = modelFactory;
    }

    @Override
    public <R> void handleRowEvent(RowEvent rowEvent, RowEntryHandler<R> entryHandler, Envelope.Operation operation) throws Exception{
        // 参数校验，如果参数为空，则直接返回
        if (Objects.isNull(rowEvent) || Objects.isNull(entryHandler) || Objects.isNull(operation)) {
            log.error("参数不能为空，rowEvent: {}, entryHandler: {}, operation: {}", Objects.isNull(rowEvent) , Objects.isNull(entryHandler), Objects.isNull(operation));
            return;
        }
        // 将 afterData 转换为 Map 类型
        Map<String, Object> afterData = JSON.parseObject(rowEvent.getAfterData());
        // 将 Map 类型数据转换为模型对象
        R after = modelFactory.newInstance(afterData, entryHandler);
        switch (operation) {
            case CREATE: {
                // 执行插入操作
                entryHandler.insert(after);
            }break;
            case UPDATE: {
                // 将 beforeData 转换为 Map 类型
                Map<String, Object> beforeData = JSON.parseObject(rowEvent.getBeforeData());
                // 将 Map 类型数据转换为模型对象
                R before = modelFactory.newInstance(beforeData, entryHandler);
                // 执行更新操作
                entryHandler.update(before, after);
            } break;
            case DELETE: {
                // 执行删除操作
                entryHandler.delete(after);
            }break;
            case TRUNCATE: {
                // 执行清空操作
                entryHandler.truncate(after);
            }break;
            default:
                break;
        }
    }
}

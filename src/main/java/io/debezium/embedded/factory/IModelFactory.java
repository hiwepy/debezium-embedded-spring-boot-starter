package io.debezium.embedded.factory;

import io.debezium.embedded.handler.RecordChangeEventEntryHandler;

import java.util.Set;

/**
 * 模型工厂接口
 * 
 * @param <T> 模型类型
 */
public interface IModelFactory<T> {

    /**
     * 创建模型实例
     *
     * @param input 输入对象
     * @param entryHandler 记录变更事件处理器
     * @return 模型实例
     * @throws Exception 异常
     */ 
    <R> R newInstance(T input, RecordChangeEventEntryHandler<R> entryHandler) throws Exception;

    /**
     * 创建模型实例
     *
     * @param input 输入对象
     * @param entryHandler 记录变更事件处理器
     * @param updatedColumns 更新列
     * @return 模型实例
     * @throws Exception 异常
     */
    default <R> R newInstance(T input, RecordChangeEventEntryHandler<R> entryHandler, Set<String> updatedColumns) throws Exception {
        return null;
    }
}

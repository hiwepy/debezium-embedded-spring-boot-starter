package io.debezium.embedded.factory;


import io.debezium.embedded.handler.RecordChangeEventEntryHandler;
import io.debezium.embedded.util.GenericUtil;

/**
 * 抽象模型工厂
 * 
 * @param <T> 模型类型
 */
public abstract class AbstractModelFactory<T> implements IModelFactory<T> {

    /**
     * 创建模型实例
     * 
     * @param entryHandler 记录变更事件处理器
     * @param input 输入对象
     * @return 模型实例
     * @throws Exception 异常
     */
    @Override
    public <R> R newInstance(T input, RecordChangeEventEntryHandler<R> entryHandler) throws Exception {
        Class<R> tableClass = GenericUtil.getTableClass(entryHandler);
        if (tableClass != null) {
            return newInstance(input, tableClass);
        }
        return null;
    }

    abstract <R> R newInstance(T input, Class<R> tableClass) throws Exception;
}

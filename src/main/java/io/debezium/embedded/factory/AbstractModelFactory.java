package io.debezium.embedded.factory;


import io.debezium.embedded.handler.RecordChangeEventEntryHandler;
import io.debezium.embedded.util.GenericUtil;

import java.util.Objects;

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
        // 1、获取泛型类型
        Class<R> genericType = GenericUtil.getGenericType(entryHandler);
        // 2、如果泛型类型为空，则抛出异常
        if (Objects.isNull(genericType)) {
            throw new RuntimeException("genericType not found form entryHandler : " + entryHandler.getClass());
        }
        // 3、创建模型实例
        return this.newInstance(input, genericType);
    }

    abstract <R> R newInstance(T input, Class<R> genericType) throws Exception;
}

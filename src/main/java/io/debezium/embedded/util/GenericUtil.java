package io.debezium.embedded.util;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import io.debezium.data.Envelope;
import io.debezium.embedded.handler.RecordChangeEventEntryHandler;
import io.debezium.embedded.model.DebeziumModel;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 泛型工具类
 */
public class GenericUtil {

    private static Map<Class<? extends RecordChangeEventEntryHandler>, Class> cache = new ConcurrentHashMap<>();

    public static Object[] getInvokeArgs(Method method, DebeziumModel rowModel) {
        return Arrays.stream(method.getParameterTypes()).map(pClass -> {
                    if(DebeziumModel.class.isAssignableFrom(pClass)){
                        return rowModel;
                    }
                    return null;
                })
                .toArray();
    }

    public static Object[] getInvokeArgs(Method method, DebeziumModel rowModel, List<Map<String, String>> rowData, Envelope.Operation operation) {
        return Arrays.stream(method.getParameterTypes()).map(pClass -> {
                if(DebeziumModel.class.isAssignableFrom(pClass)){
                    return rowModel;
                }
                if(List.class.isAssignableFrom(pClass)) {
                    return rowData;
                }
                if(Envelope.Operation.class.isAssignableFrom(pClass)) {
                    return operation;
                }
                return null;
            }).toArray();
    }

    public static String getTableGenericProperties(RecordChangeEventEntryHandler entryHandler) {
        Class<?> tableClass = getTableClass(entryHandler);
        if (tableClass != null) {
            // 3.2、获取 mybatis-plus 的注解信息
            TableInfo tableInfo = TableInfoHelper.getTableInfo(tableClass);
            if (Objects.nonNull(tableInfo)) {
                return tableInfo.getTableName();
            }
        }
        return null;
    }


    @SuppressWarnings("unchecked")
    public static <T> Class<T> getTableClass(RecordChangeEventEntryHandler<T> object) {
        // 1、获取处理器的泛型类型
        Class<? extends RecordChangeEventEntryHandler> handlerClass = object.getClass();
        Class tableClass = cache.get(handlerClass);
        if (tableClass == null) {
            Type[] interfacesTypes = handlerClass.getGenericInterfaces();
            for (Type t : interfacesTypes) {
                Class c = (Class) ((ParameterizedType) t).getRawType();
                if (c.equals(RecordChangeEventEntryHandler.class)) {
                    tableClass = (Class<T>) ((ParameterizedType) t).getActualTypeArguments()[0];
                    cache.putIfAbsent(handlerClass, tableClass);
                    return tableClass;
                }
            }
        }
        return tableClass;
    }


}

package io.debezium.embedded.util;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import io.debezium.embedded.factory.RecordChangeEventEntryHandler;
import io.debezium.embedded.model.DebeziumModel;
import org.springframework.core.ResolvableType;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 泛型工具类
 */
@SuppressWarnings("unchecked")
public class GenericUtil {

    private static final Map<Class<?>, Class<?>> CACHE = new ConcurrentHashMap<>();

    public static Object[] getInvokeArgs(Method method, DebeziumModel rowModel) {
        return Arrays.stream(method.getParameterTypes()).map(pClass -> {
                    if(DebeziumModel.class.isAssignableFrom(pClass)){
                        return rowModel;
                    }
                    return null;
                })
                .toArray();
    }

    public static String getTableName(RecordChangeEventEntryHandler<?> entryHandler) {
        Class<?> tableClass = getGenericType(entryHandler);
        if (tableClass != null) {
            // 3.2、获取 mybatis-plus 的注解信息
            TableInfo tableInfo = TableInfoHelper.getTableInfo(tableClass);
            if (Objects.nonNull(tableInfo)) {
                return tableInfo.getTableName();
            }
        }
        return null;
    }

    public static <T> Class<T> getGenericType(RecordChangeEventEntryHandler<?> entryHandler) {
        // 1、获取处理器的类型
        Class<?> handlerClass = entryHandler.getClass();
        // 2、从缓存中获取处理器的类型
        Class<?> tableClass = CACHE.get(handlerClass);
        if (Objects.isNull(tableClass)) {
            // 3、使用改进的方法获取泛型类型
            Class<?> genericType = getInterfaceGenericType(handlerClass, RecordChangeEventEntryHandler.class, 0);
            if (genericType != null) {
                CACHE.putIfAbsent(handlerClass, genericType);
                return (Class<T>) genericType;
            }
        }
        return (Class<T>) tableClass;
    }

    /**
     * 获取类的泛型类型
     */
    public static <T> Class<T> getGenericType(Class<T> clazz, int index) {
        try {
            ResolvableType resolvableType = ResolvableType.forClass(clazz);
            ResolvableType genericType = resolvableType.getGeneric(index);
            Class<?> genericClass = genericType.resolve();
            return genericClass == null ? null : (Class<T>) genericClass;
        } catch (Exception e) {
            throw new RuntimeException("获取类[" + clazz.getName() + "]泛型类型失败." , e);
        }
    }

    /**
     * 获取接口的泛型类型
     * 
     * @param clazz 实现类
     * @param interfaceClass 接口类
     * @param index 泛型参数索引
     * @return 泛型类型
     */
    public static <T> Class<T> getInterfaceGenericType(Class<?> clazz, Class<?> interfaceClass, int index) {
        try {
            // 获取所有实现的接口
            Type[] genericInterfaces = clazz.getGenericInterfaces();
            for (Type genericInterface : genericInterfaces) {
                if (genericInterface instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) genericInterface;
                    // 检查是否是目标接口
                    if (parameterizedType.getRawType().equals(interfaceClass)) {
                        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                        if (index >= 0 && index < actualTypeArguments.length) {
                            Type actualType = actualTypeArguments[index];
                            if (actualType instanceof Class) {
                                return (Class<T>) actualType;
                            } else if (actualType instanceof ParameterizedType) {
                                return (Class<T>) ((ParameterizedType) actualType).getRawType();
                            }
                        }
                    }
                }
            }
            
            // 如果没有找到，尝试从父类查找
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null && !superClass.equals(Object.class)) {
                return getInterfaceGenericType(superClass, interfaceClass, index);
            }
            
            return null;
        } catch (Exception e) {
            throw new RuntimeException("获取接口[" + interfaceClass.getName() + "]泛型类型失败.", e);
        }
    }

    /**
     * 获取字段的泛型类型
     */
    public static <T> Class<T> getFieldGenericType(Class<T> clazz, String fieldName, int index) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            ResolvableType resolvableType = ResolvableType.forField(field, clazz);
            ResolvableType genericType = resolvableType.getGeneric(index);
            Class<?> genericClass = genericType.resolve();
            return genericClass == null ? null : (Class<T>) genericClass;
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("字段不存在: " + fieldName, e);
        } catch (Exception e) {
            throw new RuntimeException("获取字段[" + fieldName + "]泛型类型失败.", e);
        }
    }

    /**
     * 获取方法参数的泛型类型
     */
    public static <T> Class<T> getMethodParameterGenericType(Method method, int parameterIndex, int genericIndex) {
        try {
            ResolvableType resolvableType = ResolvableType.forMethodParameter(method, parameterIndex);
            ResolvableType genericType = resolvableType.getGeneric(genericIndex);
            Class<?> genericClass = genericType.resolve();
            return genericClass == null ? null : (Class<T>) genericClass;
        } catch (Exception e) {
            throw new RuntimeException("获取方法[" + method.getName() + "]参数[" + parameterIndex + "]泛型类型失败." , e);
        }
    }

    /**
     * 获取方法返回值的泛型类型
     */
    public static <T> Class<T> getMethodReturnGenericType(Method method, int index) {
        try {
            ResolvableType resolvableType = ResolvableType.forMethodReturnType(method);
            ResolvableType genericType = resolvableType.getGeneric(index);
            Class<?> genericClass = genericType.resolve();
            return genericClass == null ? null : (Class<T>) genericClass;
        } catch (Exception e) {
            throw new RuntimeException("获取方法[" + method.getName() + "]返回值泛型类型失败." , e);
        }
    }
}

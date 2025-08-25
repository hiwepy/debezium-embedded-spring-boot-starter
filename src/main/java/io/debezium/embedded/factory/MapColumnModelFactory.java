package io.debezium.embedded.factory;

import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeanUtils;

import java.util.Map;

/**
 * Map列模型工厂
 * 支持将Map数据转换为自定义JavaBean对象或Map对象
 */
public class MapColumnModelFactory extends AbstractModelFactory<Map<String, Object>> {

    @Override
    @SuppressWarnings("unchecked")
    <R> R newInstance(Map<String, Object> inputMap, Class<R> targetClass) throws Exception {
        // 检查目标类型是否支持
        if (!isSupportedType(targetClass)) {
            throw new IllegalArgumentException("不支持的类型: " + targetClass.getName() + 
                "，仅支持自定义JavaBean对象和Map类型");
        }
        // 根据目标类型进行不同的处理
        if (Map.class.isAssignableFrom(targetClass)) {
            // 如果目标类型是Map，直接返回输入Map
            return (R) inputMap;
        } else {
            // 如果目标类型是自定义JavaBean，使用反射进行属性映射
            return createJavaBeanInstance(inputMap, targetClass);
        }
    }
    
    /**
     * 检查目标类型是否支持
     * 
     * @param targetClass 目标类型
     * @return 是否支持
     */
    private boolean isSupportedType(Class<?> targetClass) {
        // 支持Map类型
        if (Map.class.isAssignableFrom(targetClass)) {
            return true;
        }
        
        // 支持自定义JavaBean类型（非基本类型、非数组、非集合等）
        return !targetClass.isPrimitive() && 
               !targetClass.isArray() && 
               !targetClass.isEnum() &&
               !targetClass.isInterface() &&
               !isJavaLangClass(targetClass) &&
               !isJavaUtilClass(targetClass);
    }
    
    /**
     * 检查是否为Java.lang包下的类
     */
    private boolean isJavaLangClass(Class<?> clazz) {
        return clazz.getPackage() != null && 
               clazz.getPackage().getName().equals("java.lang");
    }
    
    /**
     * 检查是否为Java.util包下的类
     */
    private boolean isJavaUtilClass(Class<?> clazz) {
        return clazz.getPackage() != null && 
               clazz.getPackage().getName().equals("java.util");
    }
    
    /**
     * 创建JavaBean实例
     * 
     * @param inputMap 输入Map
     * @param targetClass 目标类型
     * @return JavaBean实例
     * @throws Exception 异常
     */
    private <R> R createJavaBeanInstance(Map<String, Object> inputMap, Class<R> targetClass) throws Exception {

        // 创建目标对象实例
        R object = BeanUtils.instantiateClass(targetClass);
        
        // 获取MyBatis-Plus的注解信息
        TableInfo tableInfo = TableInfoHelper.getTableInfo(targetClass);
        
        if (tableInfo != null && tableInfo.getFieldList() != null) {
            // 使用MyBatis-Plus的字段信息进行映射
            for (TableFieldInfo tableFieldInfo : tableInfo.getFieldList()) {
                // 获取实体对象属性映射字段对应的值
                Object value = MapUtils.getObject(inputMap, tableFieldInfo.getColumn());
                if (value != null) {
                    PropertyUtils.setProperty(object, tableFieldInfo.getProperty(), value);
                }
            }
        } else {
            // 如果没有MyBatis-Plus注解，使用简单的属性名映射
            for (Map.Entry<String, Object> entry : inputMap.entrySet()) {
                String propertyName = entry.getKey();
                Object value = entry.getValue();
                if (value != null && hasProperty(targetClass, propertyName)) {
                    PropertyUtils.setProperty(object, propertyName, value);
                }
            }
        }
        
        return object;
    }
    
    /**
     * 检查类是否有指定属性
     * 
     * @param clazz 类
     * @param propertyName 属性名
     * @return 是否有该属性
     */
    private boolean hasProperty(Class<?> clazz, String propertyName) {
        try {
            return BeanUtils.getPropertyDescriptor(clazz, propertyName) != null;
        } catch (Exception e) {
            return false;
        }
    }

}

package io.debezium.embedded.annotation;


import io.debezium.embedded.protocol.DebeziumEntry;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 监听 debezium 操作
 *
 * @author lujun
 */
public class DebeziumEventHolder {

    /**
     * 目标 bean
     */
    private Object target;
    /**
     * 监听的方法
     */
    private Method method;
    /**
     * 监听的事件
     */
    private OnDebeziumEvent event;

    /**
     * 构造方法，设置目标，方法以及注解类型
     * @param target Object 目标
     * @param method Method 方法
     * @param event OnDebeziumEvent 注解
     */
    public DebeziumEventHolder(Object target, Method method, OnDebeziumEvent event) {
        this.target = target;
        this.method = method;
        this.event = event;
    }

    /**
     * 返回目标类
     * @return Object
     */
    public Object getTarget() {
        return target;
    }

    /**
     * 返回方法
     * @return Method
     */
    public Method getMethod() {
        return method;
    }

    /**
     * 返回注解类型
     * @return OnDebeziumEvent
     */
    public OnDebeziumEvent getEvent() {
        return event;
    }

    public boolean isMatch(DebeziumEntry.EventType eventType) {
        return this.getEvent().eventType().length == 0 || Arrays.stream(this.getEvent().eventType()).anyMatch(ev -> ev == eventType) || eventType == null;
    }

}

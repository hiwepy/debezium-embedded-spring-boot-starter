package io.debezium.embedded.annotation.event;

import io.debezium.embedded.annotation.OnDebeziumEvent;
import io.debezium.embedded.protocol.DebeziumEntry;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 创建索引的操作
 *
 * @author lujun
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@OnDebeziumEvent(eventType = DebeziumEntry.EventType.CINDEX)
public @interface OnCreateIndexEvent {

    /**
     * debezium 指令
     * default for all
     * @return debezium destination
     */
    @AliasFor(annotation = OnDebeziumEvent.class)
    String destination() default "";

    /**
     * 数据库实例
     * @return 数据库实例
     */
    @AliasFor(annotation = OnDebeziumEvent.class)
    String schema();

    /**
     * 监听的表
     * default for all
     * @return 监听的表
     */
    @AliasFor(annotation = OnDebeziumEvent.class)
    String table();
}

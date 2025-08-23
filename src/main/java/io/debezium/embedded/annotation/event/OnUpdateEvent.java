package io.debezium.embedded.annotation.event;

import io.debezium.data.Envelope;
import io.debezium.embedded.annotation.OnDebeziumEvent;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 更新操作监听器
 * 发生update时会触发
 *
 * @author lujun
 */

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@OnDebeziumEvent(operations = Envelope.Operation.UPDATE)
public @interface OnUpdateEvent {

    /**
     * debezium 指令
     * default for all
     *
     * @return debezium destination
     */
    @AliasFor(annotation = OnDebeziumEvent.class)
    String destination() default "";

    /**
     * 数据库实例
     *
     * @return debezium destination
     */
    @AliasFor(annotation = OnDebeziumEvent.class)
    String schema();

    /**
     * 监听的表
     * default for all
     *
     * @return debezium destination
     */
    @AliasFor(annotation = OnDebeziumEvent.class)
    String table();

}

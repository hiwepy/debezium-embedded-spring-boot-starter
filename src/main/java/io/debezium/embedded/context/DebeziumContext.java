package io.debezium.embedded.context;

import io.debezium.embedded.model.DebeziumModel;
import com.alibaba.ttl.TransmittableThreadLocal;

/**
 * debezium上下文
 */
public class DebeziumContext {

    private static TransmittableThreadLocal<DebeziumModel> threadLocal = new TransmittableThreadLocal<>();

    public static DebeziumModel getModel(){
        return threadLocal.get();
    }


    public static void setModel(DebeziumModel debeziumModel){
        threadLocal.set(debeziumModel);
    }


    public  static void removeModel(){
        threadLocal.remove();
    }
}

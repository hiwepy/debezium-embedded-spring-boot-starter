package io.debezium.embedded.client;

import io.debezium.config.Configuration;
import io.debezium.embedded.handler.ChangeEventHandler;
import io.debezium.embedded.handler.RecordChangeEventHandler;
import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.RecordChangeEvent;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.connect.source.SourceRecord;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;

/**
 * Debezium Client 抽象类
 */
@Slf4j
public abstract class AbstractDebeziumClient<R> implements InitializingBean, DebeziumClient {

    /**
     * 是否运行中
     */
    protected volatile boolean running;
    /**
     * Debezium Engine
     */
    private final List<DebeziumEngine<ChangeEvent<String, String>>> changeEventEngines;
    private final List<DebeziumEngine<RecordChangeEvent<SourceRecord>>> recordChangeEventEngines;

    /**
     * 线程池
     */
    protected ThreadPoolTaskExecutor executor;

    public AbstractDebeziumClient(List<DebeziumEngine<ChangeEvent<String, String>>> changeEventEngines,
                                  List<DebeziumEngine<RecordChangeEvent<SourceRecord>>> recordChangeEventEngines,
                                  ThreadPoolTaskExecutor executor) {
        this.changeEventEngines = changeEventEngines;
        this.recordChangeEventEngines = recordChangeEventEngines;
        this.executor = executor;
    }

    @Override
    public void afterPropertiesSet() {

    }

    @Override
    public void start() {
        log.info("Start Debezium Client Of Instance： {}", this.getClass().getSimpleName());
        for (DebeziumEngine<ChangeEvent<String, String>> debeziumEngine : changeEventEngines) {
            executor.execute(debeziumEngine);
        }
        this.running = true;
    }

    @SneakyThrows
    @Override
    public void stop() {
        log.info("Stop Debezium Client Of Instance： {}", this.getClass().getSimpleName());
        this.running = false;
        for (DebeziumEngine<ChangeEvent<String, String>> debeziumEngine : changeEventEngines) {
            try {
                log.info("Stop Debezium Engine Of Instance： {}", this.getClass().getSimpleName());
                debeziumEngine.close();
                log.info("Stopped Debezium Engine Of Instance： {}", this.getClass().getSimpleName());
            } catch (Exception e) {
                log.error("Error stopping Debezium Engine Of Instance： {}", this.getClass().getSimpleName(), e);
            }
        }
        for (DebeziumEngine<RecordChangeEvent<SourceRecord>> debeziumEngine : recordChangeEventEngines) {
            try {
                log.info("Stop Debezium Engine Of Instance： {}", this.getClass().getSimpleName());
                debeziumEngine.close();
                log.info("Stopped Debezium Engine Of Instance： {}", this.getClass().getSimpleName());
            } catch (Exception e) {
                log.error("Error stopping Debezium Engine Of Instance： {}", this.getClass().getSimpleName(), e);
            }
        }
        Thread.sleep(2000);
        executor.shutdown();
    }

    @Override
    public boolean isRunning() {
        return this.running;
    }

}

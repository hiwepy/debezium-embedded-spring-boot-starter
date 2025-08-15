package io.debezium.embedded.client;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.debezium.config.Configuration;
import io.debezium.embedded.Connect;
import io.debezium.embedded.handler.ChangeEventHandler;
import io.debezium.embedded.handler.RecordChangeEventHandler;
import io.debezium.embedded.protocol.DebeziumEntry;
import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.RecordChangeEvent;
import io.debezium.engine.format.ChangeEventFormat;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.connect.source.SourceRecord;
import org.slf4j.MDC;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

/**
 * Debezium Client 抽象类
 */
@Slf4j
public abstract class AbstractDebeziumClient implements InitializingBean, DebeziumClient,
        DebeziumEngine.CompletionCallback,
        DebeziumEngine.ConnectorCallback {

    /**
     * 是否运行中
     */
    protected volatile boolean running;

    /**
     * Debezium Engine
     */
    private List<Configuration> configurations;
    private List<DebeziumEngine<?>> debeziumEngines;
    /**
     * 消息过滤
     */
    protected String filter = StringUtils.EMPTY;
    /**
     * 批处理大小
     */
    protected Integer batchSize = 1;
    /**
     * 获取数据超时时间，-1代表不做timeout控制
     */
    protected Long timeout = -1L;
    /**
     * 获取数据超时时间单位
     */
    protected TimeUnit unit = TimeUnit.SECONDS;
    /**
     * 指定订阅的事件类型，主要用于标识事务的开始，变更数据，结束
     */
    protected List<DebeziumEntry.EntryType> subscribeTypes = Arrays.asList(DebeziumEntry.EntryType.ROWDATA);
    /**
     * 消息处理器
     */
    private ChangeEventHandler changeEventHandler;
    private RecordChangeEventHandler recordChangeEventHandler;
    /**
     * 线程工厂
     */
    protected ThreadFactory threadFactory;
    /**
     * 线程池
     */
    protected ThreadPoolExecutor executor;

    public AbstractDebeziumClient(List<Configuration> configurations) {
        this.configurations = configurations;
    }

    @Override
    public void afterPropertiesSet() {
        Assert.notEmpty(configurations, "DebeziumEngine Configuration Empty!");
        threadFactory = new ThreadFactoryBuilder().setNameFormat(SQL_SERVER_LISTENER_POOL + "-%d").build();
        executor = new ThreadPoolExecutor(8, 16, 60,
                TimeUnit.SECONDS, new ArrayBlockingQueue<>(256),
                threadFactory, new ThreadPoolExecutor.DiscardPolicy());

        for (Configuration configuration : configurations) {
            DebeziumEngine.ConnectorCallback connectorCallback = this;
            DebeziumEngine.CompletionCallback completionCallback = this;
            DebeziumEngine<RecordChangeEvent<SourceRecord>> debeziumEngine = DebeziumEngine
                    .create(ChangeEventFormat.of(Connect.class))
                    .using(configuration.asProperties())
                    .using(completionCallback)
                    .using(connectorCallback)
                    .notifying((recordChangeEvents, recordCommitter) -> process(recordChangeEvents, recordCommitter))
                    .build();
            debeziumEngines.add(debeziumEngine);
        }
    }

    @Override
    public void start() {
        log.info("Start Debezium Client Of Instance： {}", this.getClass().getSimpleName());
        for (DebeziumEngine<R> debeziumEngine : debeziumEngines) {
            log.warn(ThreadPoolEnum.SQL_SERVER_LISTENER_POOL + "线程池开始执行 debeziumEngine 实时监听任务!");
            executor.execute(debeziumEngine);
        }
        this.running = true;
    }

    @SneakyThrows
    @Override
    public void stop() {
        log.info("Stop Debezium Client Of Instance： {}", this.getClass().getSimpleName());
        this.running = false;
        for (DebeziumEngine<R> debeziumEngine : debeziumEngines) {
            debeziumEngine.close();
        }
        Thread.sleep(2000);
        log.warn(ThreadPoolEnum.SQL_SERVER_LISTENER_POOL + "线程池关闭!");
        executor.shutdown();
    }

    @Override
    public boolean isRunning() {
        return this.running;
    }

    protected String getDestination(Configuration configuration, ChangeEvent<String, String> changeEvent){
        return changeEvent.destination();
    }

    protected String getDestination(Configuration configuration, RecordChangeEvent<SourceRecord> recordChangeEvent){
        return configuration.getString("destination");
    }


    @Override
    public void process(ChangeEvent<String, String> changeEvent) {

    }

    @Override
    public void process(List<RecordChangeEvent<SourceRecord>> recordChangeEvents,
                        DebeziumEngine.RecordCommitter<RecordChangeEvent<SourceRecord>> recordCommitter) {

    }



    public enum ThreadPoolEnum {

        /**
         * 实例
         */
        INSTANCE;

        public static final String SQL_SERVER_LISTENER_POOL = "sql-server-listener-pool";
        /**
         * 线程池单例
         */
        private final ExecutorService es;


        /**
         * 枚举 (构造器默认为私有）
         */
        ThreadPoolEnum() {
            final ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat(SQL_SERVER_LISTENER_POOL + "-%d").build();
            es = new ThreadPoolExecutor(8, 16, 60,
                    TimeUnit.SECONDS, new ArrayBlockingQueue<>(256),
                    threadFactory, new ThreadPoolExecutor.DiscardPolicy());
        }


        /**
         * 公有方法
         *
         * @return ExecutorService
         */
        public ExecutorService getInstance() {
            return es;
        }
    }

}

package io.debezium.embedded.spring.boot;

import io.debezium.config.Configuration;
import io.debezium.embedded.Connect;
import io.debezium.embedded.client.SimpleDebeziumClient;
import io.debezium.embedded.connector.ConnectorConfigurer;
import io.debezium.embedded.connector.ConnectorConfigurerFactory;
import io.debezium.embedded.factory.MapColumnModelFactory;
import io.debezium.embedded.handler.EntryHandler;
import io.debezium.embedded.handler.RecordChangeEventHandler;
import io.debezium.embedded.handler.RowDataHandler;
import io.debezium.embedded.handler.impl.AsyncRecordChangeEventHandlerImpl;
import io.debezium.embedded.handler.impl.MapRowDataHandlerImpl;
import io.debezium.embedded.handler.impl.SyncRecordChangeEventHandlerImpl;
import io.debezium.embedded.storage.OffsetStorageConfigurer;
import io.debezium.embedded.storage.OffsetStorageConfigurerFactory;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.RecordChangeEvent;
import io.debezium.engine.format.ChangeEventFormat;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.connect.source.SourceRecord;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Debezium Embedded 自动配置
 * 支持多个数据库实例的 Embedded 模式，支持多种数据库连接器
 */
@org.springframework.context.annotation.Configuration
@ConditionalOnClass({ DebeziumEngine.class })
@ConditionalOnProperty(prefix = "debezium.embedded", name = "enabled", havingValue = "true")
@EnableConfigurationProperties({ DebeziumClientProperties.class })
@Slf4j
public class DebeziumClientAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RowDataHandler<List<Map<String, String>>> recordRowDataHandler() {
        return new MapRowDataHandlerImpl(new MapColumnModelFactory());
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = DebeziumClientProperties.DEBEZIUM_ASYNC, havingValue = "true", matchIfMissing = true)
    public RecordChangeEventHandler asyncRecordChangeEventHandler(DebeziumClientProperties properties,
                                                                  RowDataHandler<List<Map<String, String>>> rowDataHandler,
                                                                  ObjectProvider<EntryHandler> entryHandlerProvider,
                                                                  @Qualifier("debeziumTaskExecutor") ThreadPoolTaskExecutor debeziumTaskExecutor) {
        return new AsyncRecordChangeEventHandlerImpl(properties.getSubscribeTypes(), 
                entryHandlerProvider.stream().collect(Collectors.toList()), rowDataHandler, debeziumTaskExecutor);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = DebeziumClientProperties.DEBEZIUM_ASYNC, havingValue = "false")
    public RecordChangeEventHandler syncRecordChangeEventHandler(DebeziumClientProperties properties,
                                                                 RowDataHandler<List<Map<String, String>>> rowDataHandler,
                                                                 ObjectProvider<EntryHandler> entryHandlerProvider) {
        return new SyncRecordChangeEventHandlerImpl(properties.getSubscribeTypes(), 
                entryHandlerProvider.stream().collect(Collectors.toList()), rowDataHandler);
    }

    /**
     * 为每个数据库实例创建独立的 DebeziumEmbeddedRunner
     */
    @Bean(initMethod = "start", destroyMethod = "stop")
    public DebeziumEmbeddedManager debeziumEmbeddedManager(DebeziumClientProperties properties,
                                                           RecordChangeEventHandler recordHandler,
                                                           @Qualifier("debeziumTaskExecutor") ThreadPoolTaskExecutor debeziumTaskExecutor) {
        return new DebeziumEmbeddedManager(properties, recordHandler, debeziumTaskExecutor);
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    @ConditionalOnProperty(value = DebeziumClientProperties.DEBEZIUM_MODE, havingValue = "simple", matchIfMissing = true)
    public SimpleDebeziumClient simpleDebeziumClient(ObjectProvider<SimpleDebeziumConnector> connectorProvider,
                                                     MessageHandler messageHandler,
                                                     DebeziumClientProperties debeziumProperties,
                                                     DebeziumSimpleProperties connectorProperties){
        // 1. 获取Spring 上下文中所有的 SimpleDebeziumConnector
        List<SimpleDebeziumConnector> simpleDebeziumConnectors = connectorProvider.stream().collect(Collectors.toList());
        // 2. 初始化配置文件中配置的 SimpleDebeziumConnector
        if(!CollectionUtils.isEmpty(connectorProperties.getInstances())){
            simpleDebeziumConnectors.addAll(connectorProperties.getInstances().stream()
                    .map(instance -> ConnectorUtil.createSimpleDebeziumConnector(instance))
                    .collect(Collectors.toList()));
        }
        // 3. 返回 SimpleDebeziumClient
        return (SimpleDebeziumClient) new SimpleDebeziumClient.Builder()
                .batchSize(debeziumProperties.getBatchSize())
                .filter(debeziumProperties.getFilter())
                .timeout(debeziumProperties.getTimeout())
                .unit(debeziumProperties.getUnit())
                .messageHandler(messageHandler)
                .setSubscribeTypes(debeziumProperties.getSubscribeTypes())
                .build(simpleDebeziumConnectors);
    }
    /**
     * Debezium Embedded 管理器
     * 负责管理多个数据库实例的 DebeziumEmbeddedRunner
     */
    public static class DebeziumEmbeddedManager {
        private final DebeziumClientProperties properties;
        private final RecordChangeEventHandler recordHandler;
        private final ThreadPoolTaskExecutor executor;
        private final Map<String, DebeziumEmbeddedRunner> runners = new ConcurrentHashMap<>();

        public DebeziumEmbeddedManager(DebeziumClientProperties properties,
                                       RecordChangeEventHandler recordHandler,
                                       ThreadPoolTaskExecutor executor) {
            this.properties = properties;
            this.recordHandler = recordHandler;
            this.executor = executor;
        }

        public void start() {
            log.info("Starting Debezium Embedded Manager with {} instances", properties.getInstances().size());
            
            for (int i = 0; i < properties.getInstances().size(); i++) {
                DebeziumClientProperties.DebeziumClientInstance instance = properties.getInstances().get(i);
                String instanceName = instance.getConnector().getDestination();
                
                if (instanceName == null || instanceName.trim().isEmpty()) {
                    instanceName = "debezium-instance-" + i;
                    log.warn("Instance {} has no destination name, using default name: {}", i, instanceName);
                }
                
                DebeziumEmbeddedRunner runner = new DebeziumEmbeddedRunner(
                        instance.getConnector(),
                        instance.getOffsetStorage(),
                        recordHandler,
                        executor,
                        instanceName
                );
                
                runners.put(instanceName, runner);
                runner.start();
            }
        }

        public void stop() throws IOException {
            log.info("Stopping Debezium Embedded Manager with {} instances", runners.size());
            
            for (Map.Entry<String, DebeziumEmbeddedRunner> entry : runners.entrySet()) {
                try {
                    entry.getValue().stop();
                    log.info("Stopped Debezium Embedded Runner: {}", entry.getKey());
                } catch (Exception e) {
                    log.error("Error stopping Debezium Embedded Runner: {}", entry.getKey(), e);
                }
            }
            runners.clear();
        }

        /**
         * 获取指定实例的 Runner
         */
        public DebeziumEmbeddedRunner getRunner(String instanceName) {
            return runners.get(instanceName);
        }

        /**
         * 获取所有实例的 Runner
         */
        public Map<String, DebeziumEmbeddedRunner> getAllRunners() {
            return new ConcurrentHashMap<>(runners);
        }
    }

    /**
     * Debezium Embedded 运行器
     * 负责单个数据库实例的 DebeziumEngine 生命周期管理
     */
    public static class DebeziumEmbeddedRunner {
        private final DebeziumConnectorProperties connectorProperties;
        private final DebeziumOffsetStorageProperties storageProperties;
        private final RecordChangeEventHandler recordHandler;
        private final ThreadPoolTaskExecutor executor;
        private final String instanceName;
        private DebeziumEngine<RecordChangeEvent<SourceRecord>> engine;

        public DebeziumEmbeddedRunner(DebeziumConnectorProperties connectorProperties,
                                      DebeziumOffsetStorageProperties storageProperties,
                                      RecordChangeEventHandler recordHandler,
                                      ThreadPoolTaskExecutor executor,
                                      String instanceName) {
            this.connectorProperties = connectorProperties;
            this.storageProperties = storageProperties;
            this.recordHandler = recordHandler;
            this.executor = executor;
            this.instanceName = instanceName;
        }

        public void start() {
            log.info("Starting Debezium Embedded Runner: {} with connector type: {}", 
                    instanceName, connectorProperties.getType());
            
            // 1. 创建基础配置
            Configuration.Builder builder = Configuration.create()
                    .with("name", connectorProperties.getDestination());

            // 2. 交由连接器配置器写入数据库相关配置
            ConnectorConfigurer connectorConfigurer = ConnectorConfigurerFactory.from(connectorProperties);
            connectorConfigurer.apply(builder, connectorProperties);

            // 3. 交由存储配置器写入 offset 相关参数
            OffsetStorageConfigurer storageConfigurer = OffsetStorageConfigurerFactory.from(storageProperties);
            storageConfigurer.apply(builder, storageProperties);

            Configuration config = builder.build();

            // 4. 创建 DebeziumEngine
            engine = DebeziumEngine
                    .create(ChangeEventFormat.of(Connect.class))
                    .using(config.asProperties())
                    .notifying((events, committer) -> recordHandler.handleEvent(events, committer, config.asProperties()))
                    .build();

            // 5. 启动引擎
            executor.execute(engine);
            
            log.info("Started Debezium Embedded Runner: {}", instanceName);
        }

        public void stop() throws IOException {
            log.info("Stopping Debezium Embedded Runner: {}", instanceName);
            if (engine != null) {
                engine.close();
                log.info("Stopped Debezium Embedded Runner: {}", instanceName);
            }
            // 注意：不要在这里调用 executor.shutdown()，因为 debeziumTaskExecutor 是共享的
            // 它的生命周期由 Spring 容器管理
        }

        public String getInstanceName() {
            return instanceName;
        }

        public DebeziumConnectorProperties getConnectorProperties() {
            return connectorProperties;
        }

        public DebeziumOffsetStorageProperties getStorageProperties() {
            return storageProperties;
        }
    }
}



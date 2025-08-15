package io.debezium.embedded.spring.boot;

import io.debezium.config.Configuration;
import io.debezium.embedded.Connect;
import io.debezium.embedded.factory.MapColumnModelFactory;
import io.debezium.embedded.handler.EntryHandler;
import io.debezium.embedded.handler.RecordChangeEventHandler;
import io.debezium.embedded.handler.RowDataHandler;
import io.debezium.embedded.handler.impl.AsyncRecordChangeEventHandlerImpl;
import io.debezium.embedded.handler.impl.MapRowDataHandlerImpl;
import io.debezium.embedded.handler.impl.SyncRecordChangeEventHandlerImpl;
import io.debezium.embedded.spring.boot.storage.OffsetStorageConfigurer;
import io.debezium.embedded.spring.boot.storage.OffsetStorageConfigurerFactory;
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

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Debezium Embedded 自动配置
 * 在现有架构基础上集成 Embedded 模式
 */
@org.springframework.context.annotation.Configuration
@ConditionalOnClass({ DebeziumEngine.class })
@ConditionalOnProperty(prefix = "debezium.embedded", name = "enabled", havingValue = "true")
@EnableConfigurationProperties({ DebeziumProperties.class, DebeziumEmbeddedProperties.class, DebeziumOffsetStorageProperties.class })
@Import(DebeziumThreadPoolAutoConfiguration.class)
@Slf4j
public class DebeziumEmbeddedAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RowDataHandler<List<Map<String, String>>> recordRowDataHandler() {
        return new MapRowDataHandlerImpl(new MapColumnModelFactory());
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = DebeziumProperties.DEBEZIUM_ASYNC, havingValue = "true", matchIfMissing = true)
    public RecordChangeEventHandler asyncRecordChangeEventHandler(DebeziumProperties properties,
                                                                 RowDataHandler<List<Map<String, String>>> rowDataHandler,
                                                                 ObjectProvider<EntryHandler> entryHandlerProvider,
                                                                 @Qualifier("debeziumTaskExecutor") ThreadPoolTaskExecutor debeziumTaskExecutor) {
        return new AsyncRecordChangeEventHandlerImpl(properties.getSubscribeTypes(), 
                entryHandlerProvider.stream().collect(Collectors.toList()), rowDataHandler, debeziumTaskExecutor);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = DebeziumProperties.DEBEZIUM_ASYNC, havingValue = "false")
    public RecordChangeEventHandler syncRecordChangeEventHandler(DebeziumProperties properties,
                                                                RowDataHandler<List<Map<String, String>>> rowDataHandler,
                                                                ObjectProvider<EntryHandler> entryHandlerProvider) {
        return new SyncRecordChangeEventHandlerImpl(properties.getSubscribeTypes(), 
                entryHandlerProvider.stream().collect(Collectors.toList()), rowDataHandler);
    }

    @Bean(destroyMethod = "shutdown")
    public ExecutorService debeziumEmbeddedExecutor(@Qualifier("debeziumTaskExecutor") ThreadPoolTaskExecutor debeziumTaskExecutor) {
        return Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r);
            t.setName("debezium-embedded-" + t.getId());
            return t;
        });
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public DebeziumEmbeddedRunner debeziumEmbeddedRunner(DebeziumEmbeddedProperties embeddedProperties,
                                                         DebeziumOffsetStorageProperties storageProperties,
                                                         RecordChangeEventHandler recordHandler,
                                                         ExecutorService debeziumEmbeddedExecutor) {
        return new DebeziumEmbeddedRunner(embeddedProperties, storageProperties, recordHandler, debeziumEmbeddedExecutor);
    }

    /**
     * Debezium Embedded 运行器
     */
    public static class DebeziumEmbeddedRunner {
        private final DebeziumEmbeddedProperties embeddedProperties;
        private final DebeziumOffsetStorageProperties storageProperties;
        private final RecordChangeEventHandler recordHandler;
        private final ExecutorService executor;
        private DebeziumEngine<RecordChangeEvent<SourceRecord>> engine;

        public DebeziumEmbeddedRunner(DebeziumEmbeddedProperties embeddedProperties,
                                     DebeziumOffsetStorageProperties storageProperties,
                                     RecordChangeEventHandler recordHandler,
                                     ExecutorService executor) {
            this.embeddedProperties = embeddedProperties;
            this.storageProperties = storageProperties;
            this.recordHandler = recordHandler;
            this.executor = executor;
        }

        public void start() {
            log.info("Starting Debezium Embedded with destination: {}", embeddedProperties.getDestination());
            
            // 1. 创建配置
            Configuration.Builder builder = Configuration.create()
                    .with("name", embeddedProperties.getDestination())
                    .with("connector.class", embeddedProperties.getConnectorClass())
                    .with("database.hostname", embeddedProperties.getHost())
                    .with("database.port", embeddedProperties.getPort())
                    .with("database.user", embeddedProperties.getUsername())
                    .with("database.password", embeddedProperties.getPassword());

            // 2. 数据库相关配置
            if (embeddedProperties.getDatabaseIncludeList() != null) {
                builder.with("database.include.list", embeddedProperties.getDatabaseIncludeList());
            }
            if (embeddedProperties.getTableIncludeList() != null) {
                builder.with("table.include.list", embeddedProperties.getTableIncludeList());
            }
            if (embeddedProperties.getSchemaIncludeList() != null) {
                builder.with("schema.include.list", embeddedProperties.getSchemaIncludeList());
            }

            // 3. 数据库历史配置
            builder.with("database.history", "io.debezium.relational.history.FileDatabaseHistory");
            builder.with("database.history.file.filename", "/tmp/dbhistory.dat");

            // 4. 其他配置
            builder.with("include.schema.changes", "false");

            // 5. 交由 storage 配置器写入 offset 相关参数
            OffsetStorageConfigurer configurer = OffsetStorageConfigurerFactory.from(storageProperties);
            configurer.apply(builder, storageProperties);

            Configuration config = builder.build();

            // 6. 创建 DebeziumEngine
            engine = DebeziumEngine
                    .create(ChangeEventFormat.of(Connect.class))
                    .using(config.asProperties())
                    .notifying((events, committer) -> recordHandler.handleEvent(events, committer, config.asProperties()))
                    .build();

            // 7. 启动引擎
            executor.execute(engine);
        }

        public void stop() throws IOException {
            log.info("Stopping Debezium Embedded with destination: {}", embeddedProperties.getDestination());
            if (engine != null) {
                engine.close();
            }
            executor.shutdown();
        }
    }
}



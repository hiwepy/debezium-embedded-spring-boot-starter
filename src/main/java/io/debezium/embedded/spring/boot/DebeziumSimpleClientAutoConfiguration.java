package io.debezium.embedded.spring.boot;

import io.debezium.embedded.protocol.DebeziumEntry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
@ConditionalOnProperty(value = DebeziumProperties.DEBEZIUM_MODE, havingValue = "simple", matchIfMissing = true)
@EnableConfigurationProperties({DebeziumProperties.class, DebeziumSimpleProperties.class})
@Import(DebeziumThreadPoolAutoConfiguration.class)
@Slf4j
public class DebeziumSimpleClientAutoConfiguration {

    @Bean
    public RowDataHandler<DebeziumEntry.RowData> rowDataHandler() {
        return new RowDataHandlerImpl(new EntryColumnModelFactory());
    }

    @Bean
    @ConditionalOnProperty(value = DebeziumProperties.DEBEZIUM_ASYNC, havingValue = "true", matchIfMissing = true)
    public MessageHandler asyncMessageHandler(DebeziumProperties properties,
                                              RowDataHandler<DebeziumEntry.RowData> rowDataHandler,
                                              ObjectProvider<EntryHandler> entryHandlerProvider,
                                              @Qualifier("debeziumTaskExecutor") ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        return new AsyncMessageHandlerImpl(properties.getSubscribeTypes(), entryHandlerProvider.stream().collect(Collectors.toList()), rowDataHandler, threadPoolTaskExecutor);
    }

    @Bean
    @ConditionalOnProperty(value = DebeziumProperties.DEBEZIUM_ASYNC, havingValue = "false")
    public MessageHandler syncMessageHandler(DebeziumProperties properties,
                                             RowDataHandler<DebeziumEntry.RowData> rowDataHandler,
                                             ObjectProvider<EntryHandler> entryHandlerProvider) {
        return new SyncMessageHandlerImpl(properties.getSubscribeTypes(), entryHandlerProvider.stream().collect(Collectors.toList()), rowDataHandler);
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public SimpleDebeziumClient simpleDebeziumClient(ObjectProvider<SimpleDebeziumConnector> connectorProvider,
                                                  MessageHandler messageHandler,
                                                  DebeziumProperties debeziumProperties,
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

}

package io.debezium.embedded.util;

import io.debezium.client.impl.ClusterDebeziumConnector;
import io.debezium.client.impl.ClusterNodeAccessStrategy;
import io.debezium.client.impl.SimpleDebeziumConnector;
import io.debezium.client.impl.SimpleNodeAccessStrategy;
import io.debezium.client.kafka.KafkaDebeziumConnector;
import io.debezium.client.kafka.KafkaOffsetDebeziumConnector;
import io.debezium.client.pulsarmq.PulsarMQDebeziumConnector;
import io.debezium.client.rabbitmq.RabbitMQDebeziumConnector;
import io.debezium.client.rocketmq.RocketMQDebeziumConnector;
import io.debezium.common.zookeeper.ZkClientx;
import io.debezium.spring.boot.*;
import org.springframework.util.StringUtils;

import java.net.InetSocketAddress;

public class ConnectorUtil {

    /**
     * 创建集群模式的 Debezium 连接器
     * @param instance 实例配置
     * @return Debezium 连接器
     */
    public static ClusterDebeziumConnector createClusterDebeziumConnector(DebeziumClusterProperties.Instance instance) {
        if (StringUtils.hasText(instance.getZkServers())) {
            ClusterDebeziumConnector debeziumConnector = new ClusterDebeziumConnector(instance.getUsername(),
                    instance.getPassword(),
                    instance.getDestination(),
                    new ClusterNodeAccessStrategy(instance.getDestination(), ZkClientx.getZkClient(instance.getZkServers())));
            debeziumConnector.setSoTimeout(instance.getSoTimeout());
            debeziumConnector.setIdleTimeout(instance.getIdleTimeout());
            debeziumConnector.setRetryTimes(instance.getRetryTimes());
            debeziumConnector.setRetryInterval(instance.getRetryInterval());
            return debeziumConnector;
        }
        ClusterDebeziumConnector debeziumConnector = new ClusterDebeziumConnector(
                instance.getUsername(),
                instance.getPassword(),
                instance.getDestination(),
                new SimpleNodeAccessStrategy(AddressUtils.parseAddresses(instance.getAddresses())));
        debeziumConnector.setSoTimeout(instance.getSoTimeout());
        debeziumConnector.setIdleTimeout(instance.getIdleTimeout());
        debeziumConnector.setRetryTimes(instance.getRetryTimes());
        debeziumConnector.setRetryInterval(instance.getRetryInterval());
        return debeziumConnector;
    }

    /**
     * 创建 Kafka 连接器
     * @param instance 实例配置
     * @return Debezium 连接器
     */
    public static KafkaDebeziumConnector createKafkaDebeziumConnector(DebeziumKafkaClientProperties.Instance instance) {
        KafkaDebeziumConnector connector = instance.isEarliest() ? new KafkaOffsetDebeziumConnector(instance.getServers(),
                instance.getTopic(),  instance.getPartition(), instance.getGroupId(),
                Boolean.TRUE) : new KafkaDebeziumConnector(instance.getServers(),
                instance.getTopic(),  instance.getPartition(), instance.getGroupId(),
                instance.getBatchSize(), Boolean.TRUE);
        return connector;
    }

    /**
     * 创建 PulsarMQ 连接器
     * @param instance 实例配置
     * @return Debezium 连接器
     */
    public static PulsarMQDebeziumConnector createPulsarMQDebeziumConnector(DebeziumPulsarClientProperties.Instance instance) {
        PulsarMQDebeziumConnector connector = new PulsarMQDebeziumConnector(Boolean.TRUE,
                instance.getServiceUrl(), instance.getRoleToken(), instance.getTopic(),
                instance.getSubscriptName(), instance.getBatchSize(), instance.getBatchTimeoutSeconds(),
                instance.getBatchProcessTimeoutSeconds(), instance.getRedeliveryDelaySeconds(),
                instance.getAckTimeoutSeconds(),
                instance.isRetry(), instance.isRetryDLQUpperCase(), instance.getMaxRedeliveryCount());
        return connector;
    }

    /**
     * 创建 RabbitMQ 连接器
     * @param instance 实例配置
     * @return Debezium 连接器
     */
    public static RabbitMQDebeziumConnector createRabbitMQDebeziumConnector(DebeziumRabbitmqClientProperties.Instance instance) {
        RabbitMQDebeziumConnector connector = new RabbitMQDebeziumConnector(instance.getAddresses(), instance.getVhost(),
                instance.getQueueName(), instance.getAccessKey(), instance.getSecretKey(),
                instance.getUsername(), instance.getPassword(), instance.getResourceOwnerId(),
                Boolean.TRUE);
        return connector;
    }

    /**
     * 创建 RocketMQ 连接器
     * @param instance 实例配置
     * @return Debezium 连接器
     */
    public static RocketMQDebeziumConnector createRocketMQDebeziumConnector(DebeziumRocketmqClientProperties.Instance instance) {
        // 1、创建连接实例
        RocketMQDebeziumConnector connector;
        if (StringUtils.hasText(instance.getAccessKey()) && StringUtils.hasText(instance.getSecretKey())) {
            if (StringUtils.hasText(instance.getNamespace())) {
                connector = new RocketMQDebeziumConnector(instance.getNameServer(), instance.getTopic(),
                        instance.getGroupName(), instance.getAccessKey(), instance.getSecretKey(),
                        instance.getBatchSize(), Boolean.TRUE, instance.isEnableMessageTrace(), null,
                        instance.getAccessChannel(), instance.getNamespace());
            } else if (StringUtils.hasText(instance.getCustomizedTraceTopic())) {
                connector = new RocketMQDebeziumConnector(instance.getNameServer(), instance.getTopic(),
                        instance.getGroupName(), instance.getAccessKey(), instance.getSecretKey(),
                        instance.getBatchSize(), Boolean.TRUE, instance.isEnableMessageTrace(),
                        instance.getCustomizedTraceTopic(), instance.getAccessChannel());
            } else {
                connector = new RocketMQDebeziumConnector(instance.getNameServer(), instance.getTopic(),
                        instance.getGroupName(), instance.getAccessKey(), instance.getSecretKey(),
                        instance.getBatchSize(), Boolean.TRUE);
            }
        } else {
            connector = new RocketMQDebeziumConnector(instance.getNameServer(), instance.getTopic(),
                    instance.getGroupName(), instance.getBatchSize(), Boolean.TRUE);
        }
        return connector;
    }

    /**
     * 创建单机模式的 Debezium 连接器
     * @param instance 实例配置
     * @return Debezium 连接器
     */
    public static SimpleDebeziumConnector createSimpleDebeziumConnector(DebeziumSimpleProperties.Instance instance) {
        InetSocketAddress address = new InetSocketAddress(instance.getHost(), instance.getPort());
        SimpleDebeziumConnector debeziumConnector = new SimpleDebeziumConnector(address,
                instance.getUsername(),
                instance.getPassword(),
                instance.getDestination());
        debeziumConnector.setSoTimeout(instance.getSoTimeout());
        debeziumConnector.setIdleTimeout(instance.getIdleTimeout());
        return debeziumConnector;
    }

}

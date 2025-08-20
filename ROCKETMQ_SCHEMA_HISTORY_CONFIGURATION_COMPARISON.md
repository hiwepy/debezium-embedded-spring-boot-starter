# RocketMQ Schema History 配置对比

## 概述

本文档对比了 [Debezium 官方文档](https://debezium.io/documentation/reference/3.2/configuration/storage.html) 中的 RocketMQ Schema History 配置与当前实现的对应关系。

## 官方文档配置参数

根据官方文档，RocketMQ Schema History 的配置参数如下：

| Property | Default | Description |
|----------|---------|-------------|
| `schema.history.internal` | No default | Must be set to `io.debezium.storage.rocketmq.history.RocketMqSchemaHistory` |
| `schema.history.internal.rocketmq.topic` | No Default | The name of the RocketMQ topic where Debezium stores the database schema history |
| `schema.history.internal.rocketmq.name.srv.addr` | No Default | Specifies the host and port where the Apache RocketMQ NameServer discovery service is available |
| `schema.history.internal.rocketmq.acl.enabled` | false | Specifies whether to enable access control lists in RocketMQ |
| `schema.history.internal.rocketmq.access.key` | No Default | Specifies the RocketMQ access key |
| `schema.history.internal.rocketmq.secret.key` | No Default | Specifies the RocketMQ secret key |
| `schema.history.internal.rocketmq.recovery.attempts` | No Default | Specifies the number of consecutive attempts in which RocketMQ returns no data before recovery completes |
| `schema.history.internal.rocketmq.recovery.poll.interval.ms` | No Default | Specifies the time, in milliseconds, that Debezium waits after each poll attempt to recover the history |
| `schema.history.internal.rocketmq.store.record.timeout.ms` | No Default | Specifies the time, in milliseconds, that Debezium waits for a write to Rocket MQ to complete before the operation times out |

## 当前实现配置

### 1. DebeziumSchemaHistoryProperties.RocketMq 类

```java
@Data
public static class RocketMq {
    /**
     * RocketMQ 主题名称
     * 
     * <p>指定存储数据库模式历史记录的 RocketMQ 主题名称。</p>
     * <p>无默认值，必须显式配置。</p>
     */
    private String topic;
    
    /**
     * NameServer 地址
     * 
     * <p>指定 Apache RocketMQ NameServer 发现服务的主机和端口。</p>
     * <p>无默认值，必须显式配置。</p>
     */
    private String nameSrvAddr;
    
    /**
     * 是否启用 ACL
     * 
     * <p>指定是否在 RocketMQ 中启用访问控制列表。</p>
     * <p>默认值：false</p>
     */
    private Boolean aclEnabled = false;
    
    /**
     * 访问密钥
     * 
     * <p>指定 RocketMQ 访问密钥。</p>
     * <p>如果启用了 ACL，则必须包含值。</p>
     */
    private String accessKey;
    
    /**
     * 秘密密钥
     * 
     * <p>指定 RocketMQ 秘密密钥。</p>
     * <p>如果启用了 ACL，则必须包含值。</p>
     */
    private String secretKey;
    
    /**
     * 恢复尝试次数
     * 
     * <p>指定 RocketMQ 在恢复完成前返回无数据的连续尝试次数。</p>
     * <p>无默认值。</p>
     */
    private Integer recoveryAttempts;
    
    /**
     * 恢复轮询间隔（毫秒）
     * 
     * <p>指定 Debezium 在每次轮询尝试后等待恢复历史记录的时间（毫秒）。</p>
     * <p>无默认值。</p>
     */
    private Integer recoveryPollIntervalMs;
    
    /**
     * 存储记录超时时间（毫秒）
     * 
     * <p>指定 Debezium 等待写入 RocketMQ 完成的操作超时时间（毫秒）。</p>
     * <p>无默认值。</p>
     */
    private Integer storeRecordTimeoutMs;
}
```

### 2. RocketMqSchemaHistoryConfigurer 配置映射

```java
builder.with("schema.history.internal", "io.debezium.storage.rocketmq.history.RocketMqSchemaHistory");

// 严格按照官方文档配置参数
map.from(rocketMq::getTopic).whenHasText().to(value -> builder.with("schema.history.internal.rocketmq.topic", value));
map.from(rocketMq::getNameSrvAddr).whenHasText().to(value -> builder.with("schema.history.internal.rocketmq.name.srv.addr", value));
map.from(rocketMq::getAclEnabled).to(value -> builder.with("schema.history.internal.rocketmq.acl.enabled", value));
map.from(rocketMq::getAccessKey).whenHasText().to(value -> builder.with("schema.history.internal.rocketmq.access.key", value));
map.from(rocketMq::getSecretKey).whenHasText().to(value -> builder.with("schema.history.internal.rocketmq.secret.key", value));
map.from(rocketMq::getRecoveryAttempts).to(value -> builder.with("schema.history.internal.rocketmq.recovery.attempts", value));
map.from(rocketMq::getRecoveryPollIntervalMs).to(value -> builder.with("schema.history.internal.rocketmq.recovery.poll.interval.ms", value));
map.from(rocketMq::getStoreRecordTimeoutMs).to(value -> builder.with("schema.history.internal.rocketmq.store.record.timeout.ms", value));
```

## 配置参数对比

| 官方文档参数 | Spring Boot 属性 | 配置映射 | 状态 |
|-------------|-----------------|----------|------|
| `schema.history.internal.rocketmq.topic` | `topic` | ✅ 正确映射 | ✅ 匹配 |
| `schema.history.internal.rocketmq.name.srv.addr` | `nameSrvAddr` | ✅ 正确映射 | ✅ 匹配 |
| `schema.history.internal.rocketmq.acl.enabled` | `aclEnabled` | ✅ 正确映射 | ✅ 匹配 |
| `schema.history.internal.rocketmq.access.key` | `accessKey` | ✅ 正确映射 | ✅ 匹配 |
| `schema.history.internal.rocketmq.secret.key` | `secretKey` | ✅ 正确映射 | ✅ 匹配 |
| `schema.history.internal.rocketmq.recovery.attempts` | `recoveryAttempts` | ✅ 正确映射 | ✅ 匹配 |
| `schema.history.internal.rocketmq.recovery.poll.interval.ms` | `recoveryPollIntervalMs` | ✅ 正确映射 | ✅ 匹配 |
| `schema.history.internal.rocketmq.store.record.timeout.ms` | `storeRecordTimeoutMs` | ✅ 正确映射 | ✅ 匹配 |

## 配置示例

### YAML 配置示例

```yaml
debezium:
  history:
    type: ROCKETMQ
    rocket-mq:
      topic: "debezium-schema-history"
      name-srv-addr: "localhost:9876"
      acl-enabled: false
      access-key: "your-access-key"
      secret-key: "your-secret-key"
      recovery-attempts: 100
      recovery-poll-interval-ms: 1000
      store-record-timeout-ms: 5000
```

### Java 配置示例

```java
@Bean
public DebeziumSchemaHistoryProperties rocketMqSchemaHistoryProperties() {
    DebeziumSchemaHistoryProperties properties = new DebeziumSchemaHistoryProperties();
    properties.setType(SchemaHistoryType.ROCKETMQ);
    
    DebeziumSchemaHistoryProperties.RocketMq rocketMq = properties.getRocketMq();
    rocketMq.setTopic("debezium-schema-history");
    rocketMq.setNameSrvAddr("localhost:9876");
    rocketMq.setAclEnabled(false);
    rocketMq.setAccessKey("your-access-key");
    rocketMq.setSecretKey("your-secret-key");
    rocketMq.setRecoveryAttempts(100);
    rocketMq.setRecoveryPollIntervalMs(1000);
    rocketMq.setStoreRecordTimeoutMs(5000);
    
    return properties;
}
```

## 新增内容

### 1. 新增配置类

- **`RocketMqSchemaHistoryConfigurer`**: RocketMQ Schema History 配置器
- **`DebeziumSchemaHistoryProperties.RocketMq`**: RocketMQ 配置属性类

### 2. 新增枚举值

- **`SchemaHistoryType.ROCKETMQ`**: 在 SchemaHistoryType 枚举中添加了 ROCKETMQ 类型

### 3. 更新工厂类

- **`SchemaHistoryConfigurerFactory`**: 在工厂类中添加了 RocketMQ 配置器的创建逻辑

### 4. 新增示例

- **`RocketMqSchemaHistoryExample`**: 创建了 RocketMQ Schema History 配置示例

## 总结

RocketMQ Schema History 配置现在完全符合 [Debezium 官方文档](https://debezium.io/documentation/reference/3.2/configuration/storage.html) 的规范：

1. ✅ 所有配置参数都已正确映射
2. ✅ 参数名称与官方文档完全一致
3. ✅ 配置前缀统一使用 `schema.history.internal`
4. ✅ 提供了完整的配置示例
5. ✅ 支持所有官方文档中列出的配置选项
6. ✅ 包含了 ACL 认证、恢复和超时配置

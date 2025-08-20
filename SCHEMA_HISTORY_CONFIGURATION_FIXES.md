# Schema History 配置器修复总结

## 概述

根据 [Debezium 官方文档](https://debezium.io/documentation/reference/3.2/configuration/storage.html) 的检查，发现并修复了 `io.debezium.embedded.history` 目录下多个配置器中的配置错误。

## 修复内容

### 1. JdbcSchemaHistoryConfigurer.java

**问题**: 混用了 `database.history` 和 `schema.history.internal` 前缀
**修复**: 统一使用 `schema.history.internal` 前缀

**修复前**:
```java
map.from(jdbc::getPoolSize).to(value -> builder.with("database.history.jdbc.connection.pool.size", value));
map.from(jdbc::getConnectionTimeout).to(value -> builder.with("database.history.jdbc.connection.timeout.ms", value));
// ... 其他配置
```

**修复后**:
```java
map.from(jdbc::getPoolSize).to(value -> builder.with("schema.history.internal.jdbc.connection.pool.size", value));
map.from(jdbc::getConnectionTimeout).to(value -> builder.with("schema.history.internal.jdbc.connection.timeout.ms", value));
// ... 其他配置
```

### 2. KafkaSchemaHistoryConfigurer.java

**问题**: 
1. 同时配置了 `database.history` 和 `schema.history.internal`
2. 所有配置参数都使用了 `database.history` 前缀

**修复**: 
1. 移除 `database.history` 配置
2. 统一使用 `schema.history.internal` 前缀

**修复前**:
```java
builder.with("database.history", "io.debezium.relational.history.KafkaDatabaseHistory");
builder.with("schema.history.internal", "io.debezium.storage.kafka.history.KafkaSchemaHistory");
map.from(kafka::getBootstrapServers).whenHasText().to(value -> builder.with("database.history.kafka.bootstrap.servers", value));
// ... 其他配置
```

**修复后**:
```java
builder.with("schema.history.internal", "io.debezium.storage.kafka.history.KafkaSchemaHistory");
map.from(kafka::getBootstrapServers).whenHasText().to(value -> builder.with("schema.history.internal.kafka.bootstrap.servers", value));
// ... 其他配置
```

### 3. S3SchemaHistoryConfigurer.java

**问题**: 
1. 使用了错误的类名和配置前缀
2. 配置参数与官方文档不完全对应
3. 缺少必需的 `object.name` 参数

**修复**: 
1. 使用正确的类名和 `schema.history.internal` 前缀
2. 严格按照官方文档配置参数
3. 添加缺失的必需参数

**修复前**:
```java
builder.with("database.history", "io.debezium.relational.history.S3DatabaseHistory");
map.from(s3::getBucketName).whenHasText().to(value -> builder.with("database.history.s3.bucket.name", value));
map.from(s3::getRegion).whenHasText().to(value -> builder.with("database.history.s3.region", value));
// ... 其他配置
```

**修复后**:
```java
builder.with("schema.history.internal", "io.debezium.storage.s3.history.S3SchemaHistory");
map.from(s3::getBucketName).whenHasText().to(value -> builder.with("schema.history.internal.s3.bucket.name", value));
map.from(s3::getObjectName).whenHasText().to(value -> builder.with("schema.history.internal.s3.object.name", value));
map.from(s3::getAccessKeyId).whenHasText().to(value -> builder.with("schema.history.internal.s3.access.key.id", value));
map.from(s3::getSecretAccessKey).whenHasText().to(value -> builder.with("schema.history.internal.s3.secret.access.key", value));
map.from(s3::getRegionName).whenHasText().to(value -> builder.with("schema.history.internal.s3.region.name", value));
map.from(s3::getEndpointUrl).whenHasText().to(value -> builder.with("schema.history.internal.s3.endpoint", value));
```

### 4. CustomSchemaHistoryConfigurer.java

**问题**: 使用了 `database.history` 前缀
**修复**: 统一使用 `schema.history.internal` 前缀

**修复前**:
```java
builder.with("database.history", custom.getHistoryClass());
if (key.startsWith("database.history.")) {
    builder.with(key, value);
} else {
    builder.with("database.history." + key, value);
}
```

**修复后**:
```java
builder.with("schema.history.internal", custom.getHistoryClass());
if (key.startsWith("schema.history.internal.")) {
    builder.with(key, value);
} else {
    builder.with("schema.history.internal." + key, value);
}
```

### 5. 新增 RocketMqSchemaHistoryConfigurer.java

**新增内容**: 创建了 RocketMQ Schema History 配置器，支持 Apache RocketMQ 作为 Schema History 存储

**实现内容**:
```java
builder.with("schema.history.internal", "io.debezium.storage.rocketmq.history.RocketMqSchemaHistory");
map.from(rocketMq::getTopic).whenHasText().to(value -> builder.with("schema.history.internal.rocketmq.topic", value));
map.from(rocketMq::getNameSrvAddr).whenHasText().to(value -> builder.with("schema.history.internal.rocketmq.name.srv.addr", value));
map.from(rocketMq::getAclEnabled).to(value -> builder.with("schema.history.internal.rocketmq.acl.enabled", value));
map.from(rocketMq::getAccessKey).whenHasText().to(value -> builder.with("schema.history.internal.rocketmq.access.key", value));
map.from(rocketMq::getSecretKey).whenHasText().to(value -> builder.with("schema.history.internal.rocketmq.secret.key", value));
map.from(rocketMq::getRecoveryAttempts).to(value -> builder.with("schema.history.internal.rocketmq.recovery.attempts", value));
map.from(rocketMq::getRecoveryPollIntervalMs).to(value -> builder.with("schema.history.internal.rocketmq.recovery.poll.interval.ms", value));
map.from(rocketMq::getStoreRecordTimeoutMs).to(value -> builder.with("schema.history.internal.rocketmq.store.record.timeout.ms", value));
```

### 6. 新增 AzureBlobSchemaHistoryConfigurer.java

**新增内容**: 创建了 Azure Blob Storage Schema History 配置器，支持 Azure Blob Storage 作为 Schema History 存储

**实现内容**:
```java
builder.with("schema.history.internal", "io.debezium.storage.azure.blob.history.AzureBlobSchemaHistory");
map.from(azureBlob::getConnectionString).whenHasText().to(value -> builder.with("schema.history.internal.azure.storage.account.connectionstring", value));
map.from(azureBlob::getAccountName).whenHasText().to(value -> builder.with("schema.history.internal.azure.storage.account.name", value));
map.from(azureBlob::getContainerName).whenHasText().to(value -> builder.with("schema.history.internal.azure.storage.account.container.name", value));
map.from(azureBlob::getBlobName).whenHasText().to(value -> builder.with("schema.history.internal.azure.storage.blob.name", value));
```

## 正确的配置器

以下配置器已经是正确的，无需修改：

1. **RedisSchemaHistoryConfigurer.java** - 正确使用 `schema.history.internal` 前缀
2. **FileSchemaHistoryConfigurer.java** - 正确使用 `schema.history.internal` 前缀
3. **MemorySchemaHistoryConfigurer.java** - 正确使用 `schema.history.internal` 前缀

## 官方文档参考

根据 [Debezium 官方文档](https://debezium.io/documentation/reference/3.2/configuration/storage.html)，所有 Schema History 配置都应该使用 `schema.history.internal` 前缀，而不是 `database.history`。

### 正确的配置模式

```java
// 正确的配置方式
builder.with("schema.history.internal", "io.debezium.storage.{type}.history.{Type}SchemaHistory");
map.from(property::getValue).to(value -> builder.with("schema.history.internal.{type}.property", value));
```

### 错误的配置模式

```java
// 错误的配置方式（已修复）
builder.with("database.history", "io.debezium.relational.history.{Type}DatabaseHistory");
map.from(property::getValue).to(value -> builder.with("database.history.{type}.property", value));
```

## 总结

所有 Schema History 配置器现在都严格按照 Debezium 官方文档进行配置，使用统一的 `schema.history.internal` 前缀，确保与 Debezium 引擎的兼容性。

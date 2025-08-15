# debezium-embedded-spring-boot-starter

基于 Debezium Embedded 的数据订阅组件，支持多种 Offset 存储类型。

## 功能特性

- 支持多种 Offset 存储类型：File、Kafka、JDBC、Redis、S3、自定义
- 提供同步/异步事件处理
- 支持注解驱动的表级事件处理
- 自动配置，开箱即用
- 线程池管理
- 优雅启动和关闭
- **新增：Embedded 模式** - 在现有 DebeziumClient 架构基础上封装 DebeziumEngine

## 架构模式

### 1. 现有模式（Simple/Cluster/Kafka/RocketMQ/PulsarMQ/RabbitMQ）
- 使用现有的 `DebeziumClient` 架构
- 通过 `Connector` 连接外部 Debezium 服务
- 支持多种消息队列作为数据源

### 2. Embedded 模式（新增）
- 直接使用 `DebeziumEngine` 连接数据库
- 封装 `DebeziumEngine` 初始化细节
- 使用现有的 `ChangeEventHandler`/`RecordChangeEventHandler` 和 `EntryHandler` 架构
- 支持多种 Offset 存储类型

## 快速开始

### 1. 添加依赖

```xml
<dependency>
    <groupId>com.github.hiwepy</groupId>
    <artifactId>debezium-embedded-spring-boot-starter</artifactId>
    <version>3.3.x.20240823.RELEASE</version>
</dependency>
```

### 2. Embedded 模式配置

```yaml
debezium:
  embedded:
    enabled: true  # 启用 Embedded 模式
    destination: my-mysql-connector
    connector-class: io.debezium.connector.mysql.MySqlConnector
    host: localhost
    port: 3306
    username: root
    password: password
    database-include-list: test_db
    table-include-list: test_db.users,test_db.orders
  thread-pool:
    core-pool-size: 2
    max-pool-size: 4
    queue-capacity: 1000
```

### 3. 配置 Offset 存储

#### File 存储（默认）
```yaml
debezium:
  offset-storage:
    type: FILE
    file:
      file-name: /var/lib/debezium/offsets.dat
      flush-interval-ms: 60000
```

#### Kafka 存储
```yaml
debezium:
  offset-storage:
    type: KAFKA
    kafka:
      bootstrap-servers: localhost:9092
      topic: debezium-offsets
      partitions: 3
      replication-factor: 1
```

#### JDBC 存储
```yaml
debezium:
  offset-storage:
    type: JDBC
    jdbc:
      url: jdbc:mysql://localhost:3306/debezium
      username: root
      password: password
      driver-class-name: com.mysql.cj.jdbc.Driver
      table-name: debezium_offsets
      flush-interval-ms: 60000
```

#### Redis 存储
```yaml
debezium:
  offset-storage:
    type: REDIS
    redis:
      host: localhost
      port: 6379
      password: ""
      database: 0
      key-prefix: "debezium:offsets:"
      flush-interval-ms: 60000
```

#### S3 存储
```yaml
debezium:
  offset-storage:
    type: S3
    s3:
      bucket-name: my-debezium-bucket
      region: us-west-2
      access-key-id: your-access-key
      secret-access-key: your-secret-key
      endpoint: https://s3.us-west-2.amazonaws.com
      key-prefix: debezium/offsets/
      flush-interval-ms: 60000
```

#### 自定义存储
```yaml
debezium:
  offset-storage:
    type: CUSTOM
    custom:
      class-name: com.example.CustomOffsetBackingStore
      props:
        custom.property: value
        cache.ttl.ms: 30000
```

### 4. 实现事件处理器

#### 方式一：实现 EntryHandler 接口（推荐）
```java
@Component
public class UserEventHandler implements EntryHandler<User> {
    
    @Override
    public void insert(User user) {
        log.info("用户新增: {}", user);
        // 处理用户新增逻辑
    }
    
    @Override
    public void update(User before, User after) {
        log.info("用户更新: before={}, after={}", before, after);
        // 处理用户更新逻辑
    }
    
    @Override
    public void delete(User user) {
        log.info("用户删除: {}", user);
        // 处理用户删除逻辑
    }
}
```

#### 方式二：使用注解
```java
@Component
@DebeziumEventHandler
public class OrderEventHandler {
    
    @OnDebeziumEvent(schema = "test_db", table = "orders", eventType = {DebeziumEntry.EventType.CREATE})
    public void onOrderInsert(DebeziumModel model, List<Map<String, String>> rowData) {
        log.info("订单新增: {}", rowData);
    }
    
    @OnDebeziumEvent(schema = "test_db", table = "orders", eventType = {DebeziumEntry.EventType.UPDATE})
    public void onOrderUpdate(DebeziumModel model, List<Map<String, String>> rowData) {
        log.info("订单更新: {}", rowData);
    }
}
```

### 5. 配置实体类
```java
@TableName("users")
public class User {
    @TableId
    private Long id;
    private String name;
    private String email;
    // getters and setters
}
```

## 数据流架构

### Embedded 模式数据流
```
数据库 Binlog 
    ↓
DebeziumEngine (自动配置)
    ↓
RecordChangeEventHandler (同步/异步)
    ↓
EntryHandler<T> (业务处理)
    ↓
业务逻辑
```

### 现有模式数据流
```
外部 Debezium 服务
    ↓
DebeziumClient (Simple/Cluster/Kafka/等)
    ↓
ChangeEventHandler/RecordChangeEventHandler
    ↓
EntryHandler<T> (业务处理)
    ↓
业务逻辑
```

## 配置说明

### Embedded 模式配置

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| `debezium.embedded.enabled` | 启用 Embedded 模式 | false |
| `debezium.embedded.destination` | 连接器名称 | - |
| `debezium.embedded.connector-class` | 连接器类名 | - |
| `debezium.embedded.host` | 数据库主机 | - |
| `debezium.embedded.port` | 数据库端口 | - |
| `debezium.embedded.username` | 数据库用户名 | - |
| `debezium.embedded.password` | 数据库密码 | - |
| `debezium.embedded.database-include-list` | 包含的数据库 | - |
| `debezium.embedded.table-include-list` | 包含的表 | - |

### 线程池配置

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| `debezium.thread-pool.core-pool-size` | 核心线程数 | 1 |
| `debezium.thread-pool.max-pool-size` | 最大线程数 | CPU核心数 |
| `debezium.thread-pool.queue-capacity` | 队列容量 | Integer.MAX_VALUE |
| `debezium.thread-pool.keep-alive` | 线程保活时间 | 60s |

### Offset 存储配置

#### File 存储
| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| `debezium.offset-storage.file.file-name` | 文件路径 | /tmp/offsets.dat |
| `debezium.offset-storage.file.flush-interval-ms` | 刷新间隔 | 60000 |

#### Kafka 存储
| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| `debezium.offset-storage.kafka.bootstrap-servers` | Kafka 服务器 | - |
| `debezium.offset-storage.kafka.topic` | 主题名称 | debezium-offsets |
| `debezium.offset-storage.kafka.partitions` | 分区数 | 1 |
| `debezium.offset-storage.kafka.replication-factor` | 副本因子 | 1 |

#### JDBC 存储
| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| `debezium.offset-storage.jdbc.url` | 数据库连接URL | - |
| `debezium.offset-storage.jdbc.username` | 数据库用户名 | - |
| `debezium.offset-storage.jdbc.password` | 数据库密码 | - |
| `debezium.offset-storage.jdbc.driver-class-name` | 驱动类名 | com.mysql.cj.jdbc.Driver |
| `debezium.offset-storage.jdbc.table-name` | 表名 | debezium_offsets |

#### Redis 存储
| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| `debezium.offset-storage.redis.host` | Redis 主机 | localhost |
| `debezium.offset-storage.redis.port` | Redis 端口 | 6379 |
| `debezium.offset-storage.redis.password` | Redis 密码 | - |
| `debezium.offset-storage.redis.database` | 数据库索引 | 0 |
| `debezium.offset-storage.redis.key-prefix` | 键前缀 | debezium:offsets: |

#### S3 存储
| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| `debezium.offset-storage.s3.bucket-name` | 存储桶名称 | - |
| `debezium.offset-storage.s3.region` | 区域 | - |
| `debezium.offset-storage.s3.access-key-id` | 访问密钥ID | - |
| `debezium.offset-storage.s3.secret-access-key` | 访问密钥 | - |
| `debezium.offset-storage.s3.endpoint` | 端点 | - |
| `debezium.offset-storage.s3.key-prefix` | 键前缀 | debezium/offsets/ |

## 扩展开发

### 自定义 Offset 存储

1. 实现 `OffsetBackingStore` 接口
2. 创建配置器实现 `OffsetStorageConfigurer`
3. 在 `OffsetStorageConfigurerFactory` 中注册

```java
public class CustomOffsetStorageConfigurer implements OffsetStorageConfigurer {
    @Override
    public void apply(Configuration.Builder builder, DebeziumOffsetStorageProperties properties) {
        // 配置自定义存储
        builder.with("offset.storage", "com.example.CustomOffsetBackingStore");
        // 添加自定义配置
    }
}
```

### 自定义事件处理器

实现 `RecordChangeEventHandler` 接口：

```java
@Component
public class CustomRecordChangeEventHandler implements RecordChangeEventHandler {
    @Override
    public void handleEvent(List<RecordChangeEvent<SourceRecord>> events,
                           DebeziumEngine.RecordCommitter<RecordChangeEvent<SourceRecord>> committer,
                           Properties props) {
        // 自定义事件处理逻辑
    }
}
```

## 使用场景

### Embedded 模式适用场景
- 直接连接数据库，无需外部 Debezium 服务
- 需要自定义 Offset 存储策略
- 单机或小规模部署
- 开发和测试环境

### 现有模式适用场景
- 大规模分布式部署
- 已有 Debezium 服务基础设施
- 需要消息队列作为数据源
- 生产环境

## 注意事项

1. 确保数据库开启了 binlog
2. 配置正确的数据库权限
3. 选择合适的 Offset 存储类型
4. 合理配置线程池参数
5. 监控 Offset 存储的可用性
6. Embedded 模式需要直接连接数据库，确保网络连通性

## 许可证

Apache License 2.0

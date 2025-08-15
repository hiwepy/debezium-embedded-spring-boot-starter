# Connector 配置器重构总结

## 概述

本次重构统一了所有 connector 配置器的实现方式，使用 Spring Boot 的 `PropertyMapper` 进行属性设置，并对字符串类型使用 `.whenHasText()` 判断，提高了代码的一致性和可维护性。

## 修改的 Connector 配置器

### 1. MySqlConnectorConfigurer
- ✅ 已修改：使用 `PropertyMapper` 替换传统的 `if` 判断
- ✅ 字符串类型使用 `.whenHasText()` 判断
- ✅ 数值类型使用 `.to()` 直接映射

### 2. PostgreSqlConnectorConfigurer
- ✅ 已修改：使用 `PropertyMapper` 替换传统的 `if` 判断
- ✅ 字符串类型使用 `.whenHasText()` 判断
- ✅ 数值类型使用 `.to()` 直接映射

### 3. OracleConnectorConfigurer
- ✅ 已修改：使用 `PropertyMapper` 替换传统的 `if` 判断
- ✅ 字符串类型使用 `.whenHasText()` 判断
- ✅ 数值类型使用 `.to()` 直接映射

### 4. SqlServerConnectorConfigurer
- ✅ 已修改：使用 `PropertyMapper` 替换传统的 `if` 判断
- ✅ 字符串类型使用 `.whenHasText()` 判断
- ✅ 数值类型使用 `.to()` 直接映射

### 5. MariaDbConnectorConfigurer
- ✅ 已修改：使用 `PropertyMapper` 替换传统的 `if` 判断
- ✅ 字符串类型使用 `.whenHasText()` 判断
- ✅ 数值类型使用 `.to()` 直接映射

### 6. Db2ConnectorConfigurer
- ✅ 已修改：使用 `PropertyMapper` 替换传统的 `if` 判断
- ✅ 字符串类型使用 `.whenHasText()` 判断

### 7. InformixConnectorConfigurer
- ✅ 已修改：使用 `PropertyMapper` 替换传统的 `if` 判断
- ✅ 字符串类型使用 `.whenHasText()` 判断

### 8. SpannerConnectorConfigurer
- ✅ 已修改：使用 `PropertyMapper` 替换传统的 `if` 判断
- ✅ 字符串类型使用 `.whenHasText()` 判断
- ✅ 已修复：使用正确的 `Spanner` 配置类而不是 `MongoDb`

### 9. VitessConnectorConfigurer
- ✅ 已修改：使用 `PropertyMapper` 替换传统的 `if` 判断
- ✅ 字符串类型使用 `.whenHasText()` 判断

### 10. CassandraConnectorConfigurer
- ✅ 已修改：使用 `PropertyMapper` 替换传统的 `if` 判断
- ✅ 字符串类型使用 `.whenHasText()` 判断
- ✅ 已修复：使用正确的 `Cassandra` 配置类而不是 `MongoDb`

### 11. CustomConnectorConfigurer
- ✅ 已修改：使用 `PropertyMapper` 替换传统的 `if` 判断
- ✅ 字符串类型使用 `.whenHasText()` 判断

### 12. MongoDbConnectorConfigurer
- ✅ 已符合要求：已经使用 `PropertyMapper` 和 `.whenHasText()` 判断

## 统一的实现模式

所有 connector 配置器现在都遵循以下统一的实现模式：

```java
@Override
public void apply(Configuration.Builder builder, DebeziumConnectorProperties properties) {
    // 1. 设置基本连接信息
    builder.with("connector.class", "io.debezium.connector.xxx.XxxConnector")
           .with("database.hostname", properties.getHost())
           .with("database.port", properties.getPort());
           // ... 其他基本配置

    /*
     * 批量设置参数
     */
    PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
    
    // 2. 数据库和表过滤（字符串类型使用 whenHasText）
    map.from(properties::getDatabaseIncludeList).whenHasText().to(value -> builder.with("", value));
    map.from(properties::getTableIncludeList).whenHasText().to(value -> builder.with("table.include.list", value));
    map.from(properties::getSchemaIncludeList).whenHasText().to(value -> builder.with("schema.include.list", value));

    // 3. 特定数据库配置
    if (properties.getXxx() != null) {
        DebeziumConnectorProperties.Xxx xxx = properties.getXxx();
        
        // 字符串类型使用 whenHasText
        map.from(xxx::getSomeStringProperty).whenHasText().to(value -> builder.with("some.string.property", value));
        
        // 数值类型直接映射
        map.from(xxx::getSomeNumericProperty).to(value -> builder.with("some.numeric.property", value));
    }
}
```

## 优势

1. **代码一致性**：所有 connector 使用相同的属性设置模式
2. **空值安全**：`.whenHasText()` 确保只有非空字符串才会被设置
3. **类型安全**：`PropertyMapper` 提供类型安全的属性映射
4. **可维护性**：统一的代码风格便于维护和扩展
5. **性能优化**：避免不必要的空值检查和字符串操作

## 注意事项

- 字符串类型的属性必须使用 `.whenHasText()` 判断
- 数值类型的属性可以直接使用 `.to()` 映射
- 布尔类型的属性可以直接使用 `.to()` 映射
- 集合类型的属性需要特殊处理（如果有的话）

## 重要修复

### 配置类错误使用问题

在重构过程中发现了一个重要问题：`SpannerConnectorConfigurer` 和 `CassandraConnectorConfigurer` 错误地使用了 `DebeziumConnectorProperties.MongoDb` 配置类。

**问题描述：**
- `SpannerConnectorConfigurer` 使用了 `properties.getMongoDb()` 而不是专用的 Spanner 配置
- `CassandraConnectorConfigurer` 使用了 `properties.getMongoDb()` 而不是专用的 Cassandra 配置

**解决方案：**
1. 在 `DebeziumConnectorProperties` 中添加了 `Cassandra` 和 `Spanner` 专用配置类
2. 修复了 `SpannerConnectorConfigurer` 使用 `properties.getSpanner()`
3. 修复了 `CassandraConnectorConfigurer` 使用 `properties.getCassandra()`

**新增配置类：**

```java
@Data
public static class Cassandra {
    private String connectionString;
    private String databaseList;  // keyspace
    private String tableList;
    private String snapshotMode = "initial";
    private Integer connectTimeoutMs = 30000;
    private Integer readTimeoutMs = 30000;
    // ... 其他配置
}

@Data
public static class Spanner {
    private String connectionString;
    private String databaseList;
    private String tableList;
    private String snapshotMode = "initial";
    private String projectId;
    private String instanceId;
    private String databaseId;
    // ... 其他配置
}
```

## 多实例支持

### 架构改进

为了支持同时监听多个数据库实例，我们对架构进行了以下改进：

1. **DebeziumEmbeddedManager**：新增管理器类，负责管理多个数据库实例
2. **DebeziumEmbeddedRunner**：重构为单个实例的运行器
3. **配置结构优化**：支持在 `DebeziumProperties` 中配置多个实例

### 核心特性

- **多实例支持**：可以同时监听多个不同的数据库实例
- **配置隔离**：每个实例的配置完全独立
- **统一管理**：通过 `DebeziumEmbeddedManager` 统一管理所有实例
- **共享资源**：所有实例共享同一个线程池，提高资源利用率

### 配置示例

```yaml
debezium:
  embedded:
    enabled: true
    async: true
  instances:
    - connector:
        type: MYSQL
        destination: mysql-instance-1
        host: localhost
        port: 3306
        # ... 其他配置
      offset-storage:
        type: FILE
        file:
          fileName: /tmp/offsets-mysql-1.dat
    - connector:
        type: POSTGRESQL
        destination: postgresql-instance
        host: localhost
        port: 5432
        # ... 其他配置
      offset-storage:
        type: KAFKA
        kafka:
          bootstrapServers: localhost:9092
```

## 测试建议

建议对每个 connector 进行以下测试：

1. 空值测试：确保空字符串不会被设置到配置中
2. 正常值测试：确保有效值能正确设置
3. 边界值测试：测试各种边界情况
4. 集成测试：确保整个配置流程正常工作
5. **配置类测试**：确保每个 connector 使用正确的配置类
6. **多实例测试**：测试多个实例同时运行的情况
7. **实例隔离测试**：确保实例间配置完全隔离

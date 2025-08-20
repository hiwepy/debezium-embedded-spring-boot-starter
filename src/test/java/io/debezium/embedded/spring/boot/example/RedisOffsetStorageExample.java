package io.debezium.embedded.spring.boot.example;

import io.debezium.embedded.spring.boot.DebeziumEmbeddedAutoConfiguration;
import io.debezium.embedded.spring.boot.DebeziumOffsetStorageProperties;
import io.debezium.embedded.storage.RedisOffsetStorageConfigurer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Redis Offset 存储示例
 * 
 * <p>该示例展示了如何使用 Redis 作为 Debezium 的 Offset 存储。</p>
 * 
 * <p>配置说明：</p>
 * <ul>
 *   <li>Offset Store: 使用 Redis 存储连接器的偏移量信息</li>
 *   <li>支持 SSL/TLS 连接</li>
 *   <li>支持 Redis 6.0+ ACL 认证</li>
 *   <li>支持连接池和重试机制</li>
 * </ul>
 * 
 * <p>注意：Schema History 配置现在在 {@link io.debezium.embedded.history.RedisSchemaHistoryConfigurer} 中处理</p>
 * 
 * @see <a href="https://debezium.io/documentation/reference/3.2/configuration/storage.html">Debezium Storage Configuration</a>
 */
@SpringBootApplication
public class RedisOffsetStorageExample {

    public static void main(String[] args) {
        SpringApplication.run(RedisOffsetStorageExample.class, args);
    }

    /**
     * 配置 Redis Offset 存储属性
     * 
     * <p>该配置严格按照 Debezium 官方文档中的 Redis 配置参数进行设置，包括：</p>
     * <ul>
     *   <li>Offset Store 配置（address, database, password 等）</li>
     *   <li>SSL/TLS 安全配置</li>
     *   <li>连接池和超时配置</li>
     *   <li>重试和等待配置</li>
     * </ul>
     */
    @Bean
    public DebeziumOffsetStorageProperties redisOffsetStorageProperties() {
        DebeziumOffsetStorageProperties properties = new DebeziumOffsetStorageProperties();
        
        // 设置存储类型为 Redis
        properties.setType(io.debezium.embedded.storage.OffsetStorageType.REDIS);
        
        // 配置 Redis 连接参数
        DebeziumOffsetStorageProperties.Redis redis = properties.getRedis();
        
        // ==================== Offset Store 配置 ====================
        // 基本连接配置
        redis.setAddress("localhost:6379");
        redis.setDatabase(0);
        redis.setPassword("");  // 如果 Redis 设置了密码，请填写
        redis.setUsername("");  // Redis 6.0+ ACL 支持
        redis.setClientName("debezium-client");
        
        // 超时和连接池配置
        redis.setConnectionTimeout(30000);
        redis.setReadTimeout(30000);
        redis.setPoolSize(10);
        
        // 重试配置
        redis.setMaxRetries(3);
        redis.setRetryDelayMs(1000);
        
        // 刷新配置
        redis.setFlushIntervalMs(60000);
        
        // SSL/TLS 配置（生产环境建议启用）
        redis.setSsl(false);
        redis.setSslCertPath("");
        redis.setSslKeyPath("");
        redis.setSslCaPath("");
        
        return properties;
    }

    /**
     * 配置 Redis Offset 存储配置器
     * 
     * <p>该配置器负责将 Spring Boot 配置属性转换为 Debezium 配置，
     * 严格按照官方文档中的参数名称进行映射。</p>
     */
    @Bean
    public RedisOffsetStorageConfigurer redisOffsetStorageConfigurer() {
        return new RedisOffsetStorageConfigurer();
    }
}

package io.debezium.embedded.spring.boot.example;

import io.debezium.embedded.history.RedisSchemaHistoryConfigurer;
import io.debezium.embedded.spring.boot.DebeziumSchemaHistoryProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Redis Schema History 存储示例
 * 
 * <p>该示例展示了如何使用 Redis 作为 Debezium 的 Schema History 存储。</p>
 * 
 * <p>配置说明：</p>
 * <ul>
 *   <li>Schema History: 使用 Redis 存储数据库模式变更历史</li>
 *   <li>支持 SSL/TLS 连接</li>
 *   <li>支持 Redis 6.0+ ACL 认证</li>
 *   <li>支持连接池和重试机制</li>
 *   <li>支持等待和重试配置</li>
 * </ul>
 * 
 * @see <a href="https://debezium.io/documentation/reference/3.2/configuration/storage.html">Debezium Storage Configuration</a>
 */
@SpringBootApplication
public class RedisSchemaHistoryExample {

    public static void main(String[] args) {
        SpringApplication.run(RedisSchemaHistoryExample.class, args);
    }

    /**
     * 配置 Redis Schema History 存储属性
     * 
     * <p>该配置严格按照 Debezium 官方文档中的 Redis Schema History 配置参数进行设置，包括：</p>
     * <ul>
     *   <li>Schema History 配置（address, database, key.prefix 等）</li>
     *   <li>SSL/TLS 安全配置</li>
     *   <li>连接池和超时配置</li>
     *   <li>重试和等待配置</li>
     * </ul>
     */
    @Bean
    public DebeziumSchemaHistoryProperties redisSchemaHistoryProperties() {
        DebeziumSchemaHistoryProperties properties = new DebeziumSchemaHistoryProperties();
        
        // 设置历史记录类型为 Redis
        properties.setType(io.debezium.embedded.history.DatabaseHistoryType.REDIS);
        
        // 配置 Redis 连接参数
        DebeziumSchemaHistoryProperties.Redis redis = properties.getRedis();
        
        // ==================== Schema History 配置 ====================
        // 基本连接配置
        redis.setAddress("localhost:6379");
        redis.setDatabase(1);  // 使用不同的数据库
        redis.setKey("debezium:schema-history:");  // 必须显式配置，无默认值
        redis.setPassword("");  // 如果 Redis 设置了密码，请填写
        redis.setUsername("");  // Redis 6.0+ ACL 支持
        redis.setClientName("debezium-schema-history-client");
        
        // 超时配置
        redis.setConnectionTimeout(2000);
        redis.setSocketTimeout(2000);
        
        // 重试配置
        redis.setRetryInitialDelay(300);
        redis.setRetryMaxDelay(10000);
        redis.setRetryMaxAttempts(10);
        
        // 等待配置
        redis.setWaitEnabled(false);
        redis.setWaitTimeout(1000);
        redis.setWaitRetryEnabled(false);
        redis.setWaitRetryDelay(1000);
        
        // SSL/TLS 配置（生产环境建议启用）
        redis.setSsl(false);
        redis.setSslCertPath("");
        redis.setSslKeyPath("");
        redis.setSslCaPath("");
        redis.setSslKeystorePath("");
        redis.setSslKeystorePassword("");
        redis.setSslKeystoreType("JKS");
        
        return properties;
    }

    /**
     * 配置 Redis Schema History 配置器
     * 
     * <p>该配置器负责将 Spring Boot 配置属性转换为 Debezium 配置，
     * 严格按照官方文档中的参数名称进行映射。</p>
     */
    @Bean
    public RedisSchemaHistoryConfigurer redisSchemaHistoryConfigurer() {
        return new RedisSchemaHistoryConfigurer();
    }
}

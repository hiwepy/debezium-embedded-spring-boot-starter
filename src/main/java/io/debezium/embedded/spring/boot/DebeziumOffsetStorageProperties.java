package io.debezium.embedded.spring.boot;

import io.debezium.embedded.configurer.storage.OffsetStorageType;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * Debezium 偏移量存储配置属性类
 * 
 * <p>该类用于配置 Debezium 连接器的偏移量存储方式。偏移量存储用于记录连接器读取数据库变更日志的位置，
 * 确保在连接器重启后能够从正确的位置继续读取，避免重复处理或丢失数据。</p>
 * 
 * <p>支持的存储类型包括：</p>
 * <ul>
 *   <li><strong>FILE</strong> - 文件存储，将偏移量保存在本地文件中</li>
 *   <li><strong>KAFKA</strong> - Kafka 存储，将偏移量保存在 Kafka 主题中</li>
 *   <li><strong>JDBC</strong> - 数据库存储，将偏移量保存在关系型数据库中</li>
 *   <li><strong>REDIS</strong> - Redis 存储，将偏移量保存在 Redis 中</li>
 *   <li><strong>CUSTOM</strong> - 自定义存储，使用自定义实现</li>
 * </ul>
 *
 * @since 1.0.0
 */
@Data
public class DebeziumOffsetStorageProperties {

    /**
     * 偏移量存储类型
     * 
     * <p>指定使用哪种存储方式来保存 Debezium 连接器的偏移量信息。</p>
     * <p>默认值：FILE</p>
     * <p>可选值：FILE, KAFKA, JDBC, REDIS, CUSTOM</p>
     */
    private OffsetStorageType type = OffsetStorageType.MEMORY;

    /**
     * 文件存储配置
     * 
     * <p>当 type 设置为 FILE 时生效，用于配置本地文件存储偏移量的相关参数。</p>
     */
    private File file = new File();
    
    /**
     * Kafka 存储配置
     * 
     * <p>当 type 设置为 KAFKA 时生效，用于配置 Kafka 主题存储偏移量的相关参数。</p>
     */
    private Kafka kafka = new Kafka();

    /**
     * 自定义存储配置
     * 
     * <p>当 type 设置为 CUSTOM 时生效，用于配置自定义偏移量存储的相关参数。</p>
     */
    private Custom custom = new Custom();

    /**
     * 文件存储配置类
     * 
     * <p>用于配置本地文件存储偏移量的相关参数。文件存储是最简单和常用的存储方式，
     * 适合单机部署或不需要高可用性的场景。</p>
     */
    @Data
    public static class File {
        /**
         * 偏移量文件路径
         * 
         * <p>指定存储偏移量信息的文件路径。文件将包含连接器读取数据库变更日志的位置信息。</p>
         * <p>默认值：/tmp/offsets.dat</p>
         * <p>建议使用绝对路径，确保应用有读写权限。</p>
         */
        private String fileName = "/tmp/offsets.dat";
        
        /**
         * 刷新间隔时间（毫秒）
         * 
         * <p>指定将偏移量信息刷新到文件的最大时间间隔。较小的值可以提高数据安全性，
         * 但会增加 I/O 操作频率。</p>
         * <p>默认值：60000（60秒）</p>
         * <p>建议根据数据重要性和性能要求进行调整。</p>
         */
        private Integer flushIntervalMs = 60_000;
        
        /**
         * 刷新超时时间（毫秒）
         * 
         * <p>指定偏移量提交完成的最大等待时间。如果在此时间内无法完成提交，
         * 将抛出异常。</p>
         * <p>默认值：null（无限制）</p>
         * <p>建议在生产环境中设置合理的超时时间，避免长时间阻塞。</p>
         */
        private Integer flushTimeoutMs = 5000;

    }

    /**
     * Kafka 存储配置类
     * 
     * <p>用于配置 Kafka 主题存储偏移量的相关参数。Kafka 存储适合分布式部署和高可用性场景，
     * 支持多实例共享偏移量信息。</p>
     */
    @Data
    public static class Kafka {

        /**
         * 偏移量主题名称
         *
         * <p>指定用于存储偏移量信息的 Kafka 主题名称。</p>
         * <p>默认值：debezium-offsets</p>
         * <p>如果主题不存在，将自动创建。</p>
         */
        private String topic = "debezium-offsets";

        /**
         * 主题分区数
         *
         * <p>指定偏移量主题的分区数量。仅在主题不存在时生效。</p>
         * <p>默认值：25</p>
         * <p>建议根据并发需求设置合适的分区数。</p>
         */
        private Integer partitions = 25;

        /**
         * 副本因子
         *
         * <p>指定偏移量主题的副本数量。仅在主题不存在时生效。</p>
         * <p>默认值：3</p>
         * <p>建议在生产环境中设置为 3 或更高，确保高可用性。</p>
         */
        private Integer replicationFactor = 3;



        /**
         * 生产者配置
         * 
         * <p>用于配置 Kafka 生产者的相关参数，控制偏移量数据的发送行为。</p>
         */
        private Producer producer = new Producer();
        
        /**
         * 消费者配置
         * 
         * <p>用于配置 Kafka 消费者的相关参数，控制偏移量数据的读取行为。</p>
         */
        private Consumer consumer = new Consumer();
        
        /**
         * 安全配置
         * 
         * <p>用于配置 Kafka 连接的安全相关参数，如 SSL、SASL 等。</p>
         */
        private Security security = new Security();
        
        /**
         * Kafka 生产者配置类
         * 
         * <p>用于配置向 Kafka 主题发送偏移量数据时的生产者参数。</p>
         */
        @Data
        public static class Producer {
            /**
             * 确认机制
             * 
             * <p>指定生产者发送消息的确认机制。</p>
             * <p>可选值：</p>
             * <ul>
             *   <li>0 - 不等待确认</li>
             *   <li>1 - 等待 leader 确认</li>
             *   <li>all - 等待所有副本确认</li>
             * </ul>
             * <p>默认值：all</p>
             * <p>建议使用 "all" 确保数据安全性。</p>
             */
            private String acks = "all";
            
            /**
             * 重试次数
             * 
             * <p>指定发送失败时的重试次数。</p>
             * <p>默认值：3</p>
             * <p>建议根据网络环境调整。</p>
             */
            private Integer retries = 3;
            
            /**
             * 批次大小（字节）
             * 
             * <p>指定生产者批量发送消息的大小。</p>
             * <p>默认值：16384（16KB）</p>
             * <p>较大的值可以提高吞吐量，但会增加延迟。</p>
             */
            private Integer batchSize = 16384;
            
            /**
             * 延迟时间（毫秒）
             * 
             * <p>指定生产者等待更多消息加入批次的时间。</p>
             * <p>默认值：1</p>
             * <p>较大的值可以提高吞吐量，但会增加延迟。</p>
             */
            private Integer lingerMs = 1;
            
            /**
             * 缓冲区大小（字节）
             * 
             * <p>指定生产者用于缓存未发送消息的缓冲区大小。</p>
             * <p>默认值：33554432（32MB）</p>
             * <p>建议根据内存情况调整。</p>
             */
            private Integer bufferMemory = 33554432;
            
            /**
             * 压缩类型
             * 
             * <p>指定消息压缩的类型。</p>
             * <p>可选值：none, gzip, snappy, lz4, zstd</p>
             * <p>默认值：gzip</p>
             * <p>压缩可以减少网络传输量，但会增加 CPU 使用率。</p>
             */
            private String compressionType = "gzip";
            
            /**
             * 最大请求大小（字节）
             * 
             * <p>指定单个请求的最大大小。</p>
             * <p>默认值：1048576（1MB）</p>
             * <p>必须小于等于 broker 的 message.max.bytes 配置。</p>
             */
            private Integer maxRequestSize = 1048576;
            
            /**
             * 请求超时时间（毫秒）
             * 
             * <p>指定生产者等待响应的超时时间。</p>
             * <p>默认值：30000（30秒）</p>
             * <p>建议根据网络延迟调整。</p>
             */
            private Integer requestTimeoutMs = 30000;
            
            /**
             * 元数据获取超时时间（毫秒）
             * 
             * <p>指定获取元数据的超时时间。</p>
             * <p>默认值：300000（5分钟）</p>
             * <p>影响主题发现和分区信息更新。</p>
             */
            private Integer metadataMaxAgeMs = 300000;
            
            /**
             * 连接最大空闲时间（毫秒）
             * 
             * <p>指定连接的最大空闲时间。</p>
             * <p>默认值：540000（9分钟）</p>
             * <p>超过此时间的空闲连接将被关闭。</p>
             */
            private Integer connectionsMaxIdleMs = 540000;
            
            /**
             * 重连退避时间（毫秒）
             * 
             * <p>指定重连时的退避时间。</p>
             * <p>默认值：50</p>
             * <p>用于避免频繁重连对服务器造成压力。</p>
             */
            private Integer reconnectBackoffMs = 50;
            
            /**
             * 重试退避时间（毫秒）
             * 
             * <p>指定重试时的退避时间。</p>
             * <p>默认值：100</p>
             * <p>用于避免频繁重试对服务器造成压力。</p>
             */
            private Integer retryBackoffMs = 100;
        }
        
        /**
         * Kafka 消费者配置类
         * 
         * <p>用于配置从 Kafka 主题读取偏移量数据时的消费者参数。</p>
         */
        @Data
        public static class Consumer {
            /**
             * 自动偏移量重置策略
             * 
             * <p>指定当消费者组没有已提交的偏移量时的行为。</p>
             * <p>可选值：</p>
             * <ul>
             *   <li>earliest - 从最早的消息开始读取</li>
             *   <li>latest - 从最新的消息开始读取</li>
             *   <li>none - 如果没有偏移量则抛出异常</li>
             * </ul>
             * <p>默认值：earliest</p>
             * <p>建议使用 "earliest" 确保不丢失数据。</p>
             */
            private String autoOffsetReset = "earliest";
            
            /**
             * 启用自动提交
             * 
             * <p>指定是否启用自动提交偏移量。</p>
             * <p>默认值：false</p>
             * <p>建议禁用自动提交，手动控制偏移量提交时机。</p>
             */
            private Boolean enableAutoCommit = false;
            
            /**
             * 会话超时时间（毫秒）
             * 
             * <p>指定消费者会话的超时时间。</p>
             * <p>默认值：30000（30秒）</p>
             * <p>影响消费者组的重新平衡。</p>
             */
            private Integer sessionTimeoutMs = 30000;
            
            /**
             * 心跳间隔（毫秒）
             * 
             * <p>指定发送心跳的间隔时间。</p>
             * <p>默认值：3000（3秒）</p>
             * <p>必须小于 sessionTimeoutMs 的三分之一。</p>
             */
            private Integer heartbeatIntervalMs = 3000;
            
            /**
             * 最大轮询记录数
             * 
             * <p>指定单次轮询返回的最大记录数。</p>
             * <p>默认值：500</p>
             * <p>影响内存使用和响应时间。</p>
             */
            private Integer maxPollRecords = 500;
            
            /**
             * 最大轮询间隔（毫秒）
             * 
             * <p>指定两次轮询之间的最大间隔时间。</p>
             * <p>默认值：300000（5分钟）</p>
             * <p>超过此时间将触发重新平衡。</p>
             */
            private Integer maxPollIntervalMs = 300000;
            
            /**
             * 请求超时时间（毫秒）
             * 
             * <p>指定消费者请求的超时时间。</p>
             * <p>默认值：30000（30秒）</p>
             * <p>建议根据网络延迟调整。</p>
             */
            private Integer requestTimeoutMs = 30000;
            
            /**
             * 获取最小字节数
             * 
             * <p>指定服务器返回数据的最小字节数。</p>
             * <p>默认值：1</p>
             * <p>影响网络效率。</p>
             */
            private Integer fetchMinBytes = 1;
            
            /**
             * 获取最大等待时间（毫秒）
             * 
             * <p>指定等待数据到达的最大时间。</p>
             * <p>默认值：500</p>
             * <p>影响响应延迟。</p>
             */
            private Integer fetchMaxWaitMs = 500;
            
            /**
             * 连接最大空闲时间（毫秒）
             * 
             * <p>指定连接的最大空闲时间。</p>
             * <p>默认值：540000（9分钟）</p>
             * <p>超过此时间的空闲连接将被关闭。</p>
             */
            private Integer connectionsMaxIdleMs = 540000;
            
            /**
             * 重连退避时间（毫秒）
             * 
             * <p>指定重连时的退避时间。</p>
             * <p>默认值：50</p>
             * <p>用于避免频繁重连对服务器造成压力。</p>
             */
            private Integer reconnectBackoffMs = 50;
            
            /**
             * 重试退避时间（毫秒）
             * 
             * <p>指定重试时的退避时间。</p>
             * <p>默认值：100</p>
             * <p>用于避免频繁重试对服务器造成压力。</p>
             */
            private Integer retryBackoffMs = 100;
        }
        
        /**
         * Kafka 安全配置类
         * 
         * <p>用于配置 Kafka 连接的安全相关参数，支持 SSL 和 SASL 认证。</p>
         */
        @Data
        public static class Security {
            /**
             * 安全协议
             * 
             * <p>指定与 Kafka 通信的安全协议。</p>
             * <p>可选值：</p>
             * <ul>
             *   <li>PLAINTEXT - 明文传输</li>
             *   <li>SSL - SSL/TLS 加密</li>
             *   <li>SASL_PLAINTEXT - SASL 认证，明文传输</li>
             *   <li>SASL_SSL - SASL 认证，SSL 加密</li>
             * </ul>
             * <p>默认值：PLAINTEXT</p>
             * <p>生产环境建议使用 SSL 或 SASL_SSL。</p>
             */
            private String securityProtocol = "PLAINTEXT";
            
            /**
             * SASL 机制
             * 
             * <p>指定 SASL 认证的机制。</p>
             * <p>可选值：PLAIN, SCRAM-SHA-256, SCRAM-SHA-512, OAUTHBEARER</p>
             * <p>仅在启用 SASL 时生效。</p>
             */
            private String saslMechanism;
            
            /**
             * SASL 用户名
             * 
             * <p>指定 SASL 认证的用户名。</p>
             * <p>仅在启用 SASL 时生效。</p>
             */
            private String saslUsername;
            
            /**
             * SASL 密码
             * 
             * <p>指定 SASL 认证的密码。</p>
             * <p>仅在启用 SASL 时生效。</p>
             */
            private String saslPassword;
            
            /**
             * SSL 信任库位置
             * 
             * <p>指定 SSL 信任库文件的路径。</p>
             * <p>仅在启用 SSL 时生效。</p>
             */
            private String sslTruststoreLocation;
            
            /**
             * SSL 信任库密码
             * 
             * <p>指定 SSL 信任库的密码。</p>
             * <p>仅在启用 SSL 时生效。</p>
             */
            private String sslTruststorePassword;
            
            /**
             * SSL 密钥库位置
             * 
             * <p>指定 SSL 密钥库文件的路径。</p>
             * <p>仅在启用 SSL 客户端认证时生效。</p>
             */
            private String sslKeystoreLocation;
            
            /**
             * SSL 密钥库密码
             * 
             * <p>指定 SSL 密钥库的密码。</p>
             * <p>仅在启用 SSL 客户端认证时生效。</p>
             */
            private String sslKeystorePassword;
            
            /**
             * SSL 密钥密码
             * 
             * <p>指定 SSL 私钥的密码。</p>
             * <p>仅在启用 SSL 客户端认证时生效。</p>
             */
            private String sslKeyPassword;
            
            /**
             * SSL 端点识别算法
             * 
             * <p>指定 SSL 端点识别算法。</p>
             * <p>可选值：https, none</p>
             * <p>默认值：https</p>
             * <p>用于验证服务器主机名。</p>
             */
            private String sslEndpointIdentificationAlgorithm = "https";
        }
    }

    /**
     * 自定义存储配置类
     * 
     * <p>用于配置自定义偏移量存储的相关参数。当内置的存储类型不满足需求时，
     * 可以实现自定义的偏移量存储。</p>
     */
    @Data
    public static class Custom {
        /**
         * 自定义存储类名
         * 
         * <p>指定实现 org.apache.kafka.connect.storage.OffsetBackingStore 接口的完整类名。</p>
         * <p>必填字段，用于实例化自定义存储实现。</p>
         * <p>示例：com.example.CustomOffsetStore</p>
         */
        private String className;
        
        /**
         * 自定义属性
         * 
         * <p>指定传递给自定义存储实现的额外属性。</p>
         * <p>默认值：空 Map</p>
         * <p>用于配置自定义存储的特定参数。</p>
         */
        private Map<String, String> props = new HashMap<>();
    }
}



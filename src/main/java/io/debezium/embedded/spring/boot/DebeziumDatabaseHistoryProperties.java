package io.debezium.embedded.spring.boot;

import io.debezium.embedded.configurer.history.DatabaseHistoryType;
import lombok.Data;


/**
 * Debezium 数据库历史记录配置属性。
 */
@Data
public class DebeziumDatabaseHistoryProperties {

    
    /**
     * 历史记录类型
     */
    private DatabaseHistoryType type = DatabaseHistoryType.MEMORY;
    
    /**
     * 文件历史记录配置
     */
    private File file = new File();
    
    /**
     * Kafka 历史记录配置
     */
    private Kafka kafka = new Kafka();

    /**
     * 自定义历史记录配置
     */
    private Custom custom = new Custom();
    
    @Data
    public static class File {

        /**
         * 历史记录文件路径
         * 
         * <p>指定存储数据库模式历史记录的文件路径。</p>
         * <p>默认值：dbhistory.dat</p>
         */
        private String fileName = "dbhistory.dat";

    }
    
    @Data
    public static class Kafka {

        /**
         * Kafka 主题名称
         * 
         * <p>指定存储数据库模式历史记录的 Kafka 主题名称。</p>
         * <p>无默认值，必须显式配置。</p>
         */
        private String topic;
        
        /**
         * Kafka 服务器地址
         * 
         * <p>指定 Kafka 集群的服务器地址列表，多个地址用逗号分隔。</p>
         * <p>无默认值，必须显式配置。</p>
         */
        private String bootstrapServers;
        
        /**
         * 恢复轮询间隔（毫秒）
         * 
         * <p>指定在恢复期间轮询持久化数据的时间间隔。</p>
         * <p>默认值：100</p>
         */
        private Integer recoveryPollIntervalMs = 100;
        
        /**
         * 恢复尝试次数
         * 
         * <p>指定从 Kafka 检索模式历史数据时允许的连续失败尝试次数。</p>
         * <p>默认值：100</p>
         */
        private Integer recoveryAttempts = 100;
        
        /**
         * 查询超时时间（毫秒）
         * 
         * <p>指定 Kafka AdminClient 提交请求获取集群信息后等待响应的超时时间。</p>
         * <p>默认值：3</p>
         */
        private Integer queryTimeoutMs = 3;
        
        /**
         * 创建超时时间（毫秒）
         * 
         * <p>指定 Kafka AdminClient 提交请求创建 Kafka 历史主题后等待响应的超时时间。</p>
         * <p>默认值：30</p>
         */
        private Integer createTimeoutMs = 30;
        
        /**
         * 生产者配置
         */
        private Producer producer = new Producer();
        
        /**
         * 消费者配置
         */
        private Consumer consumer = new Consumer();
        
        @Data
        public static class Producer {
            /**
             * 确认机制
             */
            private String acks = "all";
            
            /**
             * 重试次数
             */
            private Integer retries = 3;
            
            /**
             * 批次大小
             */
            private Integer batchSize = 16384;
            
            /**
             * 延迟时间
             */
            private Integer lingerMs = 1;
            
            /**
             * 缓冲区大小
             */
            private Integer bufferMemory = 33554432;
            
            /**
             * 压缩类型
             */
            private String compressionType = "gzip";
            
            /**
             * 最大请求大小
             */
            private Integer maxRequestSize = 1048576;
            
            /**
             * 请求超时时间
             */
            private Integer requestTimeoutMs = 30000;
            
            /**
             * 元数据获取超时时间
             */
            private Integer metadataMaxAgeMs = 300000;
            
            /**
             * 连接最大空闲时间
             */
            private Integer connectionsMaxIdleMs = 540000;
            
            /**
             * 重连退避时间
             */
            private Integer reconnectBackoffMs = 50;
            
            /**
             * 重试退避时间
             */
            private Integer retryBackoffMs = 100;
        }
        
        @Data
        public static class Consumer {
            /**
             * 自动偏移量重置
             */
            private String autoOffsetReset = "earliest";
            
            /**
             * 启用自动提交
             */
            private Boolean enableAutoCommit = false;
            
            /**
             * 会话超时时间
             */
            private Integer sessionTimeoutMs = 30000;
            
            /**
             * 心跳间隔
             */
            private Integer heartbeatIntervalMs = 3000;
            
            /**
             * 最大轮询记录数
             */
            private Integer maxPollRecords = 500;
            
            /**
             * 最大轮询间隔
             */
            private Integer maxPollIntervalMs = 300000;
            
            /**
             * 请求超时时间
             */
            private Integer requestTimeoutMs = 30000;
            
            /**
             * 获取超时时间
             */
            private Integer fetchMinBytes = 1;
            
            /**
             * 获取最大等待时间
             */
            private Integer fetchMaxWaitMs = 500;
            
            /**
             * 连接最大空闲时间
             */
            private Integer connectionsMaxIdleMs = 540000;
            
            /**
             * 重连退避时间
             */
            private Integer reconnectBackoffMs = 50;
            
            /**
             * 重试退避时间
             */
            private Integer retryBackoffMs = 100;
        }
        
        /**
         * 安全配置
         */
        private Security security = new Security();
        
        @Data
        public static class Security {
            /**
             * 安全协议
             */
            private String securityProtocol = "PLAINTEXT";
            
            /**
             * SASL 机制
             */
            private String saslMechanism;
            
            /**
             * SASL 用户名
             */
            private String saslUsername;
            
            /**
             * SASL 密码
             */
            private String saslPassword;
            
            /**
             * SSL 信任库位置
             */
            private String sslTruststoreLocation;
            
            /**
             * SSL 信任库密码
             */
            private String sslTruststorePassword;
            
            /**
             * SSL 密钥库位置
             */
            private String sslKeystoreLocation;
            
            /**
             * SSL 密钥库密码
             */
            private String sslKeystorePassword;
            
            /**
             * SSL 密钥密码
             */
            private String sslKeyPassword;
            
            /**
             * SSL 端点识别算法
             */
            private String sslEndpointIdentificationAlgorithm = "https";
        }
    }

    @Data
    public static class Custom {
        /**
         * 自定义历史记录类名
         */
        private String historyClass;
        
        /**
         * 自定义配置属性
         */
        private java.util.Map<String, String> props = new java.util.HashMap<>();
    }
}

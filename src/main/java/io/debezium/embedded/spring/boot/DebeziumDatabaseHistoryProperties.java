package io.debezium.embedded.spring.boot;

import io.debezium.embedded.history.DatabaseHistoryType;
import lombok.Data;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Debezium 数据库历史记录配置属性。
 */
@Data
public class DebeziumDatabaseHistoryProperties {
    
    /**
     * 是否启用历史记录
     */
    private boolean enabled = true;
    
    /**
     * 历史记录类型
     */
    private DatabaseHistoryType type = DatabaseHistoryType.FILE;
    
    /**
     * 文件历史记录配置
     */
    private File file = new File();
    
    /**
     * Kafka 历史记录配置
     */
    private Kafka kafka = new Kafka();
    
    /**
     * JDBC 历史记录配置
     */
    private Jdbc jdbc = new Jdbc();
    
    /**
     * Redis 历史记录配置
     */
    private Redis redis = new Redis();
    
    /**
     * S3 历史记录配置
     */
    private S3 s3 = new S3();
    
    /**
     * 自定义历史记录配置
     */
    private Custom custom = new Custom();
    
    @Data
    public static class File {
        /**
         * 历史记录文件路径
         */
        private String filename = "dbhistory.dat";
        
        /**
         * 是否跳过无法解析的 DDL
         */
        private boolean skipUnparseableDdl = true;
        
        /**
         * 是否只存储监控表的 DDL
         */
        private boolean storeOnlyMonitoredTablesDdl = true;
        
        /**
         * 是否只存储捕获表的 DDL
         */
        private boolean storeOnlyCapturedTablesDdl = true;
        
        /**
         * 文件编码
         */
        private Charset encoding = StandardCharsets.UTF_8;
        
        /**
         * 是否启用文件同步
         */
        private boolean sync = true;
        
        /**
         * 写入缓冲区大小
         */
        private Integer bufferSize = 8192;
        
        /**
         * 是否启用文件锁定
         */
        private boolean fileLocking = true;
        
        /**
         * 文件锁定超时时间（毫秒）
         */
        private Integer fileLockTimeoutMs = 30000;
        
        /**
         * 是否启用文件备份
         */
        private boolean backup = true;
        
        /**
         * 备份文件后缀
         */
        private String backupSuffix = ".bak";
        
        /**
         * 最大备份文件数量
         */
        private Integer maxBackupFiles = 5;
        
        /**
         * 是否启用压缩
         */
        private boolean compression = false;
        
        /**
         * 压缩级别（1-9）
         */
        private Integer compressionLevel = 6;
    }
    
    @Data
    public static class Kafka {
        /**
         * Kafka 主题名称
         */
        private String topic = "dbhistory";
        
        /**
         * Kafka 服务器地址
         */
        private String bootstrapServers = "localhost:9092";
        
        /**
         * 恢复尝试次数
         */
        private Integer recoveryAttempts = 4;
        
        /**
         * 恢复轮询间隔（毫秒）
         */
        private Integer recoveryPollIntervalMs = 100;
        
        /**
         * 查询超时时间（毫秒）
         */
        private Integer queryTimeoutMs = 3000;
        
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
    public static class Jdbc {
        /**
         * JDBC URL
         */
        private String url;
        
        /**
         * 用户名
         */
        private String username;
        
        /**
         * 密码
         */
        private String password;
        
        /**
         * 表名
         */
        private String tableName = "database_history";
        
        /**
         * 连接池大小
         */
        private Integer poolSize = 10;
        
        /**
         * 连接超时时间
         */
        private Integer connectionTimeout = 30000;
        
        /**
         * 查询超时时间
         */
        private Integer queryTimeout = 30000;
        
        /**
         * 最大连接生命周期（毫秒）
         */
        private Integer maxConnectionLifetime = 1800000;
        
        /**
         * 连接最大空闲时间（毫秒）
         */
        private Integer maxConnectionIdleTime = 600000;
        
        /**
         * 最小连接数
         */
        private Integer minConnections = 1;
        
        /**
         * 最大连接数
         */
        private Integer maxConnections = 20;
        
        /**
         * 连接验证查询
         */
        private String connectionValidationQuery = "SELECT 1";
        
        /**
         * 连接验证超时时间（毫秒）
         */
        private Integer connectionValidationTimeout = 5000;
        
        /**
         * 是否启用连接泄漏检测
         */
        private Boolean leakDetectionThreshold = false;
        
        /**
         * 连接泄漏检测阈值（毫秒）
         */
        private Integer leakDetectionThresholdMs = 60000;
        
        /**
         * 是否启用自动提交
         */
        private Boolean autoCommit = true;
        
        /**
         * 事务隔离级别
         */
        private String transactionIsolation = "TRANSACTION_READ_COMMITTED";
        
        /**
         * 是否启用 SSL
         */
        private Boolean useSSL = false;
        
        /**
         * SSL 模式
         */
        private String sslMode = "PREFERRED";
        
        /**
         * 是否验证服务器证书
         */
        private Boolean verifyServerCertificate = true;
        
        /**
         * 是否允许公钥检索
         */
        private Boolean allowPublicKeyRetrieval = false;
        
        /**
         * 字符编码
         */
        private String characterEncoding = "UTF-8";
        
        /**
         * 时区
         */
        private String timezone = "UTC";
        
        /**
         * 最大重试次数
         */
        private Integer maxRetries = 3;
        
        /**
         * 重试延迟时间（毫秒）
         */
        private Integer retryDelayMs = 1000;
    }
    
    @Data
    public static class Redis {
        /**
         * Redis 服务器地址
         */
        private String host = "localhost";
        
        /**
         * Redis 端口
         */
        private Integer port = 6379;
        
        /**
         * 密码
         */
        private String password;
        
        /**
         * 数据库索引
         */
        private Integer database = 0;
        
        /**
         * 键前缀
         */
        private String keyPrefix = "debezium:history:";
        
        /**
         * 连接超时时间
         */
        private Integer connectionTimeout = 30000;
        
        /**
         * 读取超时时间
         */
        private Integer readTimeout = 30000;
        
        /**
         * 连接池大小
         */
        private Integer poolSize = 10;
        
        /**
         * 是否启用 SSL/TLS
         */
        private Boolean ssl = false;
        
        /**
         * SSL 证书路径
         */
        private String sslCertPath;
        
        /**
         * SSL 密钥路径
         */
        private String sslKeyPath;
        
        /**
         * SSL CA 证书路径
         */
        private String sslCaPath;
        
        /**
         * 用户名（Redis 6.0+ ACL 支持）
         */
        private String username;
        
        /**
         * 客户端名称
         */
        private String clientName;
        
        /**
         * 最大重试次数
         */
        private Integer maxRetries = 3;
        
        /**
         * 重试延迟时间（毫秒）
         */
        private Integer retryDelayMs = 1000;
    }
    
    @Data
    public static class S3 {
        /**
         * S3 存储桶名称
         */
        private String bucketName;
        
        /**
         * 区域
         */
        private String region = "us-east-1";
        
        /**
         * 访问密钥 ID
         */
        private String accessKeyId;
        
        /**
         * 秘密访问密钥
         */
        private String secretAccessKey;
        
        /**
         * 端点 URL
         */
        private String endpointUrl;
        
        /**
         * 对象键前缀
         */
        private String keyPrefix = "debezium/history/";
        
        /**
         * 连接超时时间
         */
        private Integer connectionTimeout = 30000;
        
        /**
         * 读取超时时间
         */
        private Integer readTimeout = 30000;
        
        /**
         * 最大重试次数
         */
        private Integer maxRetries = 3;
        
        /**
         * 重试延迟时间（毫秒）
         */
        private Integer retryDelayMs = 1000;
        
        /**
         * 是否启用路径样式访问
         */
        private Boolean pathStyleAccessEnabled = false;
        
        /**
         * 签名区域
         */
        private String signingRegion;
        
        /**
         * 代理主机
         */
        private String proxyHost;
        
        /**
         * 代理端口
         */
        private Integer proxyPort;
        
        /**
         * 代理用户名
         */
        private String proxyUsername;
        
        /**
         * 代理密码
         */
        private String proxyPassword;
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

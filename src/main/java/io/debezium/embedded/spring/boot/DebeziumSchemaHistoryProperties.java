package io.debezium.embedded.spring.boot;

import io.debezium.embedded.history.SchemaHistoryType;
import lombok.Data;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Debezium 数据库历史记录配置属性。
 */
@Data
public class DebeziumSchemaHistoryProperties {

    
    /**
     * 历史记录类型
     */
    private SchemaHistoryType type = SchemaHistoryType.FILE;
    
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
     * RocketMQ 历史记录配置
     */
    private RocketMq rocketMq = new RocketMq();
    
    /**
     * Azure Blob Storage 历史记录配置
     */
    private AzureBlob azureBlob = new AzureBlob();
    
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
        private String filename = "dbhistory.dat";
        
        /**
         * 是否跳过无法解析的 DDL
         * 
         * <p>指定是否跳过无法解析的 DDL 语句。</p>
         * <p>默认值：true</p>
         */
        private Boolean skipUnparseableDdl = true;
        
        /**
         * 是否只存储监控表的 DDL
         * 
         * <p>指定是否只存储被监控表的 DDL 语句。</p>
         * <p>默认值：true</p>
         */
        private Boolean storeOnlyMonitoredTablesDdl = true;
        
        /**
         * 是否只存储捕获表的 DDL
         * 
         * <p>指定是否只存储被捕获表的 DDL 语句。</p>
         * <p>默认值：true</p>
         */
        private Boolean storeOnlyCapturedTablesDdl = true;
        
        /**
         * 文件编码
         * 
         * <p>指定历史记录文件的编码格式。</p>
         * <p>默认值：UTF-8</p>
         */
        private Charset encoding = StandardCharsets.UTF_8;
        
        /**
         * 是否启用文件同步
         * 
         * <p>指定是否在写入后同步文件到磁盘。</p>
         * <p>默认值：true</p>
         */
        private Boolean sync = true;
        
        /**
         * 写入缓冲区大小
         * 
         * <p>指定写入缓冲区的大小（字节）。</p>
         * <p>默认值：8192</p>
         */
        private Integer bufferSize = 8192;
        
        /**
         * 是否启用文件锁定
         * 
         * <p>指定是否启用文件锁定机制。</p>
         * <p>默认值：true</p>
         */
        private Boolean fileLocking = true;
        
        /**
         * 文件锁定超时时间（毫秒）
         * 
         * <p>指定文件锁定的超时时间。</p>
         * <p>默认值：30000</p>
         */
        private Integer fileLockTimeoutMs = 30000;
        
        /**
         * 是否启用文件备份
         * 
         * <p>指定是否在写入前备份现有文件。</p>
         * <p>默认值：true</p>
         */
        private Boolean backup = true;
        
        /**
         * 备份文件后缀
         * 
         * <p>指定备份文件的后缀名。</p>
         * <p>默认值：.bak</p>
         */
        private String backupSuffix = ".bak";
        
        /**
         * 最大备份文件数量
         * 
         * <p>指定保留的最大备份文件数量。</p>
         * <p>默认值：5</p>
         */
        private Integer maxBackupFiles = 5;
        
        /**
         * 是否启用压缩
         * 
         * <p>指定是否启用文件压缩。</p>
         * <p>默认值：false</p>
         */
        private Boolean compression = false;
        
        /**
         * 压缩级别（1-9）
         * 
         * <p>指定压缩级别，1 为最快压缩，9 为最高压缩比。</p>
         * <p>默认值：6</p>
         */
        private Integer compressionLevel = 6;
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
         * 表 DDL 语句
         * 
         * <p>指定创建历史记录表的 DDL 语句。</p>
         * <p>无默认值，可选配置。</p>
         */
        private String tableDdl;
        
        /**
         * 表查询语句
         * 
         * <p>指定查询历史记录数据的 SQL 语句。</p>
         * <p>默认值：SELECT id, history_data, history_data_seq FROM %s ORDER BY record_insert_ts, record_insert_seq, id, history_data_seq</p>
         */
        private String tableSelect = "SELECT id, history_data, history_data_seq FROM %s ORDER BY record_insert_ts, record_insert_seq, id, history_data_seq";
        
        /**
         * 表存在检查语句
         * 
         * <p>指定检查表是否存在的 SQL 语句。</p>
         * <p>默认值：SELECT * FROM %s LIMIT 1</p>
         */
        private String tableExistSelect = "SELECT * FROM %s LIMIT 1";
        
        /**
         * 表插入语句
         * 
         * <p>指定插入历史记录数据的 SQL 语句。</p>
         * <p>默认值：INSERT INTO %s(id, history_data, history_data_seq, record_insert_ts, record_insert_seq) VALUES (?, ?, ?, ?, ?)</p>
         */
        private String tableInsert = "INSERT INTO %s(id, history_data, history_data_seq, record_insert_ts, record_insert_seq) VALUES (?, ?, ?, ?, ?)";
        
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
         * 
         * <p>指定 Redis 服务器的地址，格式为 host:port。</p>
         * <p>默认值：localhost:6379</p>
         */
        private String address = "localhost:6379";
        
        /**
         * 数据库索引
         * 
         * <p>指定使用的 Redis 数据库索引。</p>
         * <p>默认值：0</p>
         */
        private Integer database = 0;
        
        /**
         * 键
         * 
         * <p>指定存储 Schema History 时使用的键。</p>
         * <p>无默认值，必须显式配置。</p>
         */
        private String key;
        
        /**
         * 密码
         * 
         * <p>指定连接 Redis 的密码。</p>
         */
        private String password;
        
        /**
         * 用户名（Redis 6.0+ ACL 支持）
         * 
         * <p>指定连接 Redis 的用户名。</p>
         */
        private String username;
        
        /**
         * 客户端名称
         * 
         * <p>指定 Redis 客户端的名称。</p>
         */
        private String clientName;
        
        /**
         * 连接超时时间（毫秒）
         * 
         * <p>指定建立 Redis 连接的超时时间。</p>
         * <p>默认值：2000（2秒）</p>
         */
        private Integer connectionTimeout = 2000;
        
        /**
         * Socket 超时时间（毫秒）
         * 
         * <p>指定 Redis Socket 超时时间。</p>
         * <p>默认值：2000（2秒）</p>
         */
        private Integer socketTimeout = 2000;
        
        /**
         * 重试初始延迟时间（毫秒）
         * 
         * <p>指定 Redis 重试的初始延迟时间。</p>
         * <p>默认值：300（0.3秒）</p>
         */
        private Integer retryInitialDelay = 300;
        
        /**
         * 重试最大延迟时间（毫秒）
         * 
         * <p>指定 Redis 重试的最大延迟时间。</p>
         * <p>默认值：10000（10秒）</p>
         */
        private Integer retryMaxDelay = 10000;
        
        /**
         * 最大重试次数
         * 
         * <p>指定 Redis 操作的最大重试次数。</p>
         * <p>默认值：10</p>
         */
        private Integer retryMaxAttempts = 10;
        
        /**
         * 是否启用等待
         * 
         * <p>指定是否等待 Redis 写入确认。</p>
         * <p>默认值：false</p>
         */
        private Boolean waitEnabled = false;
        
        /**
         * 等待超时时间（毫秒）
         * 
         * <p>指定 Redis 等待超时时间。</p>
         * <p>默认值：1000（1秒）</p>
         */
        private Integer waitTimeout = 1000;
        
        /**
         * 是否启用等待重试
         * 
         * <p>指定是否启用等待重试。</p>
         * <p>默认值：false</p>
         */
        private Boolean waitRetryEnabled = false;
        
        /**
         * 等待重试延迟时间（毫秒）
         * 
         * <p>指定等待重试的延迟时间。</p>
         * <p>默认值：1000（1秒）</p>
         */
        private Integer waitRetryDelay = 1000;
        
        /**
         * 是否启用 SSL/TLS
         * 
         * <p>指定是否启用 SSL/TLS 连接。</p>
         * <p>默认值：false</p>
         */
        private Boolean ssl = false;
        
        /**
         * SSL 证书路径
         * 
         * <p>指定 SSL 证书文件的路径。</p>
         */
        private String sslCertPath;
        
        /**
         * SSL 密钥路径
         * 
         * <p>指定 SSL 密钥文件的路径。</p>
         */
        private String sslKeyPath;
        
        /**
         * SSL CA 证书路径
         * 
         * <p>指定 SSL CA 证书文件的路径。</p>
         */
        private String sslCaPath;
        
        /**
         * SSL 密钥库路径
         * 
         * <p>指定 SSL 密钥库文件的路径。</p>
         */
        private String sslKeystorePath;
        
        /**
         * SSL 密钥库密码
         * 
         * <p>指定 SSL 密钥库的密码。</p>
         */
        private String sslKeystorePassword;
        
        /**
         * SSL 密钥库类型
         * 
         * <p>指定 SSL 密钥库的类型。</p>
         * <p>默认值：JKS</p>
         */
        private String sslKeystoreType = "JKS";
    }
    
    @Data
    public static class S3 {
        /**
         * S3 存储桶名称
         * 
         * <p>指定存储 Schema History 的 S3 存储桶名称。</p>
         * <p>无默认值，必须显式配置。</p>
         */
        private String bucketName;
        
        /**
         * 对象名称
         * 
         * <p>指定存储桶中存储 Schema History 的对象名称。</p>
         * <p>无默认值，必须显式配置。</p>
         */
        private String objectName;
        
        /**
         * 区域名称
         * 
         * <p>指定托管 S3 存储桶的区域名称。</p>
         * <p>可选配置。</p>
         */
        private String regionName;
        
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
    
    @Data
    public static class AzureBlob {
        /**
         * Azure 存储账户连接字符串
         * 
         * <p>指定 Azure Blob 存储的连接字符串。</p>
         * <p>无默认值，必须显式配置。</p>
         */
        private String connectionString;
        
        /**
         * Azure 存储账户名称
         * 
         * <p>指定 Debezium 用于连接 Azure 的账户名称。</p>
         * <p>无默认值，必须显式配置。</p>
         */
        private String accountName;
        
        /**
         * Azure 容器名称
         * 
         * <p>指定 Debezium 存储数据的 Azure 容器名称。</p>
         * <p>无默认值，必须显式配置。</p>
         */
        private String containerName;
        
        /**
         * Azure Blob 名称
         * 
         * <p>指定 Debezium 存储数据的 Blob 名称。</p>
         * <p>无默认值，必须显式配置。</p>
         */
        private String blobName;
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

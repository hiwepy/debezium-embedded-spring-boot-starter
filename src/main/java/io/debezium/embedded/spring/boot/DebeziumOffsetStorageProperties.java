package io.debezium.embedded.spring.boot;

import io.debezium.embedded.spring.boot.storage.OffsetStorageType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "debezium.offset-storage")
@Data
public class DebeziumOffsetStorageProperties {

    private OffsetStorageType type = OffsetStorageType.FILE;

    private File file = new File();
    private Kafka kafka = new Kafka();
    private Jdbc jdbc = new Jdbc();
    private Redis redis = new Redis();
    private S3 s3 = new S3();
    private Custom custom = new Custom();

    @Data
    public static class File {
        private String fileName = "/tmp/offsets.dat";
        /**
         * An optional advanced field that specifies the maximum amount of time that the embedded connector should wait
         * for an offset commit to complete.
         * 这是一个可选高级字段，指定偏移提交完成所需的最大时间。
         */
        private Integer flushIntervalMs = 60_000;
        /**
         * An optional advanced field that specifies the maximum amount of time that the embedded connector should wait
         * for an offset commit to complete.
         */
        private Integer flushTimeoutMs;
    }

    @Data
    public static class Kafka {
        private String bootstrapServers;
        private String topic = "debezium-offsets";
        private Integer partitions = 1;
        private Integer replicationFactor = 1;
    }

    @Data
    public static class Jdbc {
        private String url;
        private String username;
        private String password;
        private String driverClassName = "com.mysql.cj.jdbc.Driver";
        private String tableName = "debezium_offsets";
        private Integer flushIntervalMs = 60_000;
    }

    @Data
    public static class Redis {
        private String host = "localhost";
        private Integer port = 6379;
        private String password;
        private Integer database = 0;
        private String keyPrefix = "debezium:offsets:";
        private Integer flushIntervalMs = 60_000;
    }

    @Data
    public static class S3 {
        private String bucketName;
        private String region;
        private String accessKeyId;
        private String secretAccessKey;
        private String endpoint;
        private String keyPrefix = "debezium/offsets/";
        private Integer flushIntervalMs = 60_000;
    }

    @Data
    public static class Custom {
        /** fully-qualified class that implements org.apache.kafka.connect.storage.OffsetBackingStore */
        private String className;
        /** additional props passed through to the custom store */
        private Map<String, String> props = new HashMap<>();
    }
}



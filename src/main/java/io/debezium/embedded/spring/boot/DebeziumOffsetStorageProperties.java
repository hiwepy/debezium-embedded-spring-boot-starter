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
     * <p>可选值：FILE, KAFKA, CUSTOM</p>
     */
    private OffsetStorageType type = OffsetStorageType.FILE;

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



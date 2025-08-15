package io.debezium.embedded.history;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumDatabaseHistoryProperties;
import org.springframework.boot.context.properties.PropertyMapper;

/**
 * 文件数据库历史记录配置器。
 */
public class FileDatabaseHistoryConfigurer implements DatabaseHistoryConfigurer {
    
    /**
     * 应用数据库历史记录配置。
     *
     * @param builder 配置构建器
     * @param properties 数据库历史记录配置属性
     */
    @Override
    public void apply(Configuration.Builder builder, DebeziumDatabaseHistoryProperties properties) {
        DebeziumDatabaseHistoryProperties.File file = properties.getFile();
        
        builder.with("database.history", "io.debezium.relational.history.FileDatabaseHistory");
        
        /*
         * 批量设置参数
         */
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        
        // 基础文件配置
        map.from(file::getFilename).whenHasText().to(value -> builder.with("database.history.file.filename", value));
        map.from(file::getEncoding).whenHasText().to(value -> builder.with("database.history.file.encoding", value));
        
        // DDL 处理配置
        map.from(file::isSkipUnparseableDdl).to(value -> builder.with("database.history.skip.unparseable.ddl", value));
        map.from(file::isStoreOnlyMonitoredTablesDdl).to(value -> builder.with("database.history.store.only.monitored.tables.ddl", value));
        map.from(file::isStoreOnlyCapturedTablesDdl).to(value -> builder.with("database.history.store.only.captured.tables.ddl", value));
        
        // 文件操作配置
        map.from(file::isSync).to(value -> builder.with("database.history.file.sync", value));
        map.from(file::getBufferSize).to(value -> builder.with("database.history.file.buffer.size", value));
        
        // 文件锁定配置
        map.from(file::isFileLocking).to(value -> builder.with("database.history.file.locking", value));
        map.from(file::getFileLockTimeoutMs).to(value -> builder.with("database.history.file.lock.timeout.ms", value));
        
        // 备份配置
        map.from(file::isBackup).to(value -> builder.with("database.history.file.backup", value));
        map.from(file::getBackupSuffix).whenHasText().to(value -> builder.with("database.history.file.backup.suffix", value));
        map.from(file::getMaxBackupFiles).to(value -> builder.with("database.history.file.max.backup.files", value));
        
        // 压缩配置
        map.from(file::isCompression).to(value -> builder.with("database.history.file.compression", value));
        map.from(file::getCompressionLevel).to(value -> builder.with("database.history.file.compression.level", value));
    }
}

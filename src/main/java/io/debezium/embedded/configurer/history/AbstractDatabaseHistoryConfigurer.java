package io.debezium.embedded.configurer.history;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumDatabaseHistoryProperties;
import io.debezium.relational.HistorizedRelationalDatabaseConnectorConfig;
import io.debezium.relational.history.AbstractDatabaseHistory;
import io.debezium.relational.history.DatabaseHistory;
import org.springframework.boot.context.properties.PropertyMapper;

public abstract class AbstractDatabaseHistoryConfigurer implements DatabaseHistoryConfigurer{

    /**
     * 应用历史记录配置到 Debezium 配置构建器。
     * @param builder Debezium 配置构建器
     * @param properties 历史记录配置属性
     */
    final public void apply(Configuration.Builder builder, DebeziumDatabaseHistoryProperties properties){

        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();

        // 基础配置
        builder.with(HistorizedRelationalDatabaseConnectorConfig.DATABASE_HISTORY.name(), this.getDatabaseHistory());
        map.from(properties::getSkipUnparseableDdl).whenHasText().to(value -> builder.with(DatabaseHistory.SKIP_UNPARSEABLE_DDL_STATEMENTS.name(), value));
        map.from(properties::getStoreOnlyCapturedTablesDdl).whenHasText().to(value -> builder.with(DatabaseHistory.STORE_ONLY_CAPTURED_TABLES_DDL, value));
        map.from(properties::getDdlFilter).whenHasText().to(value -> builder.with(DatabaseHistory.DDL_FILTER.name(), value));
        map.from(properties::getPreferDdl).whenHasText().to(value -> builder.with(AbstractDatabaseHistory.INTERNAL_PREFER_DDL.name(), value));

        this.apply(map, builder, properties);
    }

    abstract String getDatabaseHistory();
    abstract void apply(PropertyMapper map, Configuration.Builder builder, DebeziumDatabaseHistoryProperties properties) ;

}

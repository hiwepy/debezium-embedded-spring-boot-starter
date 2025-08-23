package io.debezium.embedded.configurer.connector;

import io.debezium.config.Configuration;
import io.debezium.embedded.spring.boot.DebeziumConnectorProperties;
import org.apache.kafka.connect.runtime.ConnectorConfig;
import org.springframework.boot.context.properties.PropertyMapper;

public abstract class AbstractConnectorConfigurer implements ConnectorConfigurer {

    protected static final String SCHEMA_INCLUDE_LIST_NAME = "schema.include.list";
    protected static final String SCHEMA_EXCLUDE_LIST_NAME = "schema.exclude.list";
    protected static final String DATABASE_WHITELIST_NAME = "database.whitelist";
    protected static final String DATABASE_INCLUDE_LIST_NAME = "database.include.list";
    protected static final String DATABASE_BLACKLIST_NAME = "database.blacklist";
    protected static final String DATABASE_EXCLUDE_LIST_NAME = "database.exclude.list";
    protected static final String TABLE_BLACKLIST_NAME = "table.blacklist";
    protected static final String TABLE_EXCLUDE_LIST_NAME = "table.exclude.list";
    protected static final String TABLE_WHITELIST_NAME = "table.whitelist";
    protected static final String TABLE_INCLUDE_LIST_NAME = "table.include.list";

    protected static final String DATABASE_DB_TYPE = "database.dbType";
    protected static final String DATABASE_HOSTNAME_NAME = "database.hostname";
    protected static final String DATABASE_PORT_NAME = "database.port";
    protected static final String DATABASE_USER_NAME = "database.user";
    protected static final String DATABASE_PASSWORD_NAME = "database.password";
    protected static final String DATABASE_DBNAME_NAME = "database.dbname";
    protected static final String DATABASE_SERVER_ID_NAME = "database.server.id";
    protected static final String DATABASE_SERVER_NAME_NAME = "database.server.name";

    @Override
    public final void apply(Configuration.Builder builder, DebeziumConnectorProperties properties) {

        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();

        // 基础配置
        builder.with(ConnectorConfig.CONNECTOR_CLASS_CONFIG, this.getConnectorClass());
        map.from(properties::getDestination).whenHasText().to(value -> builder.with(ConnectorConfig.NAME_CONFIG, value));
        map.from(properties::getType).to(value -> builder.with(DATABASE_DB_TYPE, value.name().toLowerCase()));

        // 数据库连接配置
        map.from(properties::getHost).whenHasText().to(value -> builder.with(DATABASE_HOSTNAME_NAME, value));
        map.from(properties::getUsername).whenHasText().to(value -> builder.with(DATABASE_PORT_NAME, value));
        map.from(properties::getUsername).whenHasText().to(value -> builder.with(DATABASE_USER_NAME, value));
        map.from(properties::getPassword).whenHasText().to(value -> builder.with(DATABASE_PASSWORD_NAME, value));
        map.from(properties::getServerId).whenHasText().to(value -> builder.with(DATABASE_SERVER_ID_NAME, value));
        map.from(properties::getServerName).whenHasText().to(value -> builder.with(DATABASE_SERVER_NAME_NAME, value));

        // 数据库和表过滤配置
        map.from(properties::getSchemaIncludeList).whenHasText().to(value -> builder.with(SCHEMA_INCLUDE_LIST_NAME, value));
        map.from(properties::getSchemaExcludeList).whenHasText().to(value -> builder.with(SCHEMA_EXCLUDE_LIST_NAME, value));
        map.from(properties::getDatabaseWhitelist).whenHasText().to(value -> builder.with(DATABASE_WHITELIST_NAME, value));
        map.from(properties::getDatabaseBlacklist).whenHasText().to(value -> builder.with(DATABASE_BLACKLIST_NAME, value));
        map.from(properties::getDatabaseIncludeList).whenHasText().to(value -> builder.with(DATABASE_INCLUDE_LIST_NAME, value));
        map.from(properties::getDatabaseExcludeList).whenHasText().to(value -> builder.with(DATABASE_EXCLUDE_LIST_NAME, value));
        map.from(properties::getTableWhitelist).whenHasText().to(value -> builder.with(TABLE_WHITELIST_NAME, value));
        map.from(properties::getTableBlacklist).whenHasText().to(value -> builder.with(TABLE_BLACKLIST_NAME, value));
        map.from(properties::getTableIncludeList).whenHasText().to(value -> builder.with(TABLE_INCLUDE_LIST_NAME, value));
        map.from(properties::getTableExcludeList).whenHasText().to(value -> builder.with(TABLE_EXCLUDE_LIST_NAME, value));

        this.apply(map, builder, properties);
    }

    abstract String getConnectorClass();
    abstract void apply(PropertyMapper map, Configuration.Builder builder, DebeziumConnectorProperties properties) ;


}

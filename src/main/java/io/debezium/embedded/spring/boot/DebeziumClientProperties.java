/*
 * Copyright (c) 2018, hiwepy (https://github.com/hiwepy).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.debezium.embedded.spring.boot;

import io.debezium.embedded.protocol.DebeziumEntry;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author ： <a href="https://github.com/hiwepy">hiwepy</a>
 */
@ConfigurationProperties(DebeziumClientProperties.PREFIX)
@Data
public class DebeziumClientProperties {

	public static final String PREFIX = "debezium";
	
	/**
	 * Debezium 模式
	 */
	public static final String DEBEZIUM_MODE = "debezium.mode";
	
	/**
	 * Debezium 异步处理
	 */
	public static final String DEBEZIUM_ASYNC = "debezium.async";

    /**
     * 配置信息
     */
    private List<DebeziumClientInstance> instances = new ArrayList<>();

    /**
     * 批处理大小
     */
    private Integer batchSize = 1000;

    /**
     * 过滤器
     */
    private String filter;

    /**
     * 超时时间
     */
    private Duration timeout = Duration.ofSeconds(30);

    /**
     * 时间单位
     */
    private String unit = "SECONDS";

    /**
     * 订阅类型
     */
    private List<DebeziumEntry.EntryType> subscribeTypes = Arrays.asList(DebeziumEntry.EntryType.ROWDATA);

    /**
     * 是否启用异步处理
     */
    private Boolean async = true;

    /**
     * Debezium 模式
     */
    private String mode = "simple";

    @Data
    public static class DebeziumClientInstance {

        DebeziumConnectorProperties connector;

        DebeziumDatabaseHistoryProperties history;

        DebeziumOffsetStorageProperties offsetStorage;

    }

}

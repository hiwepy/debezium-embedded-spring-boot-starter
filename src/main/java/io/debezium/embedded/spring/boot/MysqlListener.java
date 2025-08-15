package io.debezium.embedded.spring.boot;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.format.Json;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.Executors;

@Slf4j
@Component
public class MysqlListener {

    private final List<DebeziumEngine<ChangeEvent<String, String>>> engineList = new ArrayList<>();

    private MysqlListener(@Qualifier("mysqlProperties") List<Properties> list) {
        for (Properties props : list) {
            this.engineList.add(DebeziumEngine.create(Json.class)
                    .using(props)
                    .notifying(record -> {
                        ChangeEvent<String, String> changeEvent = record;
                        receiveChangeEvent(record.value(), props);
                    }).build());
        }
    }

    private void receiveChangeEvent(String value, Properties props) {

    }

    @PostConstruct
    private void start() {
        for (DebeziumEngine<ChangeEvent<String, String>> engine : engineList) {
            Executors.newSingleThreadExecutor().execute(engine);
        }
    }

    @PreDestroy
    private void stop() {
        for (DebeziumEngine<ChangeEvent<String, String>> engine : engineList) {
            if (engine != null) {
                try {
                    engine.close();
                } catch (IOException e) {
                }
            }
        }
    }
}

package io.debezium.embedded.spring.boot.example;

import io.debezium.embedded.handler.EntryHandler;
import io.debezium.embedded.spring.boot.example.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

/**
 * Debezium Embedded 使用示例
 * 
 * 配置说明：
 * 1. 在 application.yml 中配置 debezium.embedded.enabled=true
 * 2. 配置数据库连接信息
 * 3. 选择 Offset 存储类型（FILE/KAFKA/JDBC/REDIS/S3/CUSTOM）
 * 4. 实现 EntryHandler 接口处理数据变更事件
 * 
 * 数据流：
 * DebeziumEngine -> RecordChangeEventHandler -> EntryHandler -> 业务逻辑
 */
@Slf4j
@SpringBootApplication
public class EmbeddedDebeziumExample {

    public static void main(String[] args) {
        SpringApplication.run(EmbeddedDebeziumExample.class, args);
    }

    /**
     * 用户表变更事件处理器
     * 实现 EntryHandler<User> 接口，处理用户表的增删改事件
     */
    @Component
    public static class UserEventHandler implements EntryHandler<User> {

        @Override
        public void insert(User user) {
            log.info("用户新增: {}", user);
            // 处理用户新增逻辑
            // 例如：发送欢迎邮件、更新缓存等
        }

        @Override
        public void update(User before, User after) {
            log.info("用户更新: before={}, after={}", before, after);
            // 处理用户更新逻辑
            // 例如：更新缓存、发送通知等
        }

        @Override
        public void delete(User user) {
            log.info("用户删除: {}", user);
            // 处理用户删除逻辑
            // 例如：清理缓存、记录审计日志等
        }
    }

    /**
     * 订单表变更事件处理器示例
     */
    @Component
    public static class OrderEventHandler implements EntryHandler<Order> {

        @Override
        public void insert(Order order) {
            log.info("订单新增: {}", order);
            // 处理订单新增逻辑
        }

        @Override
        public void update(Order before, Order after) {
            log.info("订单更新: before={}, after={}", before, after);
            // 处理订单更新逻辑
        }

        @Override
        public void delete(Order order) {
            log.info("订单删除: {}", order);
            // 处理订单删除逻辑
        }
    }

    /**
     * 订单实体类示例
     */
    public static class Order {
        private Long id;
        private String orderNo;
        private Long userId;
        private String status;
        private String createTime;
        private String updateTime;

        // getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getOrderNo() { return orderNo; }
        public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getCreateTime() { return createTime; }
        public void setCreateTime(String createTime) { this.createTime = createTime; }
        public String getUpdateTime() { return updateTime; }
        public void setUpdateTime(String updateTime) { this.updateTime = updateTime; }

        @Override
        public String toString() {
            return "Order{" +
                    "id=" + id +
                    ", orderNo='" + orderNo + '\'' +
                    ", userId=" + userId +
                    ", status='" + status + '\'' +
                    ", createTime='" + createTime + '\'' +
                    ", updateTime='" + updateTime + '\'' +
                    '}';
        }
    }
}

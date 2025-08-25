package io.debezium.spring.boot;

import io.debezium.embedded.factory.RecordChangeEventEntryHandler;
import io.debezium.embedded.util.GenericUtil;
import lombok.Data;

import java.util.Map;

/**
 * GenericUtil使用示例
 */
public class GenericUtilExample {

    public static void main(String[] args) {
        System.out.println("=== GenericUtil 修复验证 ===");
        
        try {
            // 1. 测试User处理器
            UserHandler userHandler = new UserHandler();
            Class<?> userGenericType = GenericUtil.getGenericType(userHandler);
            System.out.println("User处理器泛型类型: " + userGenericType.getName());
            
            // 2. 测试Map处理器
            MapHandler mapHandler = new MapHandler();
            Class<?> mapGenericType = GenericUtil.getGenericType(mapHandler);
            System.out.println("Map处理器泛型类型: " + mapGenericType.getName());
            
            // 3. 测试匿名内部类处理器
            RecordChangeEventEntryHandler<String> anonymousHandler = new RecordChangeEventEntryHandler<String>() {
                @Override
                public void insert(String entity) {
                    System.out.println("插入: " + entity);
                }

                @Override
                public void update(String before, String after) {
                    System.out.println("更新: " + before + " -> " + after);
                }

                @Override
                public void delete(String entity) {
                    System.out.println("删除: " + entity);
                }
            };
            
            Class<?> anonymousGenericType = GenericUtil.getGenericType(anonymousHandler);
            System.out.println("匿名处理器泛型类型: " + anonymousGenericType.getName());
            
            // 4. 测试直接获取接口泛型类型
            Class<?> directGenericType = GenericUtil.getInterfaceGenericType(
                UserHandler.class, 
                RecordChangeEventEntryHandler.class, 
                0
            );
            assert directGenericType != null;
            System.out.println("直接获取的泛型类型: " + directGenericType.getName());
            
            System.out.println("\n=== 所有测试通过 ===");
            
        } catch (Exception e) {
            System.err.println("测试失败: " + e.getMessage());
        }
    }
    
    // 测试用的实体类
    @Data
    public static class User {
        private String name;
        private int age;
        
        public User() {}
        
        public User(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public String toString() {
            return "User{name='" + name + "', age=" + age + "}";
        }
    }

    // 测试用的处理器
    public static class UserHandler implements RecordChangeEventEntryHandler<User> {
        @Override
        public void insert(User entity) {
            System.out.println("插入用户: " + entity);
        }

        @Override
        public void update(User before, User after) {
            System.out.println("更新用户: " + before + " -> " + after);
        }

        @Override
        public void delete(User entity) {
            System.out.println("删除用户: " + entity);
        }
    }

    // Map处理器
    public static class MapHandler implements RecordChangeEventEntryHandler<Map<String, Object>> {
        @Override
        public void insert(Map<String, Object> entity) {
            System.out.println("插入Map: " + entity);
        }

        @Override
        public void update(Map<String, Object> before, Map<String, Object> after) {
            System.out.println("更新Map: " + before + " -> " + after);
        }

        @Override
        public void delete(Map<String, Object> entity) {
            System.out.println("删除Map: " + entity);
        }
    }
}

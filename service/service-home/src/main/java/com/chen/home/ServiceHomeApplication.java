package com.chen.ssyx;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)//取消默认的数据配置源头
//默认的是mysql数据库，在这个模块中不需要使用这个数据库，所以需要排除

@EnableFeignClients
@EnableDiscoveryClient
public class ServiceHomeApplication {
    public static void main(String[] args) {

        SpringApplication.run(ServiceHomeApplication.class);

    }
}

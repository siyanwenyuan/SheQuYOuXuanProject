package com.chen.search;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;


/**
 * //@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
 * 覆盖相同数据源
 * 因为在这个模块中使用的是ES数据源，但是在依赖中使用的MySQL数据源，所以需要加入这个覆盖MySQL
 * 否则本模块中的ES数据源无法使用
 *
 */



@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)//覆盖相同数据源
@EnableFeignClients
@EnableDiscoveryClient
public class ServiceSearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceSearchApplication.class,args);

    }


}
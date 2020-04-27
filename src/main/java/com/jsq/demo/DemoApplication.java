package com.jsq.demo;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@MapperScan(value = "com.jsq.demo.dao", annotationClass = Mapper.class)
@SpringBootApplication
@ComponentScan(value = "com.jsq.demo.*")
@ServletComponentScan
public class DemoApplication {
    private static ApplicationContext applicationContext;

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static void main(String[] args) {
        DemoApplication.applicationContext = SpringApplication.run(DemoApplication.class, args);
    }

}

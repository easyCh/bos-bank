package com.bscc;

import com.bscc.core.base.BaseProvider;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

/**
 * @author Kent
 * @since 2017-03-24 18:00
 */
@MapperScan("com.bscc.**.mapper*")
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(Application.class, args);
        BaseProvider.initApplicationContext(applicationContext);
    }
}
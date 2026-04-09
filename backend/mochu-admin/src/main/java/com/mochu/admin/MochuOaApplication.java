package com.mochu.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.mochu")
@MapperScan("com.mochu.**.mapper")
@EnableAsync
@EnableScheduling
public class MochuOaApplication {

    public static void main(String[] args) {
        SpringApplication.run(MochuOaApplication.class, args);
    }
}

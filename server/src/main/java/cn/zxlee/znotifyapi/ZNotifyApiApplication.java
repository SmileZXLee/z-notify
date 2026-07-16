package cn.zxlee.znotifyapi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@MapperScan("cn.zxlee.znotifyapi.mapper")
@EnableCaching
@EnableAsync
public class ZNotifyApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZNotifyApiApplication.class, args);
    }

}

package com.artrithm.backendapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import java.util.TimeZone;

@SpringBootApplication
@EnableElasticsearchRepositories(basePackages = "com.artrithm.backendapi.search.repository")
public class BackendApiApplication {

    public static void main(String[] args) {
        // 애플리케이션 전체 시간대를 Asia/Seoul로 설정
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
        SpringApplication.run(BackendApiApplication.class, args);
    }
}

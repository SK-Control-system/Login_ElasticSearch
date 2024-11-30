package com.example.final_jj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(com.example.final_jj.elasticsearch.factor.ElasticsearchConfig.class)
public class FinalJjApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinalJjApplication.class, args);
    }

}

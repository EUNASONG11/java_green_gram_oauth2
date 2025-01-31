package com.green.greengramver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing //auditing 기능 활성화
@ConfigurationPropertiesScan
@SpringBootApplication
public class GreenGramTdd {

    public static void main(String[] args) {
        SpringApplication.run(GreenGramTdd.class, args);
    }

}

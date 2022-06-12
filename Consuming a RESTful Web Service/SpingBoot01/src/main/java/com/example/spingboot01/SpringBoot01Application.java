package com.example.spingboot01;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class SpringBoot01Application {
    private static final Logger log = LoggerFactory.getLogger(SpringBoot01Application.class);
    public static void main(String[] args) {
        SpringApplication.run(SpringBoot01Application.class, args);
    }
    @Bean
    public RestTemplateBuilder restTemplateBuilder() {
        return new RestTemplateBuilder();
    }
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean
    public CommandLineRunner run(RestTemplate restTemplate) throws Exception {
        return args -> {
            Quote quote = restTemplate.getForObject(
                    "http://localhost:8090/Sping-1.0-SNAPSHOT/get/json", Quote.class);
            log.info(quote.toString());
        };
    }
}

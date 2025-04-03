package org.wierzbadev.wordservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;
import org.wierzbadev.wordservice.config.RabbitMQConfig;

@Import(RabbitMQConfig.class)
@SpringBootApplication(exclude = RabbitAutoConfiguration.class)
@EnableDiscoveryClient
public class WordServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WordServiceApplication.class, args);
    }

}
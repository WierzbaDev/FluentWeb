package org.wierzbadev.userservice;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;
import org.wierzbadev.userservice.config.RabbitMQConfig;

@Import(RabbitMQConfig.class)
@SpringBootApplication(exclude = RabbitAutoConfiguration.class)
@EnableDiscoveryClient
@EnableRabbit
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

}

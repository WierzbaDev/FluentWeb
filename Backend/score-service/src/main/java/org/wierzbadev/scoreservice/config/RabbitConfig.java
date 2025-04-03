package org.wierzbadev.scoreservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Slf4j
@Configuration
public class RabbitConfig {

    // Config for RabbitMQ for word-service (5670)
    @Primary
    @Bean(name = "wordRabbitConnectionFactory")
    public ConnectionFactory wordRabbitConnectionFactory(
            @Value("${spring.rabbitmq.first.host}") String host,
            @Value("${spring.rabbitmq.first.port}") int port,
            @Value("${spring.rabbitmq.first.username}") String username,
            @Value("${spring.rabbitmq.first.password}") String password
    ) {
        CachingConnectionFactory factory = new CachingConnectionFactory(host, port);
        factory.setUsername(username);
        factory.setPassword(password);
        factory.setVirtualHost("/");
        return factory;
    }

    @Bean(name = "wordRabbitTemplate")
    public RabbitTemplate wordRabbitTemplate(@Qualifier("wordRabbitConnectionFactory") ConnectionFactory factory) {
        return new RabbitTemplate(factory);
    }

    @Bean(name = "wordRabbitAdmin")
    public RabbitAdmin wordRabbitAdmin(@Qualifier("wordRabbitConnectionFactory") ConnectionFactory factory) {
        return new RabbitAdmin(factory);
    }

    // Config for RabbitMQ for user-service (5673)
    @Bean(name = "userRabbitConnectionFactory")
    public ConnectionFactory userRabbitConnectionFactory(
            @Value("${spring.rabbitmq.second.host}") String host,
            @Value("${spring.rabbitmq.second.port}") int port,
            @Value("${spring.rabbitmq.second.username}") String username,
            @Value("${spring.rabbitmq.second.password}") String password
    ) {
        log.info("Creating connect with RabbitMQ {}:{}", host, port);

        CachingConnectionFactory factory = new CachingConnectionFactory(host, port);
        factory.setUsername(username);
        factory.setPassword(password);
        factory.setVirtualHost("/");
        return factory;
    }

    @Bean(name = "userRabbitTemplate")
    public RabbitTemplate userRabbitTemplate(@Qualifier("userRabbitConnectionFactory") ConnectionFactory factory) {
        return new RabbitTemplate(factory);
    }

    @Bean(name = "userRabbitAdmin")
    public RabbitAdmin userRabbitAdmin(@Qualifier("userRabbitConnectionFactory") ConnectionFactory factory) {
        return new RabbitAdmin(factory);
    }
}

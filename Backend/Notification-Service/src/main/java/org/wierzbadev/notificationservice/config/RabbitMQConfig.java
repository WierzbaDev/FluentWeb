package org.wierzbadev.notificationservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // Config for receive stats info from word-service (5674)
    @Primary
    @Bean(name = "notifiRabbitConnectionFactory")
    public ConnectionFactory notifiRabbitConnectionFactory(
            @Value("${spring.first.rabbitmq.host}") String host,
            @Value("${spring.first.rabbitmq.port}") int port,
            @Value("${spring.first.rabbitmq.username}") String username,
            @Value("${spring.first.rabbitmq.password}") String password) {

        CachingConnectionFactory factory = new CachingConnectionFactory(host, port);
        factory.setUsername(username);
        factory.setPassword(password);
        factory.setVirtualHost("/");
        return factory;
    }

    @Bean(name = "notifiRabbitTemplate")
    public RabbitTemplate notifiRabbitTemplate(@Qualifier("notifiRabbitConnectionFactory") ConnectionFactory factory) {
        RabbitTemplate template = new RabbitTemplate(factory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    @Bean(name = "notifiRabbitAdmin")
    public RabbitAdmin notifiRabbitAdmin(@Qualifier("notifiRabbitConnectionFactory") ConnectionFactory factory) {
        return new RabbitAdmin(factory);
    }

    @Bean(name = "notifiRabbitListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory notifiRabbitListenerContainerFactory(
            @Qualifier("notifiRabbitConnectionFactory") ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        return factory;
    }

    @Bean
    public Queue newStatsInfoQueue() {
        return new Queue("stats-info", true);
    }

    // Config for receive userDto from user-service (5675)
    @Bean(name = "userRabbitConnectionFactory")
    public ConnectionFactory userRabbitConnectionFactory(
            @Value("${spring.second.rabbitmq.host}") String host,
            @Value("${spring.second.rabbitmq.port}") int port,
            @Value("${spring.second.rabbitmq.username}") String username,
            @Value("${spring.second.rabbitmq.password}") String password) {

        CachingConnectionFactory factory = new CachingConnectionFactory(host, port);
        factory.setUsername(username);
        factory.setPassword(password);
        factory.setVirtualHost("/");
        return factory;
    }

    @Bean(name = "userRabbitTemplate")
    public RabbitTemplate userRabbitTemplate(@Qualifier("userRabbitConnectionFactory") ConnectionFactory factory) {
        RabbitTemplate template = new RabbitTemplate(factory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    @Bean(name = "userRabbitAdmin")
    public RabbitAdmin userRabbitAdmin(@Qualifier("userRabbitConnectionFactory") ConnectionFactory factory) {
        return new RabbitAdmin(factory);
    }

    @Bean(name = "userRabbitListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory userRabbitListenerContainerFactory(
            @Qualifier("userRabbitConnectionFactory") ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        return factory;
    }

//    @Bean
//    public Queue newUserWelcomeQueue() {
//        return new Queue("user-welcome-queue", true);
//    }
//
//    @Bean
//    public Queue newUserDtoQueue() {
//        return new Queue("user-dto-queue", true);
//    }
//
//    @Bean
//    public Queue newUserIdQueue() {
//        return new Queue("user-id-queue", true);
//    }
//
//    @Bean
//    public Queue newUserChangePasswordQueue() {
//        return new Queue("user-change-password-queue", true);
//    }
//
//    @Bean
//    public DirectExchange newUserEventExchange() {
//        return new DirectExchange("user-event-exchange");
//    }
//
//    @Bean
//    public Binding newUserIdBinding(Queue newUserIdQueue, DirectExchange newUserEventExchange) {
//        return BindingBuilder.bind(newUserIdQueue).to(newUserEventExchange).with("user-id");
//    }
}

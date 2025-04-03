package org.wierzbadev.wordservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
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

@Slf4j
@Configuration
public class RabbitMQConfig {

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // config for RabbitMQ for score-service (5670)
    @Bean(name = "wordRabbitConnectionFactory")
    public ConnectionFactory wordRabbitConnectionFactory(
        @Value("${spring.rabbitmq.first.host}") String host,
        @Value("${spring.rabbitmq.first.port}") int port,
        @Value("${spring.rabbitmq.first.username}") String username,
        @Value("${spring.rabbitmq.first.password}") String password) {

        logInfo(host, port);

        CachingConnectionFactory factory = new CachingConnectionFactory(host, port);
        factory.setUsername(username);
        factory.setPassword(password);
        factory.setVirtualHost("/");
        return factory;
    }

    @Bean(name = "wordRabbitTemplate")
    public RabbitTemplate wordRabbitTemplate(@Qualifier("wordRabbitConnectionFactory") ConnectionFactory factory) {
        RabbitTemplate template = new RabbitTemplate(factory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    @Bean(name = "wordRabbitAdmin")
    public RabbitAdmin wordRabbitAdmin(@Qualifier("wordRabbitConnectionFactory") ConnectionFactory factory) {
        return new RabbitAdmin(factory);
    }

    @Bean
    public Queue newUserScoreQueue() {
        return new Queue("user-score", true);
    }

    @Bean
    public TopicExchange newUserScoreExchange(@Qualifier("wordRabbitAdmin") RabbitAdmin rabbitAdmin) {
        TopicExchange exchange = new TopicExchange("user-score-exchange");
        rabbitAdmin.declareExchange(exchange);
        return exchange;
    }

    @Bean
    public Binding newUserScoreBinding(Queue newUserScoreQueue, TopicExchange newUserScoreExchange, @Qualifier("wordRabbitAdmin") RabbitAdmin rabbitAdmin) {
        Binding binding = BindingBuilder.bind(newUserScoreQueue).to(newUserScoreExchange).with("user.score");
        rabbitAdmin.declareBinding(binding);
        return binding;
    }

    // Config for RabbitMQ for notification-service (5674)
    @Bean(name = "notifiRabbitConnectionFactory")
    public ConnectionFactory notifiRabbitConnectionFactory(
            @Value("${spring.rabbitmq.second.host}") String host,
            @Value("${spring.rabbitmq.second.port}") int port,
            @Value("${spring.rabbitmq.second.username}") String username,
            @Value("${spring.rabbitmq.second.password}") String password) {

        logInfo(host, port);

        CachingConnectionFactory factory = new CachingConnectionFactory(host, port);
        factory.setUsername(username);
        factory.setPassword(password);
        factory.setVirtualHost("/");
        return factory;
    }

    @Primary
    @Bean(name = "notifiRabbitTemplate")
    public RabbitTemplate notifiRabbitTemplate(@Qualifier("notifiRabbitConnectionFactory") ConnectionFactory factory) {
        RabbitTemplate template = new RabbitTemplate(factory);
        template.setMessageConverter(jsonMessageConverter());
        log.info("Creating RabbitTemplate: notifiRabbitTemplate with factory: {}", factory);
        return template;
    }

    @Bean(name = "notifiRabbitAdmin")
    public RabbitAdmin notifiRabbitAdmin(@Qualifier("notifiRabbitConnectionFactory") ConnectionFactory factory) {
        return new RabbitAdmin(factory);
    }

    @Bean
    public Queue newStatsInfoQueue() {
        return new Queue("stats-info", true);
    }

    @Bean
    public TopicExchange newStatsInfoExchange(@Qualifier("notifiRabbitAdmin") RabbitAdmin rabbitAdmin) {
        TopicExchange exchange = new TopicExchange("stats-info-exchange");
        rabbitAdmin.declareExchange(exchange);
        return exchange;
    }

    @Bean
    public Binding newDailyInfoBinding(Queue newStatsInfoQueue, TopicExchange newStatsInfoExchange, @Qualifier("notifiRabbitAdmin") RabbitAdmin rabbitAdmin) {
        Binding binding = BindingBuilder.bind(newStatsInfoQueue).to(newStatsInfoExchange).with("daily-info");
        rabbitAdmin.declareBinding(binding);
        return binding;
    }

    @Bean(name = "userRabbitConnectionFactory")
    public ConnectionFactory userRabbitConnectionFactory(
            @Value("${spring.rabbitmq.third.host}") String host,
            @Value("${spring.rabbitmq.third.port}") int port,
            @Value("${spring.rabbitmq.third.username}") String username,
            @Value("${spring.rabbitmq.third.password}") String password) {

        logInfo(host, port);

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

    private void logInfo(String host, int port) {
        log.info("Creating connection with RabbitMQ {}:{}", host, port);
    }
}

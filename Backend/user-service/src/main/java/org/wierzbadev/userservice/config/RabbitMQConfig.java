package org.wierzbadev.userservice.config;

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

    // config RabbitMQ for notification-service (5675)
    @Bean(name = "notificationRabbitConnectionFactory")
    public ConnectionFactory notificationRabbitConnectionFactory(
            @Value("${spring.rabbitmq.second.host}") String host,
            @Value("${spring.rabbitmq.second.port}") int port,
            @Value("${spring.rabbitmq.second.username}") String username,
            @Value("${spring.rabbitmq.second.password}") String password) {

        CachingConnectionFactory factory = new CachingConnectionFactory(host, port);
        factory.setUsername(username);
        factory.setPassword(password);
        factory.setVirtualHost("/");
        return factory;
    }

    @Bean(name = "notificationRabbitTemplate")
    public RabbitTemplate notificationRabbitTemplate(@Qualifier("notificationRabbitConnectionFactory") ConnectionFactory factory) {
        RabbitTemplate template = new RabbitTemplate(factory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    @Bean(name = "notificationRabbitAdmin")
    public RabbitAdmin notificationRabbitAdmin(@Qualifier("notificationRabbitConnectionFactory") ConnectionFactory factory) {
        return new RabbitAdmin(factory);
    }

    @Bean(name = "notificationRabbitListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory notificationRabbitListenerContainerFactory(
            @Qualifier("notificationRabbitConnectionFactory") ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        return factory;
    }

    @Bean
    public Queue userQueue() {
        log.info("Created queue user-id-queue, because this was not exists");
        return new Queue("user-id-queue", true);
    }

    @Bean
    public Queue newUserDtoQueue() {
        return new Queue("user-dto-queue", true);
    }

    @Bean
    public Queue newUserWelcomeQueue() {
        return new Queue("user-welcome-queue", true);
    }

    @Bean
    public Queue newUserChangePasswordQueue() {
        return new Queue("user-change-password-queue", true);
    }


    @Bean(name = "notificationUserEventExchange")
    public TopicExchange  newUserEventExchange(@Qualifier("notificationRabbitAdmin") RabbitAdmin rabbitAdmin) {
        TopicExchange exchange = new TopicExchange ("user-event-exchange");
        rabbitAdmin.declareExchange(exchange);
        return exchange;
    }

    @Bean
    public Binding newUserDtoBinding(Queue newUserDtoQueue, @Qualifier("notificationUserEventExchange") TopicExchange  newUserEventExchange, @Qualifier("notificationRabbitAdmin") RabbitAdmin rabbitAdmin) {
        Binding binding = BindingBuilder.bind(newUserDtoQueue).to(newUserEventExchange).with("user-dto");
        rabbitAdmin.declareBinding(binding);
        return binding;
    }

    @Bean
    public Binding newUserVerifyBinding(Queue newUserWelcomeQueue, @Qualifier("notificationUserEventExchange") TopicExchange  newUserEventExchange, @Qualifier("notificationRabbitAdmin") RabbitAdmin rabbitAdmin) {
        Binding binding = BindingBuilder.bind(newUserWelcomeQueue).to(newUserEventExchange).with("user-verify");
        rabbitAdmin.declareBinding(binding);
        return binding;
    }

    @Bean Binding newForgotUserPassword(Queue newUserChangePasswordQueue, @Qualifier("notificationUserEventExchange") TopicExchange  newUserEventExchange, @Qualifier("notificationRabbitAdmin") RabbitAdmin rabbitAdmin) {
        Binding binding = BindingBuilder.bind(newUserChangePasswordQueue).to(newUserEventExchange).with("user.forgot.password");
        rabbitAdmin.declareBinding(binding);
        return binding;
    }

    @Bean Binding newUserIdBinding(Queue userQueue, @Qualifier("notificationUserEventExchange") TopicExchange  newUserEventExchange, @Qualifier("notificationRabbitAdmin") RabbitAdmin rabbitAdmin) {
        Binding binding = BindingBuilder.bind(userQueue).to(newUserEventExchange).with("user-id");
        rabbitAdmin.declareBinding(binding);
        return binding;
    }

    // config RabbitMQ for score-service (5673)
    @Bean(name = "scoreRabbitConnectionFactory")
    public ConnectionFactory scoreRabbitConnectionFactory(
            @Value("${spring.rabbitmq.first.host}") String host,
            @Value("${spring.rabbitmq.first.port}") int port,
            @Value("${spring.rabbitmq.first.username}") String username,
            @Value("${spring.rabbitmq.first.password}") String password) {

        CachingConnectionFactory factory = new CachingConnectionFactory(host, port);
        factory.setUsername(username);
        factory.setPassword(password);
        factory.setVirtualHost("/");
        return factory;
    }

    @Primary
    @Bean(name = "scoreRabbitTemplate")
    public RabbitTemplate scoreRabbitTemplate(@Qualifier("scoreRabbitConnectionFactory") ConnectionFactory factory) {
        RabbitTemplate template = new RabbitTemplate(factory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    @Bean(name = "scoreRabbitAdmin")
    public RabbitAdmin scoreRabbitAdmin(@Qualifier("scoreRabbitConnectionFactory") ConnectionFactory factory) {
        return new RabbitAdmin(factory);
    }

    @Bean
    public Queue newUserDeleteQueue(@Qualifier("scoreRabbitAdmin") RabbitAdmin rabbitAdmin) {
        Queue queue = new Queue("user-delete", true);
        rabbitAdmin.declareQueue(queue);
        return queue;
    }

    @Bean
    public Queue newUserUpdatedQueue(@Qualifier("scoreRabbitAdmin") RabbitAdmin rabbitAdmin) {
        Queue queue = new Queue("user-updates", true);
        rabbitAdmin.declareQueue(queue);
        return queue;
    }

    @Bean(name = "scoreUserEventExchange")
    public TopicExchange newUserEventsExchanges(@Qualifier("scoreRabbitAdmin") RabbitAdmin rabbitAdmin) {
        TopicExchange exchange = new TopicExchange("user-events-exchange");
        rabbitAdmin.declareExchange(exchange);
        return exchange;
    }

    @Bean
    public Binding newUserDeletedBinding(Queue newUserDeleteQueue, @Qualifier("scoreUserEventExchange") TopicExchange newUserEventsExchanges, @Qualifier("scoreRabbitAdmin") RabbitAdmin rabbitAdmin) {
        Binding binding = BindingBuilder.bind(newUserDeleteQueue).to(newUserEventsExchanges).with("user.deleted");
        rabbitAdmin.declareBinding(binding);
        return binding;
    }

    @Bean
    public Binding newUserUpdatedBinding(Queue newUserUpdatedQueue, @Qualifier("scoreUserEventExchange") TopicExchange newUserEventsExchanges, @Qualifier("scoreRabbitAdmin") RabbitAdmin rabbitAdmin) {
        Binding binding = BindingBuilder.bind(newUserUpdatedQueue).to(newUserEventsExchanges).with("user.updated");
        rabbitAdmin.declareBinding(binding);
        return binding;
    }

    // config RabbitMQ for word-service (5676)
    @Bean(name = "wordRabbitConnectionFactory")
    public ConnectionFactory wordRabbitConnectionFactory(
            @Value("${spring.rabbitmq.third.host}") String host,
            @Value("${spring.rabbitmq.third.port}") int port,
            @Value("${spring.rabbitmq.third.username}") String username,
            @Value("${spring.rabbitmq.third.password}") String password) {

        CachingConnectionFactory factory = new CachingConnectionFactory(host, port);
        factory.setUsername(username);
        factory.setPassword(password);
        factory.setVirtualHost("/");
        return factory;
    }

    @Bean(name = "wordRabbitTemplate")
    RabbitTemplate wordRabbitTemplate(@Qualifier("wordRabbitConnectionFactory") ConnectionFactory factory) {
        RabbitTemplate template = new RabbitTemplate(factory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    @Bean(name = "wordRabbitAdmin")
    public RabbitAdmin wordRabbitAdmin(@Qualifier("wordRabbitConnectionFactory") ConnectionFactory factory) {
        return new RabbitAdmin(factory);
    }

    @Bean(name = "wordRabbitListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory wordRabbitListenerContainerFactory(
            @Qualifier("wordRabbitConnectionFactory") ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        return factory;
    }

    @Bean
    public Queue newWordUserDeletedQueue() {
        return new Queue("user-deleted-queue", true);
    }

    @Bean(name = "wordUserEventExchange")
    public TopicExchange newWordUserExchange(@Qualifier("wordRabbitAdmin") RabbitAdmin rabbitAdmin) {
        TopicExchange exchange = new TopicExchange("user-event-exchange");
        rabbitAdmin.declareExchange(exchange);
        return exchange;
    }

    @Bean
    public Binding newWordUserQueue(Queue newWordUserDeletedQueue, @Qualifier("wordUserEventExchange") TopicExchange newWordUserExchange, @Qualifier("wordRabbitAdmin") RabbitAdmin rabbitAdmin) {
        Binding binding = BindingBuilder.bind(newWordUserDeletedQueue).to(newWordUserExchange).with("user.deleted");
        rabbitAdmin.declareBinding(binding);
        return binding;
    }
}

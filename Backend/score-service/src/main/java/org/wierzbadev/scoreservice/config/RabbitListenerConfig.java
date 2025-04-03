package org.wierzbadev.scoreservice.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitListenerConfig {

    @Bean(name = "userRabbitListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory userRabbitListenerContainerFactory(
            @Qualifier("userRabbitConnectionFactory") ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        return factory;
    }

    @Bean
    public Queue newUserQueue() {
        return new Queue("user-score", true);
    }

    @Bean
    public Queue newScoreQueue() {
        return new Queue("user-score", true);
    }

    @Bean
    public Queue newUpdatedUserQueue() {
        return new Queue("user-updates", true);
    }

    @Bean
    public Queue newDeletedUserQueue() {
        return new Queue("user-delete", true);
    }
}

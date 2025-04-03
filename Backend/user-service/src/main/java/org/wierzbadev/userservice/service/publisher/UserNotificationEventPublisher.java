package org.wierzbadev.userservice.service.publisher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserNotificationEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public UserNotificationEventPublisher(@Autowired @Qualifier("wordRabbitTemplate") RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void notifyWordService(long id) {
        String exchange = "user-event-exchange";
        String routing = "user.deleted";

        log.info("Notify word-service, user with id: {} was deleted", id);
        rabbitTemplate.convertAndSend(exchange, routing, id);
    }
}

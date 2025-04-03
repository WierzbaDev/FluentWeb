package org.wierzbadev.userservice.service.publisher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public UserEventPublisher(@Autowired @Qualifier("scoreRabbitTemplate") RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendUserUpdatedEvent(long userId) {
        log.info("Sent user updated event for user: {}", userId);
        rabbitTemplate.convertAndSend("user-events-exchange", "user.updated", userId);
    }

    public void sendUserDeletedEvent(long userId) {
        log.info("Sent user deleted event for user: {}", userId);
        rabbitTemplate.convertAndSend("user-events-exchange", "user.deleted", userId);
    }
}

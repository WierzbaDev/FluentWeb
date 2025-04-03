package org.wierzbadev.notificationservice.service.publisher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class UserIdPublisher {

    private final RabbitTemplate rabbitTemplate;

    public UserIdPublisher(@Autowired @Qualifier("userRabbitTemplate") RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendUserId(List<Long> usersId) {
        String exchange = "user-event-exchange";
        String routing = "user-id";
        if (!usersId.isEmpty()) {
            log.info("Sent List of users id to user-service");
            rabbitTemplate.convertAndSend(exchange, routing, usersId);
        } else {
            log.info("Received empty list, I won't pass it on");
        }
    }
}

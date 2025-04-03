package org.wierzbadev.wordservice.service.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.wierzbadev.wordservice.service.ReviewService;

@Slf4j
@Service
public class UserDeletedEventListener {

    private final ReviewService service;

    public UserDeletedEventListener(ReviewService service) {
        this.service = service;
    }

    @RabbitListener(queues = "user-deleted-queue", containerFactory = "userRabbitListenerContainerFactory")
    public void receiveUserDeleted(long userId) {
        log.info("Receive info: User with id: {} was deleted", userId);

        service.deleteUserWordByUserId(userId);
    }
}

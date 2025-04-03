package org.wierzbadev.userservice.service.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.wierzbadev.userservice.dto.publish.UserEmailDto;
import org.wierzbadev.userservice.service.UserService;
import org.wierzbadev.userservice.service.publisher.UserDtoEventPublisher;

import java.util.List;

@Slf4j
@Service
public class
NotificationEventListener {

    private final UserService userService;
    private final UserDtoEventPublisher publisher;

    public NotificationEventListener(UserService userService, UserDtoEventPublisher publisher) {
        this.userService = userService;
        this.publisher = publisher;
    }

    @RabbitListener(queues = "user-id-queue", containerFactory = "notificationRabbitListenerContainerFactory")
    public void receiveListOfIds(List<Long> usersId) {
        log.info("Receive List of users id: {} with size: {}", usersId, usersId.size());
        List<UserEmailDto> userDtos = userService.readUserEmailDto(usersId);
        publisher.sendUsersDto(userDtos);
    }
}

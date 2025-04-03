package org.wierzbadev.userservice.service.publisher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.wierzbadev.userservice.dto.publish.UserEmailDto;
import org.wierzbadev.userservice.dto.publish.UserForgotPassword;
import org.wierzbadev.userservice.dto.publish.UserVerifyDto;

import java.util.List;

@Slf4j
@Service
public class UserDtoEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public UserDtoEventPublisher(@Autowired @Qualifier("notificationRabbitTemplate") RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendUsersDto(List<UserEmailDto> userDtos) {
        log.info("Send List<UserEmailDto>: {} with size: {}", userDtos, userDtos.size());

        String exchange = "user-event-exchange";
        String routing = "user-dto";
        rabbitTemplate.convertAndSend(exchange, routing, userDtos);
    }

    public void sendVerifyCode(UserVerifyDto verifyDto) {
        log.info("Send uuid code for user with id: {} to notification-service", verifyDto.getUserId());

        String exchange = "user-event-exchange";
        String routing = "user-verify";
        rabbitTemplate.convertAndSend(exchange, routing, verifyDto);
    }

    public void notifyWordServiceUserForgotPassword(UserForgotPassword userForgotPassword) {
        log.info("Send user forgot password class with email: {}", userForgotPassword.getEmail());
        
        String exchange = "user-event-exchange";
        String routing = "user.forgot.password";
        rabbitTemplate.convertAndSend(exchange, routing, userForgotPassword);
    }
}

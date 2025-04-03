package org.wierzbadev.notificationservice.service.listener;

import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.wierzbadev.notificationservice.model.dto.UserEmailDto;
import org.wierzbadev.notificationservice.model.dto.UserForgotPassword;
import org.wierzbadev.notificationservice.model.dto.UserVerifyDto;
import org.wierzbadev.notificationservice.service.EmailSenderService;
import org.wierzbadev.notificationservice.service.UserProcessingService;

import java.util.List;

@Slf4j
@Service
public class UserDtoListener {

    private final EmailSenderService senderService;
    private final UserProcessingService processingService;

    public UserDtoListener(EmailSenderService senderService, UserProcessingService processingService) {
        this.senderService = senderService;
        this.processingService = processingService;
    }

    @RabbitListener(queues = "user-welcome-queue", containerFactory = "userRabbitListenerContainerFactory")
    public void receiveVerifyUser(UserVerifyDto verifyDto) {
        log.info("Received userVerifyDto for user with id: {}", verifyDto.getUserId());
        try {
            senderService.sendVerifyCode(verifyDto);
        } catch (MessagingException e) {
            log.error("Error while receiving verify code", e);
        }
    }

    @RabbitListener(queues = "user-dto-queue", containerFactory = "userRabbitListenerContainerFactory")
    public void receiveUserEmailDto(List<UserEmailDto> listEmailDto) {
        log.info("Received user emails: {}", listEmailDto.size());
        processingService.processEmails(listEmailDto);
    }

    @RabbitListener(queues = "user-change-password-queue", containerFactory = "userRabbitListenerContainerFactory")
    public void receiveResetPasswordRequest(UserForgotPassword forgotPassword) {
        log.info("Received change password request by user: {}", forgotPassword.getEmail());
        try {
            senderService.sendResetPassword(forgotPassword);
        } catch (MessagingException e) {
            log.error("Error while receiving change password request", e);
        }
    }
}

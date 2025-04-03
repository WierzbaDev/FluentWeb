package org.wierzbadev.notificationservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.wierzbadev.notificationservice.model.dto.UserEmailDto;
import org.wierzbadev.notificationservice.model.dto.UserLearningDto;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class UserProcessingService {

    private final EmailSenderService emailSenderService;
    private final Map<Long, UserLearningDto> statsMap = new ConcurrentHashMap<>();
    private final Map<Long, UserEmailDto> emailMap = new ConcurrentHashMap<>();

    public UserProcessingService(EmailSenderService emailSenderService) {
        this.emailSenderService = emailSenderService;
    }

    public synchronized void processStats(List<UserLearningDto> dtoList) {
        for (UserLearningDto dto: dtoList) {
            log.info("Received stats for user {}", dto.getUserId());
            statsMap.put(dto.getUserId(), dto);

            if (emailMap.containsKey(dto.getUserId())) {
                sendEmailWithStats(dto.getUserId());
            }
        }
    }

    public synchronized void processEmails(List<UserEmailDto> emailList) {
        for (UserEmailDto emailDto: emailList) {
            log.info("Received email for user: {}", emailDto.getUserId());
            emailMap.put(emailDto.getUserId(), emailDto);

            if (statsMap.containsKey(emailDto.getUserId())) {
                sendEmailWithStats(emailDto.getUserId());
            }
        }
    }

    private void sendEmailWithStats(Long id) {
        UserLearningDto stats = statsMap.remove(id);
        UserEmailDto email = emailMap.remove(id);

        if (stats != null && email != null) {
            log.info("Sending email with stats to {}", email.getEmail());
            emailSenderService.sendDailyStats(stats, email);
        }
    }
}

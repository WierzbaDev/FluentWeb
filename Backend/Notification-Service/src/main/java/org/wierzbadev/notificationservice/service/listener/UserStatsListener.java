package org.wierzbadev.notificationservice.service.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.wierzbadev.notificationservice.model.dto.UserLearningDto;
import org.wierzbadev.notificationservice.service.UserProcessingService;
import org.wierzbadev.notificationservice.service.publisher.UserIdPublisher;

import java.util.List;

@Slf4j
@Service
public class UserStatsListener {

    private final UserIdPublisher userIdPublisher;
    private final UserProcessingService processingService;

    public UserStatsListener(UserIdPublisher userIdPublisher, UserProcessingService processingService) {
        this.userIdPublisher = userIdPublisher;
        this.processingService = processingService;
    }

    @RabbitListener(queues = "stats-info", containerFactory = "notifiRabbitListenerContainerFactory")
    public void receiveUserLearningDto(List<UserLearningDto> dtoList) {
        log.info("Receive list of UserLearningDto: {} with size: {}", dtoList, dtoList.size());

        List<Long> userId = dtoList.stream()
                .map(UserLearningDto::getUserId).toList();
        userIdPublisher.sendUserId(userId);
        processingService.processStats(dtoList);
    }
}

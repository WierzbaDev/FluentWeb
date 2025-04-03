package org.wierzbadev.wordservice.service.stats;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.wierzbadev.wordservice.model.dto.UserLearningDto;

import java.util.List;

@Slf4j
@Service
public class SendStatsInfoService {

    private final RabbitTemplate rabbitTemplate;

    public SendStatsInfoService(@Autowired @Qualifier("notifiRabbitTemplate") RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        log.info("Using RabbitTemplate: {} (class: {})", rabbitTemplate, rabbitTemplate.getClass());
    }

    public void sendStatsInfo(List<UserLearningDto> dtoList) {
        String exchange = "stats-info-exchange";
        String routing = "daily-info";
        log.info("Send list of UserLearningDto: {} with size: {}", dtoList, dtoList.size());
        rabbitTemplate.convertAndSend(exchange, routing, dtoList);
    }
}

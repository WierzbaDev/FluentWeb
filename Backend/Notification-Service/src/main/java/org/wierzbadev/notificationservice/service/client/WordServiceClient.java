package org.wierzbadev.notificationservice.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "word-service", url = "${notification-service.word-service-url}")
public interface WordServiceClient {

    @GetMapping("/daily")
    void sendDailyStatsInfo(@RequestHeader("Authorization") String token);
}

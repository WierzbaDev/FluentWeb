package org.wierzbadev.scoreservice.config;

import feign.QueryMapEncoder;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import feign.querymap.BeanQueryMapEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.wierzbadev.scoreservice.service.auth.AuthService;

@Configuration
public class FeignClientConfig {

    private final AuthService authService;

    public FeignClientConfig(AuthService authService) {
        this.authService = authService;
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            String token = "Bearer " + authService.getSystemToken();
            requestTemplate.header("Authorization", token);
        };
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomFeignErrorDecoder();
    }

    @Bean
    public QueryMapEncoder queryMapEncoder() {
        return new BeanQueryMapEncoder();
    }
}
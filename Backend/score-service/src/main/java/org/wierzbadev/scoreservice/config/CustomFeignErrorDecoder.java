package org.wierzbadev.scoreservice.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.wierzbadev.scoreservice.exception.ScoreError;
import org.wierzbadev.scoreservice.exception.ScoreException;

import java.io.IOException;
import java.util.Map;

public class CustomFeignErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {
        String message = "Unexpected error";
        ScoreError errorType = ScoreError.SCORE_SERVICE_ERROR;

        if (response.body() != null) {
            try {
                Map<String, Object> errorResponse = objectMapper.readValue(
                        response.body().asInputStream(),
                        new TypeReference<>() {}
                );

                if (errorResponse.containsKey("message")) {
                    message = (String) errorResponse.get("message");
                }

                errorType = switch (response.status()) {
                    case 404 -> ScoreError.SCORE_NOT_FOUND;
                    case 400 -> ScoreError.SCORE_NOT_IN_RANKING;
                    default -> ScoreError.SCORE_SERVICE_ERROR;
                };

            } catch (IOException e) {
                message = "Error reading response from user-service";
            }
        } else {
            message = "No response body from user-service";
        }

        return new ScoreException(errorType, message);
    }
}

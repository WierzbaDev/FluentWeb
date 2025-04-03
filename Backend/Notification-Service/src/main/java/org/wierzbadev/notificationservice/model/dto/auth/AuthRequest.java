package org.wierzbadev.notificationservice.model.dto.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthRequest {
    private String email;
    private String password;
    private String token;
}

package org.wierzbadev.notificationservice.model.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserForgotPassword {
    private String email;
    private String token;
}

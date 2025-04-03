package org.wierzbadev.userservice.dto.publish;

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

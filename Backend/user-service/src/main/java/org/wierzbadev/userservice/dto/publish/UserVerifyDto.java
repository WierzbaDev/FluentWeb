package org.wierzbadev.userservice.dto.publish;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserVerifyDto {
    private long userId;
    private String email;
    private String name;
    private String uuid;
}

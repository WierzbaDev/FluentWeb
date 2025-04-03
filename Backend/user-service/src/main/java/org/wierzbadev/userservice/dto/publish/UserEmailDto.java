package org.wierzbadev.userservice.dto.publish;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserEmailDto {
    private long userId;
    private String email;
    private String name;
}

package org.wierzbadev.notificationservice.model.dto;

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

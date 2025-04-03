package org.wierzbadev.scoreservice.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class UserDto implements Serializable {
    private long id;
    private String name;
}

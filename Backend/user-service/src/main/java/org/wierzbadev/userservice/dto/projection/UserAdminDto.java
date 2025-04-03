package org.wierzbadev.userservice.dto.projection;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.wierzbadev.userservice.model.Role;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserAdminDto {
    private long userId;
    private String name;
    private String surname;
    private String email;
    private LocalDate birthday;
    private Role role;
    private boolean verify;
    private UUID veryficationUuid;
}

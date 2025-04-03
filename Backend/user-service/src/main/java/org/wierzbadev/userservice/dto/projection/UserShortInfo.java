package org.wierzbadev.userservice.dto.projection;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserShortInfo {
    private String name;
    private String surname;
    private String email;
    private LocalDate birthday;
}

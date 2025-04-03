package org.wierzbadev.userservice.dto.auth;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class RegisterRequest {
    private String name;
    private String surname;
    @Email(message = "Must be email format!")
    private String email;
    private String password;
    private String repeatPassword;
    private LocalDate birthday;
}

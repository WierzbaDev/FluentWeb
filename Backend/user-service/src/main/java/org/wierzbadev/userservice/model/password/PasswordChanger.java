package org.wierzbadev.userservice.model.password;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class PasswordChanger {
    @NotBlank(message = "Password must not be empty")
    private String password;
    @NotBlank(message = "Repeated password must not be empty")
    private String repeatPassword;
}

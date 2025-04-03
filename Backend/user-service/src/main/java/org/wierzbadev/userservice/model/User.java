package org.wierzbadev.userservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotBlank(message = "User's name must not be empty")
    private String name;
    @NotBlank(message = "User's surname must not be empty")
    private String surname;
    @NotBlank(message = "User's email must not be empty")
    @Column(unique = true, nullable = false)
    private String email;
    @NotNull(message = "User's birthday must not be empty")
    private LocalDate birthday;
    @NotBlank(message = "User's password must not be empty")
    private String password;
    @NotNull(message = "User's role must not be empty")
    @Enumerated(EnumType.STRING)
    private Role role;
    private boolean verify;
    private UUID uuid;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }
}

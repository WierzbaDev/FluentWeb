package org.wierzbadev.userservice.integration.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.wierzbadev.userservice.exception.UserError;
import org.wierzbadev.userservice.exception.UserException;
import org.wierzbadev.userservice.model.Role;
import org.wierzbadev.userservice.model.User;
import org.wierzbadev.userservice.model.password.PasswordChanger;
import org.wierzbadev.userservice.model.password.PasswordResetToken;
import org.wierzbadev.userservice.repository.PasswordResetTokenRepository;
import org.wierzbadev.userservice.repository.UserRepository;
import org.wierzbadev.userservice.service.PasswordResetService;
import org.wierzbadev.userservice.service.UserService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Tag("integration")
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PasswordResetServiceIntegrationTest {

    @Autowired
    private PasswordResetService resetService;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordResetTokenRepository resetRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Throws UserException(USER_PASSWORD_EXPIRED) when password token is expired")
    void resetPasswordShould_throws_UserException_when_passwordTokenIsExpired() {
        // given
        userRepository.deleteAll();
        User user = User.builder()
                        .name("Imie")
                        .surname("Nazwisko")
                        .email("test@email.com")
                        .role(Role.USER)
                        .password("Password123@")
                        .birthday(LocalDate.now())
                        .uuid(UUID.randomUUID())
                        .verify(false)
                        .build();

        userService.createUser(user);

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .expiryDate(LocalDateTime.now().minusHours(2))
                .user(user)
                .build();

        resetRepository.save(resetToken);

        PasswordChanger changer = PasswordChanger.builder()
                .password("Haslo123@")
                .repeatPassword("Haslo123@")
                .build();

        // when
        Exception exception = assertThrows(UserException.class, () ->
                resetService.resetPassword(token, changer));

        // then
        assertEquals(UserError.USER_PASSWORD_EXPIRED.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("Throws UserException(USER_PASSWORD_EXPIRED) when password not match")
    void resetPasswordShould_throws_UserException_when_passwordNotMatch() {
        // given
        userRepository.deleteAll();
        User user = User.builder()
                .name("Imie")
                .surname("Nazwisko")
                .email("test@email.com")
                .role(Role.USER)
                .password("Password123@")
                .birthday(LocalDate.now())
                .uuid(UUID.randomUUID())
                .verify(false)
                .build();

        userService.createUser(user);

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .expiryDate(LocalDateTime.now().minusHours(2))
                .user(user)
                .build();

        resetRepository.save(resetToken);

        PasswordChanger changer = PasswordChanger.builder()
                .password("Haslo123@")
                .repeatPassword("ds@")
                .build();

        // when
        Exception exception = assertThrows(UserException.class, () ->
                resetService.resetPassword(token, changer));

        // then
        assertEquals(UserError.USER_PASSWORD_EXPIRED.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("Throws UserException(USER_PASSWORD_EXPIRED) when password is weak")
    void resetPasswordShould_throws_UserException_when_passwordIsWeak() {
        // given
        userRepository.deleteAll();
        User user = User.builder()
                .name("Imie")
                .surname("Nazwisko")
                .email("test@email.com")
                .role(Role.USER)
                .password("Password123@")
                .birthday(LocalDate.now())
                .uuid(UUID.randomUUID())
                .verify(false)
                .build();

        userService.createUser(user);

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .expiryDate(LocalDateTime.now().minusHours(2))
                .user(user)
                .build();

        resetRepository.save(resetToken);

        PasswordChanger changer = PasswordChanger.builder()
                .password("d@")
                .repeatPassword("d@")
                .build();

        // when
        Exception exception = assertThrows(UserException.class, () ->
                resetService.resetPassword(token, changer));

        // then
        assertEquals(UserError.USER_PASSWORD_EXPIRED.getMessage(), exception.getMessage());
    }
}
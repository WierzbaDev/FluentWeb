package org.wierzbadev.userservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.wierzbadev.userservice.dto.publish.UserForgotPassword;
import org.wierzbadev.userservice.exception.UserError;
import org.wierzbadev.userservice.exception.UserException;
import org.wierzbadev.userservice.model.User;
import org.wierzbadev.userservice.model.password.PasswordChanger;
import org.wierzbadev.userservice.model.password.PasswordResetToken;
import org.wierzbadev.userservice.repository.PasswordResetTokenRepository;
import org.wierzbadev.userservice.repository.UserRepository;
import org.wierzbadev.userservice.service.publisher.UserDtoEventPublisher;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.regex.Pattern;

@Slf4j
@Service
public class PasswordResetService {

    private final UserService userService;
    private final PasswordResetTokenRepository tokenRepository;
    private final UserDtoEventPublisher publisher;
    private final PasswordEncoder encoder;
    private final UserRepository userRepository;

    public PasswordResetService(UserService userService, PasswordResetTokenRepository tokenRepository, UserDtoEventPublisher publisher, PasswordEncoder encoder, UserRepository userRepository) {
        this.userService = userService;
        this.tokenRepository = tokenRepository;
        this.publisher = publisher;
        this.encoder = encoder;
        this.userRepository = userRepository;
    }

    public void sendResetPasswordEmail(String email) {
        User user = userService.readUserByEmail(email);

        tokenRepository.deleteByUser(user);

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .expiryDate(LocalDateTime.now().plusHours(1))
                .user(user)
                .build();

        tokenRepository.save(resetToken);

        UserForgotPassword forgotPassword = UserForgotPassword.builder()
                        .email(resetToken.getUser().getEmail())
                        .token(token).build();

        log.info("Generated password token for user: {}", user.getId());
        publisher.notifyWordServiceUserForgotPassword(forgotPassword);
    }

    public void resetPassword(String token, PasswordChanger passwordChanger) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new UserException(UserError.USER_INVALID_PASSWORD_TOKEN));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new UserException(UserError.USER_PASSWORD_EXPIRED);
        }

        if (!(passwordChanger.getPassword().equals(passwordChanger.getRepeatPassword()))) {
            throw new UserException(UserError.USER_PASSWORD_NOT_MATCH);
        }

        if (!Pattern.matches("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$", passwordChanger.getPassword())) {
            throw new UserException(UserError.USER_PASSWORD_IS_WEAK);
        }

        User user = userRepository.findById(resetToken.getUser().getId())
                        .orElseThrow(() -> new UserException(UserError.USER_NOT_FOUND));

        logChangePassword(user);
        tokenRepository.deleteByUser(user);
        user.setPassword(encoder.encode(passwordChanger.getPassword()));
        userRepository.save(user);
    }

    public void resetPasswordLoggedInUser(Long id, PasswordChanger passwordChanger) {

        if (!(passwordChanger.getPassword().equals(passwordChanger.getRepeatPassword()))) {
            throw new UserException(UserError.USER_PASSWORD_NOT_MATCH);
        }

        if (!Pattern.matches("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$", passwordChanger.getPassword())) {
            throw new UserException(UserError.USER_PASSWORD_IS_WEAK);
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserException(UserError.USER_NOT_FOUND));

        logChangePassword(user);
        user.setPassword(encoder.encode(passwordChanger.getPassword()));
        userRepository.save(user);
    }

    private void logChangePassword(User user) {
        log.info("User with id: {} changed password", user.getId());
    }
}

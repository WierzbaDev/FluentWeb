package org.wierzbadev.userservice.service.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wierzbadev.userservice.dto.auth.AuthRequest;
import org.wierzbadev.userservice.dto.auth.AuthResponse;
import org.wierzbadev.userservice.dto.auth.RegisterRequest;
import org.wierzbadev.userservice.dto.publish.UserVerifyDto;
import org.wierzbadev.userservice.exception.UserError;
import org.wierzbadev.userservice.exception.UserException;
import org.wierzbadev.userservice.model.BannedUser;
import org.wierzbadev.userservice.model.Role;
import org.wierzbadev.userservice.model.User;
import org.wierzbadev.userservice.repository.BannedUserRepository;
import org.wierzbadev.userservice.repository.UserRepository;
import org.wierzbadev.userservice.service.UserService;
import org.wierzbadev.userservice.service.publisher.UserDtoEventPublisher;
import org.wierzbadev.userservice.service.publisher.UserEventPublisher;
import org.wierzbadev.userservice.service.publisher.UserNotificationEventPublisher;
import org.wierzbadev.userservice.utils.HashUtils;

import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.*;
import java.util.regex.Pattern;

@Slf4j
@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final BannedUserRepository bannedRepository;
    private final UserEventPublisher userEventPublisher;
    private final UserNotificationEventPublisher deletedEventPublisher;
    private final UserDtoEventPublisher dtoEventPublisher;


    public AuthenticationService(AuthenticationManager authenticationManager, UserService userService, JwtService jwtService, PasswordEncoder passwordEncoder, UserRepository userRepository, BannedUserRepository bannedRepository, UserEventPublisher userEventPublisher, UserNotificationEventPublisher deletedEventPublisher, UserDtoEventPublisher dtoEventPublisher) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.bannedRepository = bannedRepository;
        this.userEventPublisher = userEventPublisher;
        this.deletedEventPublisher = deletedEventPublisher;
        this.dtoEventPublisher = dtoEventPublisher;
    }

    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserException(UserError.USER_ALREADY_EXISTS);
        }

        if (userService.isUserBanned(request.getEmail())) {
            throw new UserException(UserError.USER_IS_BANNED);
        }

        if (!request.getPassword().equals(request.getRepeatPassword())) {
            throw new UserException(UserError.USER_PASSWORD_NOT_MATCH);
        }

        if (!Pattern.matches("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$", request.getPassword())) {
            throw new UserException(UserError.USER_PASSWORD_IS_WEAK);
        }

        User user = new User();
        user.setName(request.getName());
        user.setSurname(request.getSurname());
        user.setEmail(request.getEmail());
        user.setBirthday(request.getBirthday());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        UUID uuid = UUID.randomUUID();
        user.setUuid(uuid);
        user.setVerify(false);

        log.info("Role before save {}", user.getRole());
        User savedUser = userService.createUser(user);
        log.info("Role after save {}", user.getRole());
        dtoEventPublisher.sendVerifyCode(
                new UserVerifyDto(savedUser.getId(), savedUser.getEmail(),savedUser.getName(), savedUser.getUuid().toString())
        );
    }

    public AuthResponse login(AuthRequest request) {
        User user = userService.readUserByEmail(request.getEmail());


        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UserException(UserError.USER_INVALID_CREDENTIALS);
        }

        if (!user.isVerify()) {
            throw new UserException(UserError.USER_NOT_VERIFIED);
        }

        log.info("User with id: {} logged in", user.getId());

        String accessToken = jwtService.generateToken(user.getEmail(), user.getId(), List.of(user.getRole().name()), true);
        String refreshToken = jwtService.generateRefreshToken(user.getEmail(), user.getId(), List.of(user.getRole().name()), true);

        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse refresh(String refreshToken) {
        String email = jwtService.extractEmail(refreshToken);

        if (!jwtService.isRefreshTokenValid(refreshToken, email)) {
            throw new UserException(UserError.USER_INVALID_TOKEN);
        }

        User user = userService.readUserByEmail(email);

        if (!user.isVerify()) {
            throw new UserException(UserError.USER_NOT_VERIFIED);
        }

        log.info("Created new token for user: {}", user.getId());

        String newAccessToken = jwtService.generateToken(user.getEmail(), user.getId(), List.of(user.getRole().name()), true);

        return new AuthResponse(newAccessToken, refreshToken);
    }

    @Transactional
    @CacheEvict(value = "cacheUser", key = "'#id'")
    public void addUserToBlacklist(String auth) {

        if (auth == null || auth.trim().isEmpty()) {
            throw new UserException(UserError.USER_EMPTY_TOKEN);
        }

        auth = auth.replace("Bearer ", "");

        String email = jwtService.extractEmail(auth);
        User toBan = userService.readUserByEmail(email);
        jwtService.banUser(toBan.getEmail());

        log.info("Deleted user with id: {}", toBan.getId());
        deletedEventPublisher.notifyWordService(toBan.getId());
        BannedUser bannedUser = new BannedUser();
        bannedUser.setEmail(HashUtils.hashEmail(toBan.getEmail()));
        bannedRepository.save(bannedUser);
        userEventPublisher.sendUserDeletedEvent(toBan.getId());
        userRepository.delete(toBan);
    }

    public Map<String, Object> getJwks() {
        PublicKey publicKey = jwtService.getPublicKey();
        RSAPublicKey rsaPublicKey = (RSAPublicKey) publicKey;

        Map<String, Object> jwk = new HashMap<>();
        jwk.put("kty", "RSA");
        jwk.put("alg", "RS256");
        jwk.put("use", "sig");
        jwk.put("n", Base64.getUrlEncoder().withoutPadding().encodeToString(rsaPublicKey.getModulus().toByteArray()));
        jwk.put("e", Base64.getUrlEncoder().withoutPadding().encodeToString(rsaPublicKey.getPublicExponent().toByteArray()));

        log.info("Returned public key for any microservice");

        return Collections.singletonMap("keys", Collections.singletonList(jwk));
    }

    public Long getCurrentId(String authorization) {
        String token = authorization.replace("Bearer ", "");
        return jwtService.extractId(token);
    }

    public boolean isUserAdmin(String token) {
        token = token.replace("Bearer ", "");
        List<String> result = jwtService.extractRole(token);

        for (String role: result ) {
            if (role.equals(Role.ADMIN.name())) {
                return true;
            }
        }
        return false;
    }
}

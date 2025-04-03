package org.wierzbadev.userservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wierzbadev.userservice.dto.projection.PageResponse;
import org.wierzbadev.userservice.dto.projection.UserAdminDto;
import org.wierzbadev.userservice.dto.projection.UserShortInfo;
import org.wierzbadev.userservice.dto.publish.UserDto;
import org.wierzbadev.userservice.dto.publish.UserEmailDto;
import org.wierzbadev.userservice.exception.UserError;
import org.wierzbadev.userservice.exception.UserException;
import org.wierzbadev.userservice.model.User;
import org.wierzbadev.userservice.repository.BannedUserRepository;
import org.wierzbadev.userservice.repository.UserRepository;
import org.wierzbadev.userservice.service.auth.JwtService;
import org.wierzbadev.userservice.service.publisher.UserEventPublisher;
import org.wierzbadev.userservice.utils.HashUtils;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository repository;
    private final BannedUserRepository bannedRepository;
    private final UserEventPublisher publisher;
    private final JwtService jwtService;

    public UserService(PasswordEncoder passwordEncoder, UserRepository repository, BannedUserRepository bannedRepository, UserEventPublisher publisher, JwtService jwtService) {
        this.passwordEncoder = passwordEncoder;
        this.repository = repository;
        this.bannedRepository = bannedRepository;
        this.publisher = publisher;
        this.jwtService = jwtService;
    }

    public PageResponse<UserAdminDto> readAllUsers(Pageable pageable) {
        Page<User> userPage = repository.findAll(pageable);

        List<UserAdminDto> userAdminDtos = userPage.getContent().stream()
                .map(user -> new UserAdminDto(
                        user.getId(),
                        user.getName(),
                        user.getSurname(),
                        user.getEmail(),
                        user.getBirthday(),
                        user.getRole(),
                        user.isVerify(),
                        user.getUuid()
                )).toList();
        return new PageResponse<>(
            userAdminDtos,
            userPage.getTotalElements(),
            userPage.getTotalPages(),
            userPage.getSize(),
            userPage.getNumber()
        );
    }

    public UserAdminDto readUserById(long id) {
        log.info("Showed user with id: {}", id);
        return repository.findById(id).map(user -> new UserAdminDto(
                        user.getId(),
                        user.getName(),
                        user.getSurname(),
                        user.getEmail(),
                        user.getBirthday(),
                user.getRole(),
                user.isVerify(),
                user.getUuid()
                ))
                .orElseThrow(() -> new UserException(UserError.USER_NOT_FOUND));
    }

    @Cacheable(value = "cacheUser", key = "'#id'")
    public UserDto readUserForRanking(long id) {
        UserAdminDto user = readUserById(id);
        return new UserDto(id, user.getName());
    }

    public List<UserDto> readUsersForRanking(List<Long> userIds) {
        List<User> users =  repository.findAllById(userIds);

        return users.stream()
                .map(user -> new UserDto(user.getId(), user.getName()))
                .toList();
    }

    public User readUserByEmail(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new UserException(UserError.USER_NOT_FOUND));
    }

    public User createUser(User user) {
        if (repository.findByEmail(user.getEmail()).isPresent())
            throw new UserException(UserError.USER_ALREADY_EXISTS);

        log.info("Created user with id: {} and email: {}", user.getId(), user.getEmail());

        repository.save(user);
        return user;
    }

    public UserShortInfo readShortInfoById(long id) {
        UserAdminDto user = readUserById(id);
        return new UserShortInfo(user.getName(), user.getSurname(), user.getEmail(), user.getBirthday());
    }

    @CacheEvict(value = "cacheUser", key = "'#id'")
    public UserAdminDto patchUser(long id, User toUpdate) {
        User fromDb = repository.findById(id)
                .orElseThrow(() -> new UserException(UserError.USER_NOT_FOUND));
        validateUser(fromDb, toUpdate);
        repository.save(fromDb);
        publisher.sendUserUpdatedEvent(id);

        logUserChangeData(fromDb);

        return new UserAdminDto(fromDb.getId(), fromDb.getName(), fromDb.getSurname(), fromDb.getEmail(),
                fromDb.getBirthday(), fromDb.getRole(), fromDb.isVerify(), fromDb.getUuid());
    }

    @Transactional
    @CacheEvict(value = "cacheUser", key = "'#id'")
    public UserAdminDto putUser(long id, User toUpdate) {
        User fromDb = repository.findById(id)
                .orElseThrow(() -> new UserException(UserError.USER_NOT_FOUND));
        fromDb.setName(toUpdate.getName());
        fromDb.setSurname(toUpdate.getSurname());
        fromDb.setBirthday(toUpdate.getBirthday());
        fromDb.setEmail(toUpdate.getEmail());
        repository.save(fromDb);
        publisher.sendUserUpdatedEvent(id);

        logUserChangeData(fromDb);

        return new UserAdminDto(fromDb.getId(), fromDb.getName(), fromDb.getSurname(), fromDb.getEmail(),
                fromDb.getBirthday(), fromDb.getRole(), fromDb.isVerify(), fromDb.getUuid());
    }

    public boolean isUserBanned(String email) {
        String emailHash = HashUtils.hashEmail(email);
        return bannedRepository.existsByEmail(emailHash);
    }

    public List<UserEmailDto> readUserEmailDto(List<Long> usersId) {
        List<User> users = repository.findAllById(usersId);

        log.info("Returned list of UserEmailDto with size: {}", users.size());

        return users.stream()
                .map(user -> new UserEmailDto(user.getId(), user.getEmail(), user.getName()))
                .toList();
    }

    public void changeVerifyStatus(String uuid) {
        User user = repository.findUserByUuid(UUID.fromString(uuid));
        user.setVerify(true);

        log.info("Verified user with id: {}", user.getId());

        repository.save(user);
    }

    private void validateUser(User fromDb, User toUpdate) {
        if (toUpdate.getName() != null)
            fromDb.setName(toUpdate.getName());
        if (toUpdate.getSurname() != null)
            fromDb.setSurname(toUpdate.getSurname());
        if (toUpdate.getBirthday() != null)
            fromDb.setBirthday(toUpdate.getBirthday());
        if (toUpdate.getEmail() != null)
            fromDb.setEmail(toUpdate.getEmail());
        if (toUpdate.getRole() != null)
            fromDb.setRole(toUpdate.getRole());
    }

    private void logUserChangeData(User user) {
        log.info("User: {} changed his data", user.getId());
    }
}

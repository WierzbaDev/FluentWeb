package org.wierzbadev.userservice.integration.service;


import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.wierzbadev.userservice.exception.UserError;
import org.wierzbadev.userservice.exception.UserException;
import org.wierzbadev.userservice.model.Role;
import org.wierzbadev.userservice.model.User;
import org.wierzbadev.userservice.repository.UserRepository;
import org.wierzbadev.userservice.service.UserService;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Tag("integration")
@SpringBootTest
@ActiveProfiles("test")
class UserServiceIntegrationTest {

    @Autowired
    private UserRepository repository;

    @Autowired
    private UserService userService;

    @Test
    void shouldSaveUserInDatabase() {
        User user = User.builder()
                        .name("Paweł")
                        .surname("Kowalski")
                        .email("example@wp.pl")
                        .birthday(LocalDate.now())
                        .password("password")
                        .role(Role.USER)
                                .build();


        userService.createUser(user);

        var result = userService.readUserByEmail("example@wp.pl");

        assertEquals("Paweł", result.getName());
        assertEquals("Kowalski", result.getSurname());
        assertEquals("example@wp.pl", result.getEmail());
        assertEquals(LocalDate.now(), result.getBirthday());
        assertEquals(Role.USER, result.getRole());
    }

    @Test
    void should_Throw_UserException_When_User_With_TheSameEmailExists() {
        User user = User.builder()
                .name("Andrzej")
                .surname("Kowalski")
                .email("example2@wp.pl")
                .birthday(LocalDate.now())
                .password("password")
                .role(Role.USER)
                .build();

        User userNext = User.builder()
                .name("Kacper")
                .surname("Kowalski")
                .email("example2@wp.pl")
                .birthday(LocalDate.now())
                .password("password")
                .role(Role.USER)
                .build();

        userService.createUser(user);

        Exception exception = assertThrows(UserException.class, () -> {
           userService.createUser(userNext);
        });

        assertEquals(UserError.USER_ALREADY_EXISTS.getMessage(), exception.getMessage());
    }

    @Test
    void patchUser_shouldThrow_UserException_whenUserDoesNotExits() {
        User user = User.builder()
                .name("Andrzej")
                .surname("Kowalski")
                .email("example3@wp.pl")
                .birthday(LocalDate.now())
                .password("password")
                .role(Role.USER)
                .build();

        Exception exception = assertThrows(UserException.class, () -> {
           userService.patchUser(Long.MAX_VALUE, user);
        });

        assertEquals(UserError.USER_NOT_FOUND.getMessage(), exception.getMessage());
    }

    @Test
    void patchUser_shouldUpdate() {
        User user = User.builder()
                .name("Andrzej")
                .surname("Kowalski")
                .email("example4@wp.pl")
                .birthday(LocalDate.now())
                .password("password")
                .role(Role.USER)
                .build();

        userService.createUser(user);

        User result = userService.readUserByEmail(user.getEmail());

        User toUpdate = new User();
        toUpdate.setName("Patch");

        var toTest = userService.patchUser(result.getId(), toUpdate);

        assertEquals("Patch", toTest.getName());
    }
}
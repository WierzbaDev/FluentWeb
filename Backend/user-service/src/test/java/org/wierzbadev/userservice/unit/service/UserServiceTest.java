package org.wierzbadev.userservice.unit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.wierzbadev.userservice.dto.projection.UserAdminDto;
import org.wierzbadev.userservice.model.User;
import org.wierzbadev.userservice.repository.BannedUserRepository;
import org.wierzbadev.userservice.repository.UserRepository;
import org.wierzbadev.userservice.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private BannedUserRepository bannedUserRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test readAllUsers")
    void testReadAllUsers() {
        User user = new User();
        Pageable pageable = PageRequest.of(0, 20);
        Page<User> page = new PageImpl<>(Collections.singletonList(user), pageable, 1);

        when(repository.findAll(any(Pageable.class))).thenReturn(page);

        List<UserAdminDto> users = userService.readAllUsers(pageable).content().stream()
                .map(userAdminDto -> new UserAdminDto(
                        userAdminDto.getUserId(),
                        userAdminDto.getName(),
                        userAdminDto.getSurname(),
                        userAdminDto.getEmail(),
                        userAdminDto.getBirthday(),
                        userAdminDto.getRole(),
                        userAdminDto.isVerify(),
                        userAdminDto.getVeryficationUuid()
                )).toList();


        assertNotNull(users);
        assertEquals(1, users.size());

        verify(repository, times(1)).findAll(any(Pageable.class));
    }


    @Test
    @DisplayName("Test readById")
    void testReadUserById() {
        User user = new User();
        when(repository.findById(anyLong())).thenReturn(Optional.of(user));

        UserAdminDto result = userService.readUserById(1);

        assertNotNull(result);
        verify(repository, times(1)).findById(anyLong());
    }
}
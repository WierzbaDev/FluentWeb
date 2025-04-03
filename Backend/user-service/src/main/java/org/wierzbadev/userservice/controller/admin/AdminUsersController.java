package org.wierzbadev.userservice.controller.admin;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wierzbadev.userservice.dto.projection.PageResponse;
import org.wierzbadev.userservice.dto.projection.UserAdminDto;
import org.wierzbadev.userservice.model.User;
import org.wierzbadev.userservice.service.UserService;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/api/admin/users")
public class AdminUsersController {

    private final UserService service;

    public AdminUsersController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<PageResponse<UserAdminDto>> readAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Exposing all the users!");
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(service.readAllUsers(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserAdminDto> readUserById(@PathVariable("id") long id) {
        return ResponseEntity.ok(service.readUserById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserAdminDto> putUser(@PathVariable("id") long id, @RequestBody @Valid User user) {
        UserAdminDto result = service.putUser(id, user);
        return ResponseEntity.created(URI.create("/" + id)).body(result);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserAdminDto> patchUser(@PathVariable("id") long id, @RequestBody User user) {
        UserAdminDto result = service.patchUser(id, user);
        return ResponseEntity.created(URI.create("/" + id)).body(result);
    }
}

package org.wierzbadev.userservice.controller.user;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wierzbadev.userservice.dto.projection.UserAdminDto;
import org.wierzbadev.userservice.dto.projection.UserShortInfo;
import org.wierzbadev.userservice.model.User;
import org.wierzbadev.userservice.model.password.PasswordChanger;
import org.wierzbadev.userservice.service.PasswordResetService;
import org.wierzbadev.userservice.service.UserService;
import org.wierzbadev.userservice.service.auth.AuthenticationService;

import java.net.URI;

@RestController
@RequestMapping("/api/user/account")
public class UserProfileController {

    private final UserService service;
    private final AuthenticationService authService;
    private final PasswordResetService resetService;

    public UserProfileController(UserService service, AuthenticationService authService, PasswordResetService resetService) {
        this.service = service;
        this.authService = authService;
        this.resetService = resetService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserAdminDto> readMyData(@RequestHeader("Authorization") String authorization) {
        long id = authService.getCurrentId(authorization);
        return ResponseEntity.ok(service.readUserById(id));
    }

    @GetMapping("/info")
    public ResponseEntity<UserShortInfo> readShortData(@RequestHeader("Authorization") String authorization) {
        long id = authService.getCurrentId(authorization);
        return ResponseEntity.ok(service.readShortInfoById(id));
    }

    @GetMapping("/isAdmin")
    public ResponseEntity<Boolean> userIdAdmin(@RequestHeader("Authorization") String authorization) {
        boolean result = authService.isUserAdmin(authorization);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> resetPassword(@RequestHeader("Authorization") String authorization,
            @RequestBody @Valid PasswordChanger passwordChanger) {
        long id = authService.getCurrentId(authorization);
        resetService.resetPasswordLoggedInUser(id, passwordChanger);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/me/edit")
    public ResponseEntity<UserAdminDto> changeMyData(
            @RequestHeader("Authorization") String authorization, @RequestBody @Valid User user) {
        long id = authService.getCurrentId(authorization);
        UserAdminDto result = service.patchUser(id, user);
        return ResponseEntity.created(URI.create("/" + id)).body(result);
    }

    @PatchMapping("/me/edit")
    public ResponseEntity<UserAdminDto> changeMyField(
            @RequestHeader("Authorization") String authorization, @RequestBody User user) {
        long id = authService.getCurrentId(authorization);
        UserAdminDto result = service.patchUser(id, user);
        return ResponseEntity.created(URI.create("/" + id)).body(result);
    }

    @PostMapping("/forget")
    public ResponseEntity<Void> forgetUser(@RequestHeader("Authorization") String authorization) {
        System.out.println("Token: " + authorization);
        authService.addUserToBlacklist(authorization);
        return ResponseEntity.noContent().build();
    }
}

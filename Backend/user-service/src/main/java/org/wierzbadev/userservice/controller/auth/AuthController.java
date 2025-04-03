package org.wierzbadev.userservice.controller.auth;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wierzbadev.userservice.dto.auth.AuthRequest;
import org.wierzbadev.userservice.dto.auth.AuthResponse;
import org.wierzbadev.userservice.dto.auth.ForgotPasswordRequest;
import org.wierzbadev.userservice.dto.auth.RegisterRequest;
import org.wierzbadev.userservice.model.password.PasswordChanger;
import org.wierzbadev.userservice.service.PasswordResetService;
import org.wierzbadev.userservice.service.UserService;
import org.wierzbadev.userservice.service.auth.AuthenticationService;
import org.wierzbadev.userservice.service.auth.JwtService;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationService authService;
    private final JwtService jwtService;
    private final UserService userService;
    private final PasswordResetService resetService;

    public AuthController(AuthenticationService authService, JwtService jwtService, UserService userService, PasswordResetService resetService) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.userService = userService;
        this.resetService = resetService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok("Your account has been successfully registered!");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestParam String refreshToken) {
        return ResponseEntity.ok(authService.refresh(refreshToken));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        resetService.sendResetPasswordEmail(request.getEmail());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestHeader("X-Reset-Token") String token,
                                                @RequestBody @Valid PasswordChanger passwordChanger) {
        resetService.resetPassword(token, passwordChanger);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/.well-known/jwks.json")
    public ResponseEntity<Map<String, Object>> readPublicKey() {
        return ResponseEntity.ok(authService.getJwks());
    }

    @GetMapping("/system/validate")
    public ResponseEntity<Boolean> validateToken(@RequestHeader("Authorization") String token, String email) {
        boolean isValid = jwtService.isTokenValid(token, email);
        return ResponseEntity.ok(isValid);
    }

    @GetMapping("/verify")
    public ResponseEntity<Void> verifyUser(@RequestParam String uuid) {
        userService.changeVerifyStatus(uuid);
        return ResponseEntity.noContent().build();
    }
}

package com.gaurav.trading;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public AuthService.AuthResponse register(@Valid @RequestBody CredentialsRequest request) {
        return authService.register(request.username(), request.password());
    }

    @PostMapping("/login")
    public AuthService.AuthResponse login(@Valid @RequestBody CredentialsRequest request) {
        return authService.login(request.username(), request.password());
    }

    public record CredentialsRequest(
            @NotBlank @Size(min = 3, max = 50) String username,
            @NotBlank @Size(min = 6, max = 72) String password
    ) {
    }
}

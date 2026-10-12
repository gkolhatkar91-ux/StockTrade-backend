package com.gaurav.trading;

import java.math.BigDecimal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final AppUserRepository userRepository;
    private final WalletRepository walletRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(AppUserRepository userRepository, WalletRepository walletRepository,
                       PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(String username, String password) {
        String normalizedUsername = username.trim().toLowerCase();
        if (userRepository.existsByUsername(normalizedUsername)) {
            throw new IllegalArgumentException("Username is already taken");
        }

        AppUser user = userRepository.save(new AppUser(normalizedUsername, passwordEncoder.encode(password)));
        walletRepository.save(new Wallet(user, new BigDecimal("100000.00")));
        return new AuthResponse("Registration successful", normalizedUsername, jwtService.generateToken(normalizedUsername));
    }

    public AuthResponse login(String username, String password) {
        String normalizedUsername = username.trim().toLowerCase();
        AppUser user = userRepository.findByUsername(normalizedUsername)
                .orElseThrow(() -> new SecurityException("Invalid username or password"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new SecurityException("Invalid username or password");
        }

        return new AuthResponse("Login successful", normalizedUsername, jwtService.generateToken(normalizedUsername));
    }

    public record AuthResponse(String message, String username, String token) {
    }
}

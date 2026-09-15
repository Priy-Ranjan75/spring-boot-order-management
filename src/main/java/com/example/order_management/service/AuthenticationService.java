package com.example.order_management.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.order_management.dto.LoginRequest;
import com.example.order_management.dto.LoginResponse;
import com.example.order_management.dto.RegisterRequest;
import com.example.order_management.entity.AppUser;
import com.example.order_management.repository.AppUserRepository;

@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            AppUserRepository appUserRepository,
            PasswordEncoder passwordEncoder) {

        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse login(LoginRequest request) {

        Authentication authentication = authenticationManager
                .authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getUsername(),
                                request.getPassword()
                        )
                );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String token = jwtService.generateToken(userDetails);

        return new LoginResponse(token);
    }

    public String register(RegisterRequest request) {

        // Check duplicate username
        if (appUserRepository.existsByUsername(request.getUsername())) {
            return "Username already exists";
        }

        // Check duplicate email
        if (appUserRepository.existsByEmail(request.getEmail())) {
            return "Email already exists";
        }

        AppUser user = new AppUser();

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());

        // Encode password before saving
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        /*
         * First registered user becomes ADMIN.
         * All users registered after the first user become USER.
         */
        if (appUserRepository.count() == 0) {
            user.setRole("ADMIN");
        } else {
            user.setRole("USER");
        }

        appUserRepository.save(user);

        return "User registered successfully with role: " + user.getRole();
    }
}
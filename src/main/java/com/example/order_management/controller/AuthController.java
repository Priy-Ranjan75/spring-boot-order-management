package com.example.order_management.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.order_management.dto.LoginRequest;
import com.example.order_management.dto.LoginResponse;
import com.example.order_management.dto.RegisterRequest;
import com.example.order_management.service.AuthenticationService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthenticationService authenticationService;

	public AuthController(AuthenticationService authenticationService) {

		this.authenticationService = authenticationService;
	}

	@PostMapping("/register")
	public ResponseEntity<String> register(@RequestBody RegisterRequest request) {

		return ResponseEntity.ok(authenticationService.register(request));
	}

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

		return ResponseEntity.ok(authenticationService.login(request));
	}
}
package com.example.routeplaner.controller;

import com.example.routeplaner.model.User;
import com.example.routeplaner.model.LoginRequest;
import com.example.routeplaner.model.LoginResponse;
import com.example.routeplaner.model.RegisterRequest;
import com.example.routeplaner.repository.UserRepository;
import com.example.routeplaner.service.UserService;
import com.example.routeplaner.utils.JwtUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        Optional<User> existingUser = userService.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            return ResponseEntity.badRequest().body("Username is already taken");
        }
        User savedUser = userService.saveUser(user);
        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpSession session) {
        User user = userService.authenticateUser(request.getUsername(), request.getPassword());
        String token = JwtUtil.generateToken(user.getUsername());
        session.setAttribute("userId", user.getId());
        session.setAttribute("username", user.getUsername());
        return ResponseEntity.ok(new LoginResponse(token));
    }
}

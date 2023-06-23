package lk.directpay.Login.controllers;

import lk.directpay.Login.entities.User;
import lk.directpay.Login.repositories.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {
//    @PostMapping("/register")
//    public ResponseEntity<String> registerUser(@RequestBody User user) {
//        if (userRepository.findByUsername(user.getUsername()) != null) {
//            return ResponseEntity.badRequest().body("Username already exists");
//        }
//
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
//        userRepository.save(user);
//
//        return ResponseEntity.ok("User registered successfully");
//    }

//    @PostMapping("/login")
//    public ResponseEntity<String> loginUser(@RequestBody User user) {
//        User storedUser = userRepository.findByUsername(user.getUsername());
//
//        if (storedUser == null || !passwordEncoder.matches(user.getPassword(), storedUser.getPassword())) {
//            return ResponseEntity.badRequest().body("Invalid username or password");
//        }
//
//        return ResponseEntity.ok("User logged in successfully");
//    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }
}


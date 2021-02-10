package com.udacity.ecommerce.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.udacity.ecommerce.model.persistence.Cart;
import com.udacity.ecommerce.model.persistence.User;
import com.udacity.ecommerce.model.persistence.repositories.CartRepository;
import com.udacity.ecommerce.model.persistence.repositories.UserRepository;
import com.udacity.ecommerce.model.requests.CreateUserRequest;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private UserRepository userRepository;
    private CartRepository cartRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserController(
            UserRepository userRepository,
            CartRepository cartRepository,
            BCryptPasswordEncoder bCryptPasswordEncoder
    ) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id) {
        return ResponseEntity.of(userRepository.findById(id));
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> findByUserName(@PathVariable String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            log.info("Could not find user " + username);
            return ResponseEntity.notFound().build();
        } else {
            log.info("User retrieved successfully.");
            return ResponseEntity.ok(user);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
        User user = new User();
        user.setUsername(createUserRequest.getUsername());
        Cart cart = new Cart();
        cartRepository.save(cart);
        user.setCart(cart);

        if (createUserRequest.getPassword().length() < MIN_PASSWORD_LENGTH) {
            log.info(String.format("Error - Password should contain at least %s characters", MIN_PASSWORD_LENGTH));
            return ResponseEntity.badRequest().build();
        }

        if (!createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())) {
            log.info("Error - Password confirmation does not match");
            return ResponseEntity.badRequest().build();
        }

        user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));

        userRepository.save(user);
        log.info("User created successfully.");
        return ResponseEntity.ok(user);
    }

}
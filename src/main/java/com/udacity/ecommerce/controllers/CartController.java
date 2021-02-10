package com.udacity.ecommerce.controllers;

import java.util.Optional;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.udacity.ecommerce.model.persistence.Cart;
import com.udacity.ecommerce.model.persistence.Item;
import com.udacity.ecommerce.model.persistence.User;
import com.udacity.ecommerce.model.persistence.repositories.CartRepository;
import com.udacity.ecommerce.model.persistence.repositories.ItemRepository;
import com.udacity.ecommerce.model.persistence.repositories.UserRepository;
import com.udacity.ecommerce.model.requests.ModifyCartRequest;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private static final Logger log = LoggerFactory.getLogger(CartController.class);

    private UserRepository userRepository;
    private CartRepository cartRepository;
    private ItemRepository itemRepository;

    public CartController(
            UserRepository userRepository,
            CartRepository cartRepository,
            ItemRepository itemRepository
    ) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.itemRepository = itemRepository;
    }

    @PostMapping("/addToCart")
    public ResponseEntity<Cart> addToCart(@RequestBody ModifyCartRequest request) {
        User user = userRepository.findByUsername(request.getUsername());
        if (user == null) {
            log.info("Error - Could not find user with username " + request.getUsername());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Optional<Item> item = itemRepository.findById(request.getItemId());
        if (!item.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Cart cart = user.getCart();
        IntStream.range(0, request.getQuantity())
                .forEach(i -> cart.addItem(item.get()));
        cartRepository.save(cart);

        log.info("Cart saved successfully.");
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/removeFromCart")
    public ResponseEntity<Cart> removeFromCart(@RequestBody ModifyCartRequest request) {
        User user = userRepository.findByUsername(request.getUsername());
        if (user == null) {
            log.info("Error - Could not find user with username " + request.getUsername());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Optional<Item> item = itemRepository.findById(request.getItemId());
        if (!item.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Cart cart = user.getCart();
        IntStream.range(0, request.getQuantity())
                .forEach(i -> cart.removeItem(item.get()));
        cartRepository.save(cart);

        log.info("Cart saved successfully.");
        return ResponseEntity.ok(cart);
    }

}
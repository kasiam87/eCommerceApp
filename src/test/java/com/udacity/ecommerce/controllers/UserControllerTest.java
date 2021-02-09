package com.udacity.ecommerce.controllers;

import com.udacity.ecommerce.model.persistence.Cart;
import com.udacity.ecommerce.model.persistence.User;
import com.udacity.ecommerce.model.persistence.repositories.CartRepository;
import com.udacity.ecommerce.model.persistence.repositories.UserRepository;
import com.udacity.ecommerce.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController userController;

    private UserRepository userRepository = mock(UserRepository.class);

    private CartRepository cartRepository = mock(CartRepository.class);

    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setup() {
        Mockito.reset(userRepository, cartRepository, encoder);
        userController = new UserController(userRepository, cartRepository, encoder);
    }

    @Test
    public void createUser() {

        // given
        when(encoder.encode("testPassword")).thenReturn("hashedPassword");
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setUsername("test");
        userRequest.setPassword("testPassword");
        userRequest.setConfirmPassword("testPassword");

        // when
        ResponseEntity<User> response = userController.createUser(userRequest);

        // then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().getId());
        assertEquals("test", response.getBody().getUsername());
        assertEquals("hashedPassword", response.getBody().getPassword());
        verify(cartRepository).save(any(Cart.class));
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void createUserWithTooShortPassword() {
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setUsername("test");
        userRequest.setPassword("test");
        userRequest.setConfirmPassword("test");

        ResponseEntity<User> response = userController.createUser(userRequest);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void createUserWithWrongRepeatedPassword() {
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setUsername("test");
        userRequest.setPassword("test");
        userRequest.setConfirmPassword("test2");

        ResponseEntity<User> response = userController.createUser(userRequest);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void findUserById() {
        // given
        User user = new User();
        user.setUsername("test");
        user.setId(0);
        when(userRepository.findById((long) 0)).thenReturn(Optional.of(user));

        // when
        ResponseEntity<User> userResponse = userController.findById((long) 0);

        assertNotNull(userResponse);
        assertEquals(200, userResponse.getStatusCodeValue());
        assertNotNull(userResponse.getBody());
        assertEquals(0, userResponse.getBody().getId());
        assertEquals("test", userResponse.getBody().getUsername());
    }

    @Test
    public void findUserByName() {
        // given
        User user = new User();
        user.setUsername("test");
        user.setId(0);
        when(userRepository.findByUsername("test")).thenReturn(user);

        // when
        ResponseEntity<User> userResponse = userController.findByUserName("test");

        assertNotNull(userResponse);
        assertEquals(200, userResponse.getStatusCodeValue());
        assertNotNull(userResponse.getBody());
        assertEquals(0, userResponse.getBody().getId());
        assertEquals("test", userResponse.getBody().getUsername());
    }

    @Test
    public void findUserByNameReturnNotFoundWhenUserDoesNotExist() {
        // given
        when(userRepository.findByUsername("test")).thenReturn(null);

        // when
        ResponseEntity<User> userResponse = userController.findByUserName("test");

        assertNotNull(userResponse);
        assertEquals(404, userResponse.getStatusCodeValue());
    }
}

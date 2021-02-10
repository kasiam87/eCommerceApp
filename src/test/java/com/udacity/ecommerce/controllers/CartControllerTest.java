package com.udacity.ecommerce.controllers;

import com.udacity.ecommerce.model.persistence.Cart;
import com.udacity.ecommerce.model.persistence.Item;
import com.udacity.ecommerce.model.persistence.User;
import com.udacity.ecommerce.model.persistence.repositories.CartRepository;
import com.udacity.ecommerce.model.persistence.repositories.ItemRepository;
import com.udacity.ecommerce.model.persistence.repositories.UserRepository;
import com.udacity.ecommerce.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CartControllerTest {

    private CartController cartController;

    private UserRepository userRepository = mock(UserRepository.class);

    private CartRepository cartRepository = mock(CartRepository.class);

    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setup() {
        Mockito.reset(userRepository, cartRepository, itemRepository);
        cartController = new CartController(userRepository, cartRepository, itemRepository);
    }

    @Test
    public void addToCart() {
        // given
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        User user = new User();
        user.setId(0L);

        Cart cart = new Cart();
        cart.setId((long) 0);
        user.setCart(cart);
        user.setUsername("test");

        Item item = new Item();
        item.setId(0L);
        item.setName("item name");
        item.setPrice(BigDecimal.valueOf(12.00));
        modifyCartRequest.setUsername(user.getUsername());
        modifyCartRequest.setItemId(item.getId());
        modifyCartRequest.setQuantity(5);

        when(userRepository.findByUsername("test")).thenReturn(user);
        when(itemRepository.findById(0L)).thenReturn(Optional.of(item));

        // when
        ResponseEntity<Cart> response = cartController.addToCart(modifyCartRequest);

        // then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        verify(cartRepository).save(cart);
    }

    @Test
    public void addToCartReturnNotFoundWhenUserDoesNotExist() {
        // given
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();

        Item item = new Item();
        item.setId(0L);
        item.setName("item name");
        item.setPrice(BigDecimal.valueOf(12.00));
        modifyCartRequest.setItemId(item.getId());
        modifyCartRequest.setQuantity(5);

        when(itemRepository.findById(0L)).thenReturn(Optional.of(item));

        // when
        ResponseEntity<Cart> response = cartController.addToCart(modifyCartRequest);

        // then
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void addToCartReturnNotFoundWhenThereIsNoItem() {
        // given
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        User user = new User();
        user.setId(0L);

        Cart cart = new Cart();
        cart.setId((long) 0);
        user.setCart(cart);
        user.setUsername("test");

        Item item = new Item();
        item.setId(0L);
        item.setName("item name");
        item.setPrice(BigDecimal.valueOf(12.00));
        modifyCartRequest.setUsername(user.getUsername());
        modifyCartRequest.setItemId(item.getId());
        modifyCartRequest.setQuantity(5);

        when(userRepository.findByUsername("test")).thenReturn(user);

        // when
        ResponseEntity<Cart> response = cartController.addToCart(modifyCartRequest);

        // then
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }
}

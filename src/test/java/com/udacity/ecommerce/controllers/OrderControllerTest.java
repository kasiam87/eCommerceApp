package com.udacity.ecommerce.controllers;

import com.udacity.ecommerce.model.persistence.Cart;
import com.udacity.ecommerce.model.persistence.Item;
import com.udacity.ecommerce.model.persistence.User;
import com.udacity.ecommerce.model.persistence.UserOrder;
import com.udacity.ecommerce.model.persistence.repositories.OrderRepository;
import com.udacity.ecommerce.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    private OrderController orderController;

    private UserRepository userRepository = mock(UserRepository.class);
    private OrderRepository orderRepository = mock(OrderRepository.class);

    @Before
    public void setup() {
        Mockito.reset(userRepository, orderRepository);
        orderController = new OrderController(userRepository, orderRepository);
    }

    @Test
    public void submitOrder() {
        // given
        User user = new User();
        user.setUsername("test");
        user.setId(0);

        Cart cart = new Cart();
        cart.setId((long) 0);
        cart.setUser(user);
        Item item = new Item();
        item.setId(1L);
        item.setName("Some item");
        List<Item> items = Arrays.asList(item);
        cart.setItems(items);
        user.setCart(cart);

        UserOrder order = new UserOrder();
        order.setId(0L);
        order.setUser(user);

        when(userRepository.findByUsername("test")).thenReturn(user);

        // when
        ResponseEntity<UserOrder> response = orderController.submit("test");

        // then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertArrayEquals(items.toArray(), response.getBody().getItems().toArray());
        assertEquals(user, response.getBody().getUser());
        verify(orderRepository).save(any(UserOrder.class));
    }

    @Test
    public void submitOrderReturnNotFoundWhenUserDoesNotExist() {
        // given
        when(userRepository.findByUsername("test")).thenReturn(null);

        // when
        ResponseEntity<UserOrder> userOrderResponse = orderController.submit("test");

        assertNotNull(userOrderResponse);
        assertEquals(404, userOrderResponse.getStatusCodeValue());
    }

    @Test
    public void getOrdersForUser() {
        //given
        User user = new User();
        user.setUsername("test");
        user.setId(0);

        UserOrder order1 = new UserOrder();
        order1.setId((long) 0);
        order1.setUser(user);

        UserOrder order2 = new UserOrder();
        order2.setId((long) 1);
        order2.setUser(user);

        List<UserOrder> userOrders = Arrays.asList(order1, order2);
        when(userRepository.findByUsername("test")).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(userOrders);

        //when
        ResponseEntity<List<UserOrder>> userOrderResponse = orderController.getOrdersForUser("test");

        //then
        assertNotNull(userOrderResponse);
        assertEquals(200, userOrderResponse.getStatusCodeValue());
        assertNotNull(userOrderResponse.getBody());
        assertFalse(userOrderResponse.getBody().isEmpty());

        userOrderResponse.getBody().forEach(
                order ->
                assertEquals(order.getUser(), user)
        );
    }

    @Test
    public void getOrdersForUserReturnNotFoundWhenUserDoesNotExist(){
        // given
        when(userRepository.findByUsername("test")).thenReturn(null);

        // when
        ResponseEntity<List<UserOrder>> userOrderResponse = orderController.getOrdersForUser("test");

        assertNotNull(userOrderResponse);
        assertEquals(404, userOrderResponse.getStatusCodeValue());
    }
}

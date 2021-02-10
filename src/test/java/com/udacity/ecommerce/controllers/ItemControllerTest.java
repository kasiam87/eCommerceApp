package com.udacity.ecommerce.controllers;

import com.udacity.ecommerce.model.persistence.Item;
import com.udacity.ecommerce.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    private ItemController itemController;

    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setup() {
        Mockito.reset(itemRepository);
        itemController = new ItemController(itemRepository);
    }

    @Test
    public void getItems(){
        //given
        Item item1 = new Item();
        item1.setId(0L);
        item1.setName("Item1");
        item1.setPrice(BigDecimal.valueOf(12.00));
        when(itemRepository.findAll()).thenReturn(Arrays.asList(item1));

        // when
        ResponseEntity<List<Item>> response = itemController.getItems();

        //then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    public void getItemById(){
        //given
        Item item1 = new Item();
        item1.setId(0L);
        item1.setName("item name");
        item1.setPrice(BigDecimal.valueOf(12.00));
        when(itemRepository.findById(0L)).thenReturn(Optional.of(item1));

        // when
        ResponseEntity<Item> response = itemController.getItemById(0L);

        //then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("item name", response.getBody().getName());
    }

    @Test
    public void getItemByName(){
        //given
        Item item1 = new Item();
        item1.setId(0L);
        item1.setName("item name");
        item1.setPrice(BigDecimal.valueOf(12.00));
        when(itemRepository.findByName("item name")).thenReturn(Arrays.asList(item1));

        // when
        ResponseEntity<List<Item>> response = itemController.getItemsByName("item name");

        //then
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

    @Test
    public void getItemByNameReturnNotFoundWhenItemDoesNotExist(){
        //given
        when(itemRepository.findByName("item name")).thenReturn(null);

        // when
        ResponseEntity<List<Item>> response = itemController.getItemsByName("item name");

        //then
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void getItemByNameReturnNotFoundWhenItemsListIsEmpty(){
        //given
        when(itemRepository.findByName("item name")).thenReturn(Collections.emptyList());

        // when
        ResponseEntity<List<Item>> response = itemController.getItemsByName("item name");

        //then
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }
}

package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.ItemList;
import com.upgrad.FoodOrderingApp.api.model.ItemListResponse;
import com.upgrad.FoodOrderingApp.service.businness.ItemBusinessService;
import com.upgrad.FoodOrderingApp.service.businness.RestaurantBusinessService;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.UUID;

@Controller
@CrossOrigin
public class ItemController {

    @Autowired
    ItemBusinessService itemBusinessService; // Handles all the Service Related to Item.

    @Autowired
    RestaurantBusinessService restaurantBusinessService; // Handles all the Service Related to Restaurant.


    /* The method handles get Top Five Items By Popularity request & takes restaurant_id as the path variable
       & produces response in ItemListResponse and returns list of 5 items sold by restaurant on basis of popularity  with details from the db. If error returns error code and error message.
       */
    @RequestMapping(method = RequestMethod.GET, path = "/item/restaurant/{restaurant_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ItemListResponse> getItemByPopularity(@PathVariable("restaurant_id")final String restaurant_id)throws RestaurantNotFoundException {
        //Calls restaurantByUUID method of restaurantService to get the restaurant entity.
        RestaurantEntity restaurantEntity = restaurantBusinessService.restaurantByuuid(restaurant_id);

        //Calls getItemsByPopularity method of itemService to get the ItemEntity.
        List<ItemEntity> itemEntities = itemBusinessService.getItemByPopularity(restaurantEntity);

        //Creating the ItemListResponse details as required.
        ItemListResponse itemListResponse = new ItemListResponse();
        itemEntities.forEach(itemEntity -> {
            ItemList itemList = new ItemList()
                    .id(UUID.fromString(itemEntity.getUuid()))
                    .itemName(itemEntity.getItemName());
            itemListResponse.add(itemList);
        });

        return new ResponseEntity<ItemListResponse>(itemListResponse, HttpStatus.OK);
    }

}
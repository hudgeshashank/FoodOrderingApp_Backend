package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.*;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.InvalidRatingException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;


// Restaurant Controller Handles all  the restaurant related endpoints

@Controller
@RestController
@CrossOrigin
public class RestaurantController {
    @Autowired
    RestaurantBusinessService restaurantBusinessService; // Handles all the Service Related to Restaurant.

    @Autowired
    CategoryBusinessService categoryBusinessService; // Handles all the Service Related to category.

    @Autowired
    ItemBusinessService itemBusinessService; // Handles all the Service Related to item.

    @Autowired
    CustomerBusinessService customerBusinessService; // Handles all the Service Related to Customer.

    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET, path = "/restaurant",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getrestaurantList()
    {

        //Calls restaurantsByRating method of restaurantService to get the list of restaurant entity.
        List<RestaurantEntity> restaurantEntities = restaurantBusinessService.restaurantsByRating();

        //Creating restaurant list for the response
        List<RestaurantList> restaurantLists = new LinkedList<>();
        for (RestaurantEntity restaurantEntity : restaurantEntities) { //Looping for each restaurant entity in restaurantEntities

            //Calls  getCategoriesByRestaurant to get categories of the corresponding restaurant.
            List<CategoryEntity> categoryEntities = categoryBusinessService.getCategoriesByRestaurant(restaurantEntity.getUuid());
            String categories = new String();
            //To concat the category names.
            ListIterator<CategoryEntity> listIterator = categoryEntities.listIterator();
            while (listIterator.hasNext()){
                categories =  categories + listIterator.next().getCategoryName() ;
                if(listIterator.hasNext()){
                    categories = categories + ", ";
                }
            }

            //Creating the RestaurantDetailsResponseAddressState for the RestaurantDetailsResponseAddress
            RestaurantDetailsResponseAddressState restaurantDetailsResponseAddressState = new RestaurantDetailsResponseAddressState()
                    .id(UUID.fromString(restaurantEntity.getAddress().getState().getUuid()))
                    .stateName(restaurantEntity.getAddress().getState().getStateName());

            //Creating the RestaurantDetailsResponseAddress for the RestaurantList
            RestaurantDetailsResponseAddress restaurantDetailsResponseAddress = new RestaurantDetailsResponseAddress()
                    .id(UUID.fromString(restaurantEntity.getAddress().getUuid()))
                    .city(restaurantEntity.getAddress().getCity())
                    .flatBuildingName(restaurantEntity.getAddress().getFlatBuildNumber())
                    .locality(restaurantEntity.getAddress().getLocality())
                    .pincode(restaurantEntity.getAddress().getPincode())
                    .state(restaurantDetailsResponseAddressState);

            //Creating RestaurantList to add to list of RestaurantList
            RestaurantList restaurantList = new RestaurantList()
                    .id(UUID.fromString(restaurantEntity.getUuid()))
                    .restaurantName(restaurantEntity.getRestaurantName())
                    .averagePrice(restaurantEntity.getAveragePriceForTwo())
                    .categories(categories)
                    .customerRating(BigDecimal.valueOf(restaurantEntity.getCustomerRating()))
                    .numberCustomersRated(restaurantEntity.getNumber_of_customers_rated())
                    .photoURL(restaurantEntity.getPhotoUrl())
                    .address(restaurantDetailsResponseAddress);

            //Adding it to the list
            restaurantLists.add(restaurantList);

        }

        //Creating the RestaurantListResponse by adding the list of RestaurantList
        RestaurantListResponse restaurantListResponse = new RestaurantListResponse().restaurants(restaurantLists);
        return new ResponseEntity<RestaurantListResponse>(restaurantListResponse,HttpStatus.OK);
    }


    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET, path = "/restaurant/name/{restaurant_name}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getrestaurantByName(@PathVariable("restaurant_name")final String restaurant_name)throws RestaurantNotFoundException
    {
//Calls restaurantsByName method of restaurantService to get the list of restaurant entity.
        List<RestaurantEntity> restaurantEntities = restaurantBusinessService.restaurantsByName(restaurant_name);

        if (!restaurantEntities.isEmpty()) {//Checking if the restaurant entity is empty

            //Creating restaurant list for the response
            List<RestaurantList> restaurantLists = new LinkedList<>();
            for (RestaurantEntity restaurantEntity : restaurantEntities) {  //Looping for each restaurant entity in restaurantEntities

                //Calls  getCategoriesByRestaurant to get categories of the corresponding restaurant.
                List<CategoryEntity> categoryEntities = categoryBusinessService.getCategoriesByRestaurant(restaurantEntity.getUuid());
                String categories = new String();
                ListIterator<CategoryEntity> listIterator = categoryEntities.listIterator();
                //To concat the category names.
                while (listIterator.hasNext()) {
                    categories = categories + listIterator.next().getCategoryName();
                    if (listIterator.hasNext()) {
                        categories = categories + ", ";
                    }
                }

                //Creating the RestaurantDetailsResponseAddressState for the RestaurantDetailsResponseAddress
                RestaurantDetailsResponseAddressState restaurantDetailsResponseAddressState = new RestaurantDetailsResponseAddressState()
                        .id(UUID.fromString(restaurantEntity.getAddress().getState().getUuid()))
                        .stateName(restaurantEntity.getAddress().getState().getStateName());

                //Creating the RestaurantDetailsResponseAddress for the RestaurantList
                RestaurantDetailsResponseAddress restaurantDetailsResponseAddress = new RestaurantDetailsResponseAddress()
                        .id(UUID.fromString(restaurantEntity.getAddress().getUuid()))
                        .city(restaurantEntity.getAddress().getCity())
                        .flatBuildingName(restaurantEntity.getAddress().getFlatBuildNumber())
                        .locality(restaurantEntity.getAddress().getLocality())
                        .pincode(restaurantEntity.getAddress().getPincode())
                        .state(restaurantDetailsResponseAddressState);

                //Creating RestaurantList to add to list of RestaurantList
                RestaurantList restaurantList = new RestaurantList()
                        .id(UUID.fromString(restaurantEntity.getUuid()))
                        .restaurantName(restaurantEntity.getRestaurantName())
                        .averagePrice(restaurantEntity.getAveragePriceForTwo())
                        .categories(categories)
                        .customerRating(BigDecimal.valueOf(restaurantEntity.getCustomerRating()))
                        .numberCustomersRated(restaurantEntity.getNumber_of_customers_rated())
                        .photoURL(restaurantEntity.getPhotoUrl())
                        .address(restaurantDetailsResponseAddress);

                //Adding it to the list
                restaurantLists.add(restaurantList);

            }

            //Creating the RestaurantListResponse by adding the list of RestaurantList
            RestaurantListResponse restaurantListResponse = new RestaurantListResponse().restaurants(restaurantLists);
            return new ResponseEntity<RestaurantListResponse>(restaurantListResponse, HttpStatus.OK);
        }else {
            return new ResponseEntity<RestaurantListResponse>(new RestaurantListResponse(),HttpStatus.OK);
        }

    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET, path = "/restaurant/category/{category_id}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getrestaurantByCategoryId(@PathVariable("category_id")final String category_id) throws CategoryNotFoundException
    {
        //Calls restaurantByCategory method of restaurantService to get the list of restaurant entity.
        List<RestaurantEntity> restaurantEntities = restaurantBusinessService.restaurantByCategory(category_id);

        //Creating restaurant list for the response
        List<RestaurantList> restaurantLists = new LinkedList<>();
        for (RestaurantEntity restaurantEntity : restaurantEntities) { //Looping for each restaurant entity in restaurantEntities

            //Calls  getCategoriesByRestaurant to get categories of the corresponding restaurant.
            List<CategoryEntity> categoryEntities = categoryBusinessService.getCategoriesByRestaurant(restaurantEntity.getUuid());
            String categories = new String();
            ListIterator<CategoryEntity> listIterator = categoryEntities.listIterator();
            //To concat the category names.
            while (listIterator.hasNext()) {
                categories = categories + listIterator.next().getCategoryName();
                if (listIterator.hasNext()) {
                    categories = categories + ", ";
                }
            }

            //Creating the RestaurantDetailsResponseAddressState for the RestaurantDetailsResponseAddress
            RestaurantDetailsResponseAddressState restaurantDetailsResponseAddressState = new RestaurantDetailsResponseAddressState()
                    .id(UUID.fromString(restaurantEntity.getAddress().getState().getUuid()))
                    .stateName(restaurantEntity.getAddress().getState().getStateName());

            //Creating the RestaurantDetailsResponseAddress for the RestaurantList
            RestaurantDetailsResponseAddress restaurantDetailsResponseAddress = new RestaurantDetailsResponseAddress()
                    .id(UUID.fromString(restaurantEntity.getAddress().getUuid()))
                    .city(restaurantEntity.getAddress().getCity())
                    .flatBuildingName(restaurantEntity.getAddress().getFlatBuildNumber())
                    .locality(restaurantEntity.getAddress().getLocality())
                    .pincode(restaurantEntity.getAddress().getPincode())
                    .state(restaurantDetailsResponseAddressState);

            //Creating RestaurantList to add to list of RestaurantList
            RestaurantList restaurantList = new RestaurantList()
                    .id(UUID.fromString(restaurantEntity.getUuid()))
                    .restaurantName(restaurantEntity.getRestaurantName())
                    .averagePrice(restaurantEntity.getAveragePriceForTwo())
                    .categories(categories)
                    .customerRating(BigDecimal.valueOf(restaurantEntity.getCustomerRating()))
                    .numberCustomersRated(restaurantEntity.getNumber_of_customers_rated())
                    .photoURL(restaurantEntity.getPhotoUrl())
                    .address(restaurantDetailsResponseAddress);

            //Adding it to the list
            restaurantLists.add(restaurantList);

        }

        //Creating the RestaurantListResponse by adding the list of RestaurantList
        RestaurantListResponse restaurantListResponse = new RestaurantListResponse().restaurants(restaurantLists);
        return new ResponseEntity<RestaurantListResponse>(restaurantListResponse, HttpStatus.OK);

    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET, path = "/api/restaurant/{restaurant_id}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantDetailsResponse> getrestaurantById(@PathVariable("restaurant_id")final String restaurant_id)throws RestaurantNotFoundException
    {
        //Calls restaurantByUUID method of restaurantService to get the restaurant entity.
        RestaurantEntity restaurantEntity = restaurantBusinessService.restaurantByuuid(restaurant_id);

        //Calls  getCategoriesByRestaurant to get categories of the corresponding restaurant.
        List<CategoryEntity> categoryEntities = categoryBusinessService.getCategoriesByRestaurant(restaurant_id);

        //Creating category Lists  for the response
        List<CategoryList> categoryLists = new LinkedList<>();
        for (CategoryEntity categoryEntity:categoryEntities){  //Looping for each CategoryEntity in categoryEntities

            //Calls getItemsByCategoryAndRestaurant of itemService to get list of itemEntities.
            List<ItemEntity> itemEntities = itemBusinessService.getItemsByCategoryAndRestaurant(restaurant_id ,categoryEntity.getUuid());
            //Creating Item List for the CategoryList.
            List<ItemList> itemLists = new LinkedList<>();
            itemEntities.forEach(itemEntity -> {
                ItemList itemList = new ItemList()
                        .id(UUID.fromString(itemEntity.getUuid()))
                        .itemName(itemEntity.getItemName());

                itemLists.add(itemList);
            });

            //Creating new category list to add listof category list
            CategoryList categoryList = new CategoryList()
                    .itemList(itemLists)
                    .id(UUID.fromString(categoryEntity.getUuid()))
                    .categoryName(categoryEntity.getCategoryName());

            //adding to the categoryLists
            categoryLists.add(categoryList);
        }

        //Creating the RestaurantDetailsResponseAddressState for the RestaurantDetailsResponseAddress
        RestaurantDetailsResponseAddressState restaurantDetailsResponseAddressState = new RestaurantDetailsResponseAddressState()
                .id(UUID.fromString(restaurantEntity.getAddress().getState().getUuid()))
                .stateName(restaurantEntity.getAddress().getState().getStateName());

        //Creating the RestaurantDetailsResponseAddress for the RestaurantList
        RestaurantDetailsResponseAddress restaurantDetailsResponseAddress = new RestaurantDetailsResponseAddress()
                .id(UUID.fromString(restaurantEntity.getAddress().getUuid()))
                .city(restaurantEntity.getAddress().getCity())
                .flatBuildingName(restaurantEntity.getAddress().getFlatBuildNumber())
                .locality(restaurantEntity.getAddress().getLocality())
                .pincode(restaurantEntity.getAddress().getPincode())
                .state(restaurantDetailsResponseAddressState);

        //Creating the RestaurantDetailsResponse by adding the list of categoryList and other details.
        RestaurantDetailsResponse restaurantDetailsResponse = new RestaurantDetailsResponse()
                .restaurantName(restaurantEntity.getRestaurantName())
                .address(restaurantDetailsResponseAddress)
                .averagePrice(restaurantEntity.getAveragePriceForTwo())
                .customerRating(BigDecimal.valueOf(restaurantEntity.getCustomerRating()))
                .numberCustomersRated(restaurantEntity.getNumber_of_customers_rated())
                .id(UUID.fromString(restaurantEntity.getUuid()))
                .photoURL(restaurantEntity.getPhotoUrl())
                .categories(categoryLists);

        return new ResponseEntity<RestaurantDetailsResponse>(restaurantDetailsResponse,HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST, path = "/api/restaurant/{restaurant_id}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantUpdatedResponse> updateRestaurant(@PathVariable("restaurant_id")final String restaurantUuid, @RequestParam(name = "customer_rating") final Double customerRating,@RequestHeader("authorization")final String authorization)throws AuthorizationFailedException, RestaurantNotFoundException, InvalidRatingException
    {
        //Access the accessToken from the request Header
        final String accessToken = authorization.split("Bearer ")[1];

        //Calls customerService getCustomerMethod to check the validity of the customer.this methods returns the customerEntity.
        CustomerEntity customerEntity = customerBusinessService.getCustomer(accessToken);

        //Calls restaurantByUUID method of restaurantService to get the restaurant entity.
        RestaurantEntity restaurantEntity = restaurantBusinessService.restaurantByuuid(restaurantUuid);

        //Calls updateRestaurantRating and passes restaurantentity found and customer rating and return the updated entity.
        RestaurantEntity updatedRestaurantEntity = restaurantBusinessService.updateRestaurantRating(restaurantEntity,customerRating);

        //Creating RestaurantUpdatedResponse containing the UUID of the updated Restaurant and the success message.
        RestaurantUpdatedResponse restaurantUpdatedResponse = new RestaurantUpdatedResponse()
                .id(UUID.fromString(restaurantUuid))
                .status("RESTAURANT RATING UPDATED SUCCESSFULLY");

        return new ResponseEntity<RestaurantUpdatedResponse>(restaurantUpdatedResponse,HttpStatus.OK);
    }

}

package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CategoryDao;
import com.upgrad.FoodOrderingApp.service.dao.RestaurantCategoryDao;
import com.upgrad.FoodOrderingApp.service.dao.RestaurantDao;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantCategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class CategoryBusinessService {
    @Autowired
    RestaurantCategoryDao restaurantCategoryDao; //Handles all data related to the RestaurantCategoryEntity


    @Autowired
    RestaurantDao restaurantDao; //Handles all data related to the RestaurantEntity

    @Autowired
    CategoryDao categoryDao;   //Handles all data related to the CategoryEntity

    /* This method is to get All Categories Ordered By Name and returns list of CategoryEntity
        If error throws exception with error code and error message.
        */
    public List<CategoryEntity> getAllCategoriesOrderedByName()
    {
        //Calls getAllCategoriesOrderedByName of categoryDao to get list of CategoryEntity
        List<CategoryEntity> categoryEntities = categoryDao.getAllCategories();
        return categoryEntities;
    }

    /* This method is to get Category By Id and returns CategoryEntity it takes categoryUuid as input.
        If error throws exception with error code and error message.
        */

    public CategoryEntity getCategoryById(String CategoryId)throws CategoryNotFoundException
    {
        if(CategoryId == null || CategoryId == ""){  //Checking for categoryUuid to be null or empty to throw exception.
            throw new CategoryNotFoundException("CNF-001","Category id field should not be empty");
        }

        //Calls getCategoryByUuid of categoryDao to get CategoryEntity
        CategoryEntity categoryEntity = categoryDao.getCategoryById(CategoryId);

        if(categoryEntity == null){ //Checking for categoryEntity to be null or empty to throw exception.
            throw new CategoryNotFoundException("CNF-002","No category by this id");
        }

        return categoryEntity;
    }

    /* This method is to get Categories By Restaurant and returns list of CategoryEntity. Its takes restaurantUuid as the input.
        If error throws exception with error code and error message.
        */

    public List<CategoryEntity> getCategoriesByRestaurant(String restaurantId)
    {
        //Calls getRestaurantByUuid of restaurantDao to get RestaurantEntity
        RestaurantEntity restaurantEntity = restaurantDao.getRestaurantByUuid(restaurantId);

        //Calls getCategoriesByRestaurant of restaurantCategoryDao to get list of RestaurantCategoryEntity
        List<RestaurantCategoryEntity> restaurantCategoryEntities = restaurantCategoryDao.getCategoriesByRestaurant(restaurantEntity);

        //Creating the list of the Category entity to be returned.
        List<CategoryEntity> categoryEntities = new LinkedList<>();
        restaurantCategoryEntities.forEach(restaurantCategoryEntity -> {
            categoryEntities.add(restaurantCategoryEntity.getCategoryEntity());
        });
        return categoryEntities;
    }
}
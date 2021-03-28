package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.common.Utility;
import com.upgrad.FoodOrderingApp.service.dao.ItemDao;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class ItemBusinessService {

    @Autowired
    ItemDao itemDao;
    @Autowired
    Utility utility;


    public List<ItemEntity> getItemByPopularity(RestaurantEntity restaurantEntity) {
        //Calls getOrdersByRestaurant method of orderDao to get the  OrdersEntity
        List<OrdersEntity> ordersEntities = orderDao.getOrdersByRestaurant(restaurantEntity);

        //Creating list of ItemEntity which are ordered from the restaurant.
        List<ItemEntity> itemEntities = new LinkedList<>();

        //Looping in for each ordersEntity in ordersEntities to get the corresponding orders
        ordersEntities.forEach(ordersEntity -> {
            //Calls getItemsByOrders method of orderItemDao to get the  OrderItemEntity
            List<OrderItemEntity> orderItemEntities = orderItemDao.getItemsByOrders(ordersEntity);
            orderItemEntities.forEach(orderItemEntity -> { //Looping in to get each tem from the OrderItemEntity.
                itemEntities.add(orderItemEntity.getItem());
            });
        });

        //Creating a HashMap to count the frequency of the order.
        Map<String, Integer> itemCountMap = new HashMap<String, Integer>();
        itemEntities.forEach(itemEntity -> { //Looping in to count the frequency of Item ordered correspondingly updating the count.
            Integer count = itemCountMap.get(itemEntity.getUuid());
            itemCountMap.put(itemEntity.getUuid(), (count == null) ? 1 : count + 1);
        });

        //Calls sortMapByValues method of uitilityProvider and get sorted map by value.
        Map<String, Integer> sortedItemCountMap = utility.sortMapByValues(itemCountMap);

        //Creating the top 5 Itementity list
        List<ItemEntity> sortedItemEntites = new LinkedList<>();
        Integer count = 0;
        for (Map.Entry<String, Integer> item : sortedItemCountMap.entrySet()) {
            if (count < 5) {
                //Calls getItemByUUID to get the Itemtentity
                sortedItemEntites.add(itemDao.getItemById(item.getKey()));
                count = count + 1;
            } else {
                break;
            }
        }

        return sortedItemEntites;
    }
}
package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.*;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class OrderBusinessService {

    @Autowired
    OrderDao orderDao;

    @Autowired
    CouponDao couponDao;

    @Autowired
    OrderItemDao orderItemDao;

    @Autowired
    CustomerBusinessService customerBusinessService;

    @Autowired
    CustomerEntity customerEntity;

    @Autowired
    CustomerAuthDao customerAuthDao;

    @Autowired
    AddressDao addressDao;

    @Autowired
    CustomerAddressDao customerAddressDao;

    @Autowired
    ItemDao itemDao;

    //1
    public CouponEntity getCouponDetailsByName(final String couponName, final String accessToken) throws CouponNotFoundException, AuthorizationFailedException
    {

        CustomerAuthEntity customerAuthEntity = customerAuthDao.getCustomerByAuthToken(accessToken);

        if (customerAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        }

        if (customerAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint.");
        }

        final ZonedDateTime now = ZonedDateTime.now();

        if (customerAuthEntity.getExpiresAt().compareTo(now) <= 0) {
            throw new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint.");
        }

        if(couponName == null||couponName == ""){
            throw new CouponNotFoundException("CPF-002","Coupon name field should not be empty");
        }

        CouponEntity couponEntity = couponDao.getCouponDetailsByName(couponName);
        if(couponEntity == null){
            throw new CouponNotFoundException("CPF-001","No coupon by this name");
        }

        return couponEntity;
    }

    public List<OrderEntity> getOrdersByCustomer(final String accessToken) throws AuthorizationFailedException
    {

        CustomerAuthEntity customerAuthEntity = customerAuthDao.getCustomerByAuthToken(accessToken);

        if (customerAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        }

        if (customerAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint.");
        }

        final ZonedDateTime now = ZonedDateTime.now();

        if (customerAuthEntity.getExpiresAt().compareTo(now) <= 0) {
            throw new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint.");
        }

        CustomerEntity customerEntity = customerBusinessService.getCustomer(accessToken);
        List<OrderEntity> ordersEntities = orderDao.getOrdersByCustomer(customerEntity);
        return ordersEntities;
    }

    public List<OrderItemEntity> fetchItemDetails(OrderEntity orderEntity)
    {
        List<OrderItemEntity> orderItemEntities = orderItemDao.fetchItemDetails(orderEntity);
        return orderItemEntities;
    }

    public CouponEntity getCouponByCouponId(String couponId) throws CouponNotFoundException {

        CouponEntity couponEntity = couponDao.getCouponByCouponId(couponId);
        if(couponEntity == null){
            throw new CouponNotFoundException("CPF-002","No coupon by this id");
        }
        return couponEntity;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public OrderEntity saveOrder(CustomerEntity customerEntity, CouponEntity couponEntity, PaymentEntity paymentEntity, AddressEntity addressEntity,
                                 RestaurantEntity restaurantEntity, OrderEntity orderEntity, String accessToken)
            throws AuthorizationFailedException, CouponNotFoundException, AddressNotFoundException,
            PaymentMethodNotFoundException, RestaurantNotFoundException, ItemNotFoundException
    {

        CustomerAuthEntity customerAuthEntity = customerAuthDao.getCustomerByAuthToken(accessToken);

        if (customerAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        }

        if (customerAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint.");
        }

        final ZonedDateTime now = ZonedDateTime.now();

        if (customerAuthEntity.getExpiresAt().compareTo(now) <= 0) {
            throw new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint.");
        }

        String couponName = couponEntity.getCouponName();

        if(couponName == null||couponName == ""){
            throw new CouponNotFoundException("CPF-002","Coupon name field should not be empty");
        }

        if (addressEntity == null){
            throw new AddressNotFoundException("ANF-003","No address by this id");
        }

        //CHECK THIS
        CustomerAddressEntity customerAddressEntity = customerAddressDao.getCustomerAddressEntityByAddress(addressEntity);

        if(!customerAddressEntity.getCustomer().getUuid().equals(customerEntity.getUuid())) {
            throw new AuthorizationFailedException("ATHR-004", "You are not authorized to view/update/delete any one else's address");
        }

        if(paymentEntity.getUuid() == null) {
            throw new PaymentMethodNotFoundException("PNF-002", "No payment method found by this id");
        }

        if(restaurantEntity.getUuid() == null) {
            throw new RestaurantNotFoundException("RNF-001", "No restaurant by this id");
        }

        //CHECK THIS
        if(orderEntity.getOrderItem() == null) {
            throw new ItemNotFoundException("INF-003","No item by this id exist");
        }


        OrderEntity savedOrderEntity = orderDao.saveOrder(orderEntity);
        return savedOrderEntity;

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public OrderItemEntity saveOrderItem (OrderItemEntity orderItemEntity){

        OrderItemEntity savedOrderItemEntity = orderItemDao.saveOrderItem(orderItemEntity);
        return savedOrderItemEntity;
    }

}



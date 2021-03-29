package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.*;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@CrossOrigin
@Controller
public class OrderController {

    @Autowired
    CustomerBusinessService customerBusinessService;

    @Autowired
    OrderBusinessService orderBusinessService;

    @Autowired
    PaymentBusinessService paymentBusinessService;

    @Autowired
    AddressBusinessService addressBusinessService;

    @Autowired
    RestaurantBusinessService restaurantBusinessService;

    @Autowired
    ItemBusinessService itemBusinessService;

    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET, value = "/order/coupon/{coupon_name}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CouponDetailsResponse> getCouponByName(@PathVariable("coupon_name")final String coupon_name, @RequestHeader("authorization")final String authorization)
            throws AuthorizationFailedException, CouponNotFoundException
    {
        String accessToken = authorization.split("Bearer ")[1];

        CouponEntity couponEntity = orderBusinessService.getCouponDetailsByName(coupon_name, accessToken);

        CouponDetailsResponse couponDetailsResponse = new CouponDetailsResponse()
                .couponName(couponEntity.getCouponName())
                .id(UUID.fromString(couponEntity.getUuid()))
                .percent(couponEntity.getPercent());
        return new ResponseEntity<CouponDetailsResponse>(couponDetailsResponse, HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET,path = "",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CustomerOrderResponse> pastOrders(@RequestHeader(value = "authorization")final String authorization )
            throws AuthorizationFailedException
    {
        String accessToken = authorization.split("Bearer ")[1];

        List<OrderEntity> orderEntities =  orderBusinessService.getOrdersByCustomer(accessToken);

        List<OrderList> orderLists = new LinkedList<>();

        if(orderEntities != null){
            for(OrderEntity orderEntity:orderEntities){
                List<OrderItemEntity> orderItemEntities = orderBusinessService.fetchItemDetails(orderEntity);

                List<ItemQuantityResponse> itemQuantityResponseList = new LinkedList<>();
                orderItemEntities.forEach(orderItemEntity -> {
                    ItemQuantityResponseItem itemQuantityResponseItem = new ItemQuantityResponseItem()
                            .itemName(orderItemEntity.getItemEntity().getItemName())
                            .itemPrice(orderItemEntity.getItemEntity().getPrice())
                            .id(UUID.fromString(orderItemEntity.getItemEntity().getUuid()))
                            .type(ItemQuantityResponseItem.TypeEnum.valueOf(orderItemEntity.getItemEntity().getType().getValue()));

                    ItemQuantityResponse itemQuantityResponse = new ItemQuantityResponse()
                            .item(itemQuantityResponseItem)
                            .quantity(orderItemEntity.getQuantity())
                            .price(orderItemEntity.getPrice());
                    itemQuantityResponseList.add(itemQuantityResponse);
                });
                OrderListAddressState orderListAddressState = new OrderListAddressState()
                        .id(UUID.fromString(orderEntity.getAddress().getState().getUuid()))
                        .stateName(orderEntity.getAddress().getState().getStateName());

                OrderListAddress orderListAddress = new OrderListAddress()
                        .id(UUID.fromString(orderEntity.getAddress().getUuid()))
                        .flatBuildingName(orderEntity.getAddress().getFlatBuildNumber())
                        .locality(orderEntity.getAddress().getLocality())
                        .city(orderEntity.getAddress().getCity())
                        .pincode(orderEntity.getAddress().getPincode())
                        .state(orderListAddressState);

                OrderListCoupon orderListCoupon = new OrderListCoupon()
                        .couponName(orderEntity.getCoupon().getCouponName())
                        .id(UUID.fromString(orderEntity.getCoupon().getUuid()))
                        .percent(orderEntity.getCoupon().getPercent());

                OrderListCustomer orderListCustomer = new OrderListCustomer()
                        .id(UUID.fromString(orderEntity.getCustomer().getUuid()))
                        .firstName(orderEntity.getCustomer().getFirstname())
                        .lastName(orderEntity.getCustomer().getLastname())
                        .emailAddress(orderEntity.getCustomer().getEmail())
                        .contactNumber(orderEntity.getCustomer().getContact_number());

                OrderListPayment orderListPayment = new OrderListPayment()
                        .id(UUID.fromString(orderEntity.getPayment().getUuid()))
                        .paymentName(orderEntity.getPayment().getPaymentName());

                OrderList orderList = new OrderList()
                        .id(UUID.fromString(orderEntity.getUuid()))
                        .itemQuantities(itemQuantityResponseList)
                        .address(orderListAddress)
                        .bill(BigDecimal.valueOf(orderEntity.getBill()))
                        .date(String.valueOf(orderEntity.getDate()))
                        .discount(BigDecimal.valueOf(orderEntity.getDiscount()))
                        .coupon(orderListCoupon)
                        .customer(orderListCustomer)
                        .payment(orderListPayment);
                orderLists.add(orderList);
            }

            CustomerOrderResponse customerOrderResponse = new CustomerOrderResponse()
                    .orders(orderLists);
            return new ResponseEntity<CustomerOrderResponse>(customerOrderResponse,HttpStatus.OK);
        }else {
            return new ResponseEntity<CustomerOrderResponse>(new CustomerOrderResponse(),HttpStatus.OK);
        }
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST,path = "",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SaveOrderResponse> saveOrder(@RequestHeader(value = "authorization")final String authorization, @RequestBody(required = false) final SaveOrderRequest saveOrderRequest) throws AuthorizationFailedException, PaymentMethodNotFoundException, AddressNotFoundException, RestaurantNotFoundException, CouponNotFoundException ,ItemNotFoundException{

        String accessToken = authorization.split("Bearer ")[1];

        CustomerEntity customerEntity = customerBusinessService.getCustomer(accessToken);

        CouponEntity couponEntity = orderBusinessService.getCouponByCouponId(saveOrderRequest.getCouponId().toString());

        PaymentEntity paymentEntity = paymentBusinessService.getPaymentByPaymentId(saveOrderRequest.getPaymentId().toString());

        AddressEntity addressEntity = addressBusinessService.getAddressByAddressId(saveOrderRequest.getAddressId(),customerEntity);

        RestaurantEntity restaurantEntity = restaurantBusinessService.restaurantByuuid(saveOrderRequest.getRestaurantId().toString());

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setUuid(UUID.randomUUID().toString());
        orderEntity.setBill(saveOrderRequest.getBill().floatValue());
        orderEntity.setDate(timestamp);
        orderEntity.setCustomer(customerEntity);
        orderEntity.setDiscount(saveOrderRequest.getDiscount().doubleValue());
        orderEntity.setPayment(paymentEntity);
        orderEntity.setAddress(addressEntity);
        orderEntity.setRestauarnt(restaurantEntity);
        orderEntity.setCoupon(couponEntity);

        OrderEntity savedOrderEntity = orderBusinessService.saveOrder(customerEntity, couponEntity, paymentEntity, addressEntity, restaurantEntity, orderEntity, accessToken);

        List<ItemQuantity> itemQuantities = saveOrderRequest.getItemQuantities();
        for(ItemQuantity itemQuantity : itemQuantities) {

            OrderItemEntity orderItemEntity = new OrderItemEntity();

            ItemEntity itemEntity = itemBusinessService.getItemById(itemQuantity.getItemId().toString());

            orderItemEntity.setItemEntity(itemEntity);
            orderItemEntity.setOrderEntity(orderEntity);
            orderItemEntity.setPrice(itemQuantity.getPrice());
            orderItemEntity.setQuantity(itemQuantity.getQuantity());

            OrderItemEntity savedOrderItem = orderBusinessService.saveOrderItem(orderItemEntity);
        }

        SaveOrderResponse saveOrderResponse = new SaveOrderResponse()
                .id(savedOrderEntity.getUuid())
                .status("ORDER SUCCESSFULLY PLACED");
        return new ResponseEntity<SaveOrderResponse>(saveOrderResponse,HttpStatus.CREATED);
    }
}

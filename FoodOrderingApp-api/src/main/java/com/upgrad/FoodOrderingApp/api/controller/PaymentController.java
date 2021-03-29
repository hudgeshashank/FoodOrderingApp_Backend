package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.PaymentListResponse;
import com.upgrad.FoodOrderingApp.api.model.PaymentResponse;
import com.upgrad.FoodOrderingApp.service.businness.PaymentBusinessService;
import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Controller
@CrossOrigin
public class PaymentController {

    @Autowired
    PaymentBusinessService paymentBusinessService;

    @RequestMapping(method = RequestMethod.GET, path = "/payment", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<PaymentListResponse> retrievePaymentMethods()
    {
        List<PaymentEntity> paymentEntities = paymentBusinessService.fetchAllPayments();

        List<PaymentResponse> paymentResponses = new LinkedList<>();
        paymentEntities.forEach(paymentEntity -> {
            PaymentResponse paymentResponse = new PaymentResponse()
                    .paymentName(paymentEntity.getPaymentName())
                    .id(UUID.fromString(paymentEntity.getUuid()));
            paymentResponses.add(paymentResponse);
        });

        PaymentListResponse paymentListResponse = new PaymentListResponse()
                .paymentMethods(paymentResponses);
        return new ResponseEntity<PaymentListResponse>(paymentListResponse, HttpStatus.OK);
    }

}

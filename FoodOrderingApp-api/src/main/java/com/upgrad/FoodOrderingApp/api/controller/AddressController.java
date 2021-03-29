package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.AddressBusinessService;
import com.upgrad.FoodOrderingApp.service.businness.CustomerBusinessService;
import com.upgrad.FoodOrderingApp.service.dao.StateDao;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;


@CrossOrigin
@Controller
public class AddressController {
    @Autowired
    AddressBusinessService addressBusinessService;

    @Autowired
    CustomerBusinessService customerBusinessService;

    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST, path = "/address", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SaveAddressResponse> saveAddress(@RequestHeader("authorization")final String authorization, @RequestBody(required = false) final SaveAddressRequest saveAddressRequest)
            throws AuthorizationFailedException, AddressNotFoundException, SaveAddressException
    {
        String accessToken = authorization.split("Bearer ")[1];

        CustomerEntity customerEntity = customerBusinessService.getCustomer(accessToken);

        AddressEntity addressEntity = new AddressEntity();

        addressEntity.setFlatBuildNumber(saveAddressRequest.getFlatBuildingName());
        addressEntity.setCity(saveAddressRequest.getCity());
        addressEntity.setLocality(saveAddressRequest.getLocality());
        addressEntity.setPincode(saveAddressRequest.getPincode());
        addressEntity.setUuid(UUID.randomUUID().toString());


        StateEntity stateEntity = addressBusinessService.getStateByAddressId(saveAddressRequest.getStateUuid());

        AddressEntity savedAddress = addressBusinessService.saveAddressEntity(accessToken,addressEntity,stateEntity);

        addressBusinessService.saveCustomerAddressEntity(customerEntity,savedAddress);

        SaveAddressResponse saveAddressResponse = new SaveAddressResponse()
                .id(savedAddress.getUuid())
                .status("ADDRESS SUCCESSFULLY REGISTERED");

        return new ResponseEntity<SaveAddressResponse>(saveAddressResponse, HttpStatus.CREATED);
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET, path = "/address/customer", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AddressListResponse> getAllSavedAddress(@RequestHeader("authorization")final String authorization)
            throws AuthorizationFailedException
    {
        String accessToken = authorization.split("Bearer ")[1];

        List<AddressEntity> addressEntities = addressBusinessService.getSavedAddresses(accessToken);

        List<AddressList> addressLists = new LinkedList<>();

        addressEntities.forEach(addressEntity -> {
            AddressListState addressListState = new AddressListState()
                    .stateName(addressEntity.getState().getStateName())
                    .id(UUID.fromString(addressEntity.getState().getUuid()));
            AddressList addressList = new AddressList()
                    .id(UUID.fromString(addressEntity.getUuid()))
                    .city(addressEntity.getCity())
                    .flatBuildingName(addressEntity.getFlatBuildNumber())
                    .locality(addressEntity.getLocality())
                    .pincode(addressEntity.getPincode())
                    .state(addressListState);
            addressLists.add(addressList);
        });

        AddressListResponse addressListResponse = new AddressListResponse().addresses(addressLists);
        return new ResponseEntity<AddressListResponse>(addressListResponse,HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.DELETE, path = "/address/{address_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<DeleteAddressResponse> deleteSavedAddress(@RequestHeader("authorization")final String authorization, @PathVariable("address_id")final String addressId)
            throws AuthorizationFailedException
    {
        String accessToken = authorization.split("Bearer ")[1];

        AddressEntity deletedAddressEntity = addressBusinessService.deleteAddress(accessToken, addressId);

        DeleteAddressResponse deleteAddressResponse = new DeleteAddressResponse()
                .id(UUID.fromString(deletedAddressEntity.getUuid()))
                .status("ADDRESS DELETED SUCCESSFULLY");

        return new ResponseEntity<DeleteAddressResponse>(deleteAddressResponse,HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET,path = "/states",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)

    public ResponseEntity<StatesListResponse> getAllStates(){

        List<StateEntity> stateEntities = addressBusinessService.getStates();

        if(!stateEntities.isEmpty()) {
            List<StatesList> statesLists = new LinkedList<>();
            stateEntities.forEach(stateEntity -> {
                StatesList statesList = new StatesList()
                        .id(UUID.fromString(stateEntity.getUuid()))
                        .stateName(stateEntity.getStateName());
                statesLists.add(statesList);
            });

            StatesListResponse statesListResponse = new StatesListResponse().states(statesLists);
            return new ResponseEntity<StatesListResponse>(statesListResponse, HttpStatus.OK);
        }else
            return new ResponseEntity<StatesListResponse>(new StatesListResponse(),HttpStatus.OK);
    }
}

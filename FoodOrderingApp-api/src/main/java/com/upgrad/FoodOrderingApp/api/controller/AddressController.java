package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.AddressService;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
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
    AddressService addressService;

    @Autowired
    CustomerService customerService;

    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST, path = "/address", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SaveAddressResponse> saveAddress(@RequestHeader("authorization")final String authorization, @RequestBody(required = false) final SaveAddressRequest saveAddressRequest)
            throws AuthorizationFailedException, AddressNotFoundException, SaveAddressException
    {
        String accessToken = authorization.split("Bearer ")[1];

        CustomerEntity customerEntity = customerService.getCustomer(accessToken);

        final AddressEntity addressEntity = new AddressEntity(UUID.randomUUID().toString(),
                saveAddressRequest.getFlatBuildingName(),
                saveAddressRequest.getLocality(),
                saveAddressRequest.getCity(),
                saveAddressRequest.getPincode(),
                addressService.getStateByUUID(saveAddressRequest.getStateUuid()));
        addressEntity.setActive(1);

        final AddressEntity savedAddress = addressService.saveAddress(customerEntity, addressEntity);

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

        final CustomerEntity customer = customerService.getCustomer(accessToken);
        final List<AddressEntity> addresses = addressService.getAllAddress(customer);

        AddressListResponse addressListResponse = new AddressListResponse();
        for (AddressEntity a : addresses) {
            AddressListState state = new AddressListState().
                    id(UUID.fromString((a.getState().getUuid()))).
                    stateName(a.getState().getStateName());

            AddressList address = new AddressList().
                    id(UUID.fromString(a.getUuid())).
                    flatBuildingName(a.getFlatBuilNumber()).
                    locality(a.getLocality()).
                    city(a.getCity()).
                    pincode(a.getPincode()).
                    state(state);

            addressListResponse.addAddressesItem(address);
        }
        return new ResponseEntity<>(addressListResponse, HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.DELETE, path = "/address/{address_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<DeleteAddressResponse> deleteSavedAddress(@RequestHeader("authorization")final String authorization, @PathVariable("address_id")final String addressId)
            throws AuthorizationFailedException, AddressNotFoundException {
        String accessToken = authorization.split("Bearer ")[1];

        CustomerEntity customerEntity = customerService.getCustomer(accessToken);

        final AddressEntity addressToBeDeleted = addressService.getAddressByUUID(addressId, customerEntity);
        final AddressEntity deletedAddress = addressService.deleteAddress(addressToBeDeleted);

        DeleteAddressResponse deleteAddressResponse = new DeleteAddressResponse()
                .id(UUID.fromString(deletedAddress.getUuid()))
                .status("ADDRESS DELETED SUCCESSFULLY");

        return new ResponseEntity<DeleteAddressResponse>(deleteAddressResponse,HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET,path = "/states",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)

    public ResponseEntity<StatesListResponse> getAllStates(){

        List<StateEntity> stateEntities = addressService.getAllStates();

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
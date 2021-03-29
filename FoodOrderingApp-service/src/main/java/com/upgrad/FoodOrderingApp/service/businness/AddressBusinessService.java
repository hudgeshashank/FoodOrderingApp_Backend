package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.common.Utility;
import com.upgrad.FoodOrderingApp.service.dao.*;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Service
public class AddressBusinessService {

    @Autowired
    AddressDao addressDao;

    @Autowired
    CustomerAuthDao customerAuthDao;

    @Autowired
    CustomerDao customerDao;

    @Autowired
    Utility utility;

    @Autowired
    StateDao stateDao;

    @Autowired
    CustomerAddressDao customerAddressDao;

    @Autowired
    CustomerBusinessService customerBusinessService;

//    @Autowired
//    OrderDao orderDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public AddressEntity saveAddressEntity(final String authorization, AddressEntity addressEntity, StateEntity stateEntity) throws SaveAddressException, AuthorizationFailedException, AddressNotFoundException
    {
        CustomerAuthEntity customerAuthEntity = customerAuthDao.getCustomerByAuthToken(authorization);

        if(customerAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        }

        if(customerAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint.");
        }

        final ZonedDateTime now = ZonedDateTime.now();

        if(customerAuthEntity.getExpiresAt().compareTo(now) < 0) {
            throw new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint.");
        }

        if(addressEntity.getCity() == null || addressEntity.getFlatBuildNumber() == null || addressEntity.getPincode() == null || addressEntity.getLocality() == null) {
            throw new SaveAddressException("SAR-001","No field can be empty");
        }

        if(!utility.isPincodeValid(addressEntity.getPincode())) {
            throw new SaveAddressException("SAR-002","Invalid pincode");
        }

        if(stateEntity == null) {
            throw new AddressNotFoundException("ANF-002", "No state by this id");
        }

        addressEntity.setState(stateEntity);

        AddressEntity newAddress = addressDao.saveAddress(addressEntity);

        return newAddress;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAddressEntity saveCustomerAddressEntity(CustomerEntity customerEntity,AddressEntity addressEntity){

        CustomerAddressEntity customerAddressEntity = new CustomerAddressEntity();
        customerAddressEntity.setCustomer(customerEntity);
        customerAddressEntity.setAddress(addressEntity);

        CustomerAddressEntity createdCustomerAddressEntity = customerAddressDao.saveCustomerAddress(customerAddressEntity);
        return createdCustomerAddressEntity;

    }

    public List<AddressEntity> getSavedAddresses(final String accessToken) throws AuthorizationFailedException
    {
        CustomerAuthEntity customerAuthEntity = customerAuthDao.getCustomerByAuthToken(accessToken);

        if(customerAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        }

        if(customerAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint.");
        }

        final ZonedDateTime now = ZonedDateTime.now();

        if(customerAuthEntity.getExpiresAt().compareTo(now) < 0) {
            throw new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint.");
        }

        CustomerEntity customerEntity = customerBusinessService.getCustomer(accessToken);

        List<CustomerAddressEntity> customerAddressesEntities = customerAddressDao.getAllCustomerAddressByCustomer(customerEntity);

        List<AddressEntity> addressEntitiestoFetch = new LinkedList<>();

        if(customerAddressesEntities != null) {
            customerAddressesEntities.forEach(customerAddressEntity -> {
                addressEntitiestoFetch.add(customerAddressEntity.getAddress());
            });
        }
        Collections.reverse(addressEntitiestoFetch);

        return addressEntitiestoFetch;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AddressEntity deleteAddress(final String accessToken, final String addressId) throws AuthorizationFailedException
    {
        // need order entity for the same to set Active status 0


        CustomerAuthEntity customerAuthEntity = customerAuthDao.getCustomerByAuthToken(accessToken);

        if(customerAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        }

        if(customerAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint.");
        }

        final ZonedDateTime now = ZonedDateTime.now();

        if(customerAuthEntity.getExpiresAt().compareTo(now) < 0) {
            throw new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint.");
        }

        CustomerEntity customerEntity = customerBusinessService.getCustomer(accessToken);

        AddressEntity addressEntity = addressDao.getAddressByAddressId(addressId);

        CustomerAddressEntity customerAddressEntity = customerAddressDao.getCustomerAddressEntityByAddress(addressEntity);

        //check this
        if(!customerAddressEntity.getCustomer().getUuid().equals(customerEntity.getUuid())) {
            throw new AuthorizationFailedException("ATHR-004", "You are not authorized to view/update/delete any one else's address");
        }

        AddressEntity addressEntityDeleted = addressDao.deleteAddress(addressEntity);

        return addressEntityDeleted;
    }

    public List<StateEntity> getStates()
    {
        List<StateEntity> stateEntities = stateDao.getStates();
        return stateEntities;
    }

    public StateEntity getStateByAddressId (String addressId) throws AddressNotFoundException{
        StateEntity stateEntity = stateDao.getStateByAddressId(addressId);
        if(stateEntity == null) {
            throw new AddressNotFoundException("ANF-002", "No state by this id");
        }
        return  stateEntity;
    }

    public AddressEntity getAddressByAddressId(final String addressId, final CustomerEntity customerEntity)throws AuthorizationFailedException,AddressNotFoundException{
        if(addressId == null){
            throw new AddressNotFoundException("ANF-005","Address id can not be empty");
        }

        AddressEntity addressEntity = addressDao.getAddressByAddressId(addressId);
        if (addressEntity == null){
            throw new AddressNotFoundException("ANF-003","No address by this id");
        }

        CustomerAddressEntity customerAddressEntity = customerAddressDao.getCustomerAddressByAddress(addressEntity);

        if(customerAddressEntity.getCustomer().getUuid() == customerEntity.getUuid()){
            return addressEntity;
        }else{
            throw new AuthorizationFailedException("ATHR-004","You are not authorized to view/update/delete any one else's address");
        }

    }
}

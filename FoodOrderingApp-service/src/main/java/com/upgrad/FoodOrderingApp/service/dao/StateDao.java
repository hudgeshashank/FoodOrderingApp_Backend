package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class StateDao {

    @PersistenceContext
    private EntityManager entityManager;


    public List<StateEntity> getStates()
    {
        try {
            List<StateEntity> stateEntities = entityManager.createNamedQuery("getAllStates",StateEntity.class).getResultList();
            return stateEntities;
        }catch (NoResultException nre){
            return null;
        }
    }

    public StateEntity getStateByAddressId(String addressId){
        try{
            StateEntity stateEntity = entityManager.createNamedQuery("getStateByUuid",StateEntity.class).setParameter("uuid",addressId).getSingleResult();
            return stateEntity;
        }catch (NoResultException nre){
            return null;
        }
    }

    public StateEntity getStateById(String uuid)
    {
        return null;
    }
}

package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class CouponDao {

    @PersistenceContext
    private EntityManager entityManager;

    public CouponEntity getCouponDetailsByName(String couponName){
        try{
            CouponEntity couponEntity = entityManager.createNamedQuery("getCouponDetailsByName",CouponEntity.class).setParameter("couponName",couponName).getSingleResult();
            return couponEntity;
        }catch (NoResultException nre){
            return null;
        }
    }

    public CouponEntity getCouponByCouponId(String couponId) {
        try {
            CouponEntity couponEntity = entityManager.createNamedQuery("getCouponByCouponId",CouponEntity.class).setParameter("uuid",couponId).getSingleResult();
            return couponEntity;
        }catch (NoResultException nre){
            return null;
        }
    }

}

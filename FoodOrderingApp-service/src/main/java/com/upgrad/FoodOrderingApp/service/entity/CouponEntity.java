package com.upgrad.FoodOrderingApp.service.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "coupon")
@NamedQueries({
        @NamedQuery(name = "getCouponDetailsByName", query = "SELECT c from CouponEntity c where c.couponName = :couponName"),
        @NamedQuery(name = "getCouponByCouponId",query = "SELECT c FROM  CouponEntity c WHERE c.uuid = :uuid"),
})
public class CouponEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "uuid")
    @NotNull
    private String uuid;
    
    @Column(name = "coupon_name")
    private String couponName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCouponName() {
        return couponName;
    }

    public void setCouponName(String couponName) {
        this.couponName = couponName;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    @Column(name = "percent")
    private int percent;
}

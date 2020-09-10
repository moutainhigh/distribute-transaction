package com.wuqiong.tx.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description 订单
 * @Author Cain
 * @date 2020/9/10
 */
public class Trade implements Serializable {

    private static final long serialVersionUID = -5332536663201620948L;

    private long id;
    private Date createTime;
    private String companyID;
    private String buyerID;
    private Long userID;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCompanyID() {
        return companyID;
    }

    public void setCompanyID(String companyID) {
        this.companyID = companyID;
    }

    public String getBuyerID() {
        return buyerID;
    }

    public void setBuyerID(String buyerID) {
        this.buyerID = buyerID;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }
}

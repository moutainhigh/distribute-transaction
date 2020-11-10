package com.wuqiong.tx.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description
 * @Author Cain
 * @date 2020/8/27
 */
public class User implements Serializable {

    private static final long serialVersionUID = -8948335350346118120L;

    private long id;
    private String companyID;
    private String username;
    private Date createTime;
    private int from;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCompanyID() {
        return companyID;
    }

    public void setCompanyID(String companyID) {
        this.companyID = companyID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }
}

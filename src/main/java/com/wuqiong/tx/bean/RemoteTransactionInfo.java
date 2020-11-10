package com.wuqiong.tx.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @Description 远程事务信息
 * @Author Cain
 * @date 2020/11/6
 */
public class RemoteTransactionInfo implements Serializable {

    private static final long serialVersionUID = 1406702342661731676L;

    private String transactionID;
    /** @see com.wuqiong.tx.enums.RemoteTransactionStatus */
    private int status;
    private boolean isMain;
    private List<RemoteTransactionInfo> subTransactionInfoList;

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isMain() {
        return isMain;
    }

    public void setMain(boolean main) {
        isMain = main;
    }

    public List<RemoteTransactionInfo> getSubTransactionInfoList() {
        return subTransactionInfoList;
    }

    public void setSubTransactionInfoList(List<RemoteTransactionInfo> subTransactionInfoList) {
        this.subTransactionInfoList = subTransactionInfoList;
    }
}

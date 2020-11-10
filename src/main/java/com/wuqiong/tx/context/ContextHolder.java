package com.wuqiong.tx.context;

/**
 * @Description
 * @Author Cain
 * @date 2020/8/27
 */
public class ContextHolder {
    public static ThreadLocal<String> companyIDLocal = new ThreadLocal<String>();
    public static ThreadLocal<Integer> applicationTypeLocal = new ThreadLocal<>();
    public static ThreadLocal<String> mainTransactionIDLocal = new ThreadLocal<>();
    public static ThreadLocal<String> localTransactionIDLocal = new ThreadLocal<>();

    private ContextHolder() {}

    public static void setCompanyID(String companyID) {
        companyIDLocal.set(companyID);
    }

    public static void setApplicationType(int type) {
        applicationTypeLocal.set(type);
    }

    public static String getCompanyID() {
        return companyIDLocal.get();
    }

    public static int getApplicationType() {
        return applicationTypeLocal.get();
    }

    public static String getMainTransactionID() {
        return mainTransactionIDLocal.get();
    }

    public static void setMainTransactionID(String mainTransactionID) {
        mainTransactionIDLocal.set(mainTransactionID);
    }

    public static String getLocalTransactionID() {
        return localTransactionIDLocal.get();
    }

    public static void setLocalTransactionID(String localTransactionID) {
        localTransactionIDLocal.set(localTransactionID);
    }
}

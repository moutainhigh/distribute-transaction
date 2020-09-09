package com.wuqiong.tx.context;

/**
 * @Description
 * @Author Cain
 * @date 2020/8/27
 */
public class ContextHolder {
    public static ThreadLocal<String> companyIDLocal = new ThreadLocal<String>();
    public static ThreadLocal<Integer> applicationTypeLocal = new ThreadLocal<>();

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
}

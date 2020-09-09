package com.wuqiong.tx.context;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @Description
 * @Author Cain
 * @date 2020/8/27
 */
public class ApplicationContextHelper implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextHelper.applicationContext = applicationContext;
    }

    public static <T> T getBeanByNameAndType(String name, Class c) {
        if (applicationContext == null) {
            throw  new RuntimeException("上下文未初始化");
        }
        return (T)applicationContext.getBean(name, c);
    }
}

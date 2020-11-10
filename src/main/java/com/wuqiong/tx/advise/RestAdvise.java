package com.wuqiong.tx.advise;

import com.wuqiong.tx.context.ContextHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


/**
 * @Description
 * @Author Cain
 * @date 2020/9/24
 */
@Aspect
@Component
public class RestAdvise {

    @Pointcut("execution(* com.wuqiong.tx.rest..*(..))")
    public void initContext(){

    }

    @Around(value="initContext()")
    public Object aroundTruncate(ProceedingJoinPoint joinpoint) throws Throwable{
        String companyID = (String) joinpoint.getArgs()[0];
        Object secondParam = joinpoint.getArgs()[1];
        ContextHolder.setCompanyID(companyID);
        ContextHolder.setApplicationType(2);
        if (secondParam != null && secondParam instanceof String) {
            String transactionID = (String) secondParam;
            if (StringUtils.hasText(transactionID)) {
                ContextHolder.setMainTransactionID(transactionID);
            }
        }
        return joinpoint.proceed();
    }

    @Before(value = "initContext()")
    public void startMethod() {
    }

    @AfterReturning(value = "initContext()")
    public void executeMethod() {
    }

    @AfterThrowing(value = "initContext()")
    public void returnMethod() {
    }
}

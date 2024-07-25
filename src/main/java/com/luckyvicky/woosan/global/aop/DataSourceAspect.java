package com.luckyvicky.woosan.global.aop;

import com.luckyvicky.woosan.global.config.distinct.DataSourceContextHolder;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class DataSourceAspect {

    @Before("execution(* com.luckyvicky.woosan..*.service..*.*(..)) && @annotation(com.luckyvicky.woosan.global.annotation.SlaveDBRequest)")
    public void setReadDataSourceType() {
        DataSourceContextHolder.setDataSourceType("slaveDataSource");
        System.out.println("====================================================================================================");
        System.out.println("SLAVE DATA SOURCE");
        System.out.println("====================================================================================================");
    }

    @Before("execution(* com.luckyvicky.woosan..*.service..*.*(..)) && !@annotation(com.luckyvicky.woosan.global.annotation.SlaveDBRequest)")
    public void setWriteDataSourceType() {
        DataSourceContextHolder.setDataSourceType("masterDataSource");
        System.out.println("====================================================================================================");
        System.out.println("MASTER DATA SOURCE");
        System.out.println("====================================================================================================");
    }
}

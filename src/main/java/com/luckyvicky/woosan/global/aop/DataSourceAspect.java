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
    }

    @Before("execution(* com.luckyvicky.woosan..*.service..*.*(..)) && !@annotation(com.luckyvicky.woosan.global.annotation.SlaveDBRequest)")
    public void setWriteDataSourceType() {
        DataSourceContextHolder.setDataSourceType("masterDataSource");
    }
}

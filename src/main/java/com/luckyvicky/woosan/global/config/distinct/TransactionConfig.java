//package com.luckyvicky.woosan.global.config.distinct;
//
//import org.springframework.aop.Advisor;
//import org.springframework.aop.support.DefaultPointcutAdvisor;
//import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.transaction.TransactionDefinition;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
//import org.springframework.transaction.interceptor.TransactionInterceptor;
//import org.springframework.transaction.PlatformTransactionManager;
//import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
//import org.springframework.transaction.annotation.Transactional;
//
//@Configuration
//@EnableTransactionManagement
//public class TransactionConfig {
//
//    private final PlatformTransactionManager transactionManager;
//
//    public TransactionConfig(PlatformTransactionManager transactionManager) {
//        this.transactionManager = transactionManager;
//    }
//
//    @Bean(name = "customTransactionInterceptor")
//    public TransactionInterceptor customTransactionInterceptor() {
//        DefaultTransactionAttribute transactionAttribute = new DefaultTransactionAttribute();
//        transactionAttribute.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
//        AnnotationTransactionAttributeSource source = new AnnotationTransactionAttributeSource();
//        return new TransactionInterceptor(transactionManager, source);
//    }
//
//    @Bean(name = "customTransactionAdvisor")
//    public Advisor customTransactionAdvisor() {
//        AnnotationTransactionAttributeSource source = new AnnotationTransactionAttributeSource();
//        AnnotationMatchingPointcut pointcut = new AnnotationMatchingPointcut(null, Transactional.class);
//        return new DefaultPointcutAdvisor(pointcut, customTransactionInterceptor());
//    }
//}

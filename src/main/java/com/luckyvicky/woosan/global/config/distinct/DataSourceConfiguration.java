//package com.luckyvicky.woosan.global.config.distinct;
//
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.boot.jdbc.DataSourceBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.jdbc.datasource.DataSourceTransactionManager;
//import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.transaction.PlatformTransactionManager;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//import com.zaxxer.hikari.HikariDataSource;
//
//import javax.sql.DataSource;
//import java.util.HashMap;
//import java.util.Map;
//
//@Configuration
//@EnableTransactionManagement
//public class DataSourceConfiguration {
//
//    public static final String MASTER_DATASOURCE = "masterDataSource";
//    public static final String SLAVE_DATASOURCE = "slaveDataSource";
//
//    @Bean(MASTER_DATASOURCE)
//    @ConfigurationProperties(prefix = "spring.datasource.master.hikari")
//    public DataSource masterDataSource() {
//        return DataSourceBuilder.create()
//                .type(HikariDataSource.class)
//                .build();
//    }
//
//    @Bean(SLAVE_DATASOURCE)
//    @ConfigurationProperties(prefix = "spring.datasource.slave.hikari")
//    public DataSource slaveDataSource() {
//        return DataSourceBuilder.create()
//                .type(HikariDataSource.class)
//                .build();
//    }
//
//    @Bean
//    @Primary
//    public DataSource routingDataSource() {
//        RoutingDataSource routingDataSource = new RoutingDataSource();
//        Map<Object, Object> dataSourceMap = new HashMap<>();
//        dataSourceMap.put(MASTER_DATASOURCE, masterDataSource());
//        dataSourceMap.put(SLAVE_DATASOURCE, slaveDataSource());
//        routingDataSource.setTargetDataSources(dataSourceMap);
//        routingDataSource.setDefaultTargetDataSource(masterDataSource());
//        return routingDataSource;
//    }
//
//    @Bean
//    public PlatformTransactionManager transactionManager(@Qualifier("routingDataSource") DataSource dataSource) {
//        return new DataSourceTransactionManager(dataSource);
//    }
//}

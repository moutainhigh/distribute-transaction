package com.wuqiong.tx.config;

import com.wuqiong.tx.context.ApplicationContextHelper;
import com.wuqiong.tx.datasource.MultiDatasource;
import com.wuqiong.tx.transaction.MyTransactionManager;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description
 * @Author Cain
 * @date 2020/8/27
 */
@Configuration
@EnableTransactionManagement
@ComponentScan("com.wuqiong.tx")
@MapperScan("com.wuqiong.tx.mapper")
public class MyConfiguration {

    @Primary
    @Bean
    public DataSource defaultDatasource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://127.0.0.1:3306/test-sys");
        dataSource.setUsername("root");
        dataSource.setPassword("root");
        dataSource.setCatalog("test-sys");
        return dataSource;
    }

    @Bean
    public MultiDatasource multiDatasource(@Qualifier("defaultDatasource") DataSource defaultDatasource) {
        Map<Object, Object> targetDataSource = new HashMap<Object, Object>();
        targetDataSource.put("test-sys", defaultDatasource);
        MultiDatasource dataSource = new MultiDatasource();
        dataSource.setTargetDataSources(targetDataSource);
        dataSource.setDefaultTargetDataSource(defaultDatasource);
        return dataSource;
    }

    //@Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager(MultiDatasource multiDatasource) {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(multiDatasource);
        return transactionManager;
    }

    @Bean(name = "myTransactionManager")
    public MyTransactionManager myTransactionManager(MultiDatasource multiDatasource) {
        MyTransactionManager myTransactionManager = new MyTransactionManager();
        myTransactionManager.setDataSource(multiDatasource);
        return myTransactionManager;
    }

    @Bean(name = "SqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(MultiDatasource multiDatasource)
            throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(multiDatasource);
        bean.setMapperLocations(
                new PathMatchingResourcePatternResolver().getResources("classpath*:mapper/*.xml"));
        return bean.getObject();
    }

    @Bean
    public ApplicationContextHelper applicationContextHelper() {
        return new ApplicationContextHelper();
    }
}

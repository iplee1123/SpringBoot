package com.example.springboot.batch.datasource;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.util.Properties;

@Slf4j
@Configuration
@MapperScan(basePackages = {"com.example.springboot.batch.mapper.read"}, sqlSessionFactoryRef = "oracleSqlSessionFactory")
@MapperScan(basePackages = {"com.example.springboot.batch.mapper.write"}, sqlSessionFactoryRef = "mssqlSqlSessionFactory")
public class MybatisConfig {


    private Properties getConfiguration() {
        Properties properties = new Properties();
        properties.setProperty("mapUnderscoreToCamelCase", "true");
        return properties;
    }

    @Bean(name = "h2SqlSessionFactory")
    @Primary
    public SqlSessionFactory h2SqlSessionFactory(@Qualifier("h2DataSource") DataSource dataSource, ApplicationContext applicationContext) throws Exception{
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        return sqlSessionFactoryBean.getObject();

    }

    @Bean(name = "oracleSqlSessionFactory")
    public SqlSessionFactory oracleSqlSessionFactory(@Qualifier("oracleDataSource") DataSource dataSource, ApplicationContext applicationContext) throws Exception{
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        //sqlSessionFactoryBean.setConfigLocation(applicationContext.getResource("classpath:mybatis/mybatis-config.xml"));
        sqlSessionFactoryBean.setMapperLocations(applicationContext.getResource("classpath:mybatis/oracle/read.xml"));
        sqlSessionFactoryBean.setConfigurationProperties(this.getConfiguration());
        sqlSessionFactoryBean.getObject().getConfiguration().setMapUnderscoreToCamelCase(true);
        return sqlSessionFactoryBean.getObject();

    }

    @Bean(name = "mssqlSqlSessionFactory")
    public SqlSessionFactory mssqlSqlSessionFactory(@Qualifier("mssqlDataSource") DataSource dataSource, ApplicationContext applicationContext) throws Exception{
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setConfigurationProperties(this.getConfiguration());
        //sqlSessionFactoryBean.setConfigLocation(applicationContext.getResource("classpath:mybatis/mybatis-config.xml"));
        sqlSessionFactoryBean.setMapperLocations(applicationContext.getResource("classpath:mybatis/mssql/write.xml"));
        return sqlSessionFactoryBean.getObject();
    }
}

package com.glyde.mall.batch.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableTransactionManagement
public class XACJDBconfig {
	
	@Value("${spring.jta.atomikos.datasource.cj.unique-resource-name}")
	private String uniqueResourceName;

	@Value("${spring.jta.atomikos.datasource.cj.xa-data-source-class-name}")
	private String xaDataSourceClassName;

	@Value("${spring.jta.atomikos.datasource.cj.xa-properties.user}")
	private String user;

	@Value("${spring.jta.atomikos.datasource.cj.xa-properties.password}")
	private String password;

	@Value("${spring.jta.atomikos.datasource.cj.xa-properties.URL}")
	private String url;

	@Value("${mybatis.mapper-locations}")
	private String[] mapperLocation;

	@Value("${mybatis.config-location}")
	private String configLocation;

	@Bean(name = "dataSourceCJ")
	@Lazy
	public DataSource dataSource() {
		AtomikosDataSourceBean ds = new AtomikosDataSourceBean();
		ds.setUniqueResourceName(uniqueResourceName);
		ds.setXaDataSourceClassName(xaDataSourceClassName);

		ds.setMinPoolSize(2);
		ds.setMaxPoolSize(5);

		log.info("Glyde MinPoolSize:" + ds.getMinPoolSize());
		log.info("Glyde MaxPoolSize:" + ds.getMaxPoolSize());

		Properties p = new Properties();
		p.setProperty("user", user);
		p.setProperty("password", password);
		p.setProperty("URL", url);
		ds.setXaProperties(p);

		return ds;

	}

	@Bean(name = "sqlSessionFactoryCJ")
	@Lazy
	public SqlSessionFactory sqlSessionFactory(@Qualifier("dataSourceCJ") DataSource dataSource) throws Exception {
		final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
		sessionFactory.setDataSource(dataSource);
		PathMatchingResourcePatternResolver pathResolver = new PathMatchingResourcePatternResolver();
		sessionFactory.setDataSource(dataSource);
		sessionFactory.setConfigLocation(pathResolver.getResource(configLocation));
		sessionFactory.setMapperLocations(pathResolver.getResources(mapperLocation[1]));

		return sessionFactory.getObject();
	}
}

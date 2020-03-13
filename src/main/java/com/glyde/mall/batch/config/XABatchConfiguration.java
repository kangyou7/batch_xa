package com.glyde.mall.batch.config;

import javax.sql.DataSource;

import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.JobParametersIncrementer;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.JobExplorerFactoryBean;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.dao.AbstractJdbcBatchMetadataDao;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.glyde.mall.batch.config.listener.BatchJobListener;
import com.glyde.mall.batch.config.listener.BatchStepListener;

import lombok.extern.slf4j.Slf4j;

@Lazy
@Slf4j
@EnableBatchProcessing
@EnableTransactionManagement
@Configuration
public class XABatchConfiguration implements BatchConfigurer {

	@Autowired
	@Qualifier("dataSourceGlyde")
	protected DataSource dataSource;
	
	@Autowired
	@Qualifier("multiTxManager")
	protected PlatformTransactionManager platformTransactionManager;	

	@Override
	public JobRepository getJobRepository() throws Exception {
		JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
		factory.setDatabaseType("POSTGRES");
		factory.setDataSource(dataSource);
		factory.setTransactionManager(getTransactionManager());
		factory.setIsolationLevelForCreate("ISOLATION_DEFAULT");
		factory.setTablePrefix(AbstractJdbcBatchMetadataDao.DEFAULT_TABLE_PREFIX);
		factory.afterPropertiesSet();

		JobRepository repository = factory.getObject();

		log.info("spring-batch repository:{}, datasource:{}", repository, dataSource);

		return factory.getObject();
	}

	@Override
	public JobLauncher getJobLauncher() throws Exception {
		SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
		jobLauncher.setJobRepository(getJobRepository());
		jobLauncher.afterPropertiesSet();
		return jobLauncher;
	}


	@Override
	public JobExplorer getJobExplorer() throws Exception {
		JobExplorerFactoryBean jobExplorerFactory = new JobExplorerFactoryBean();
		jobExplorerFactory.setDataSource(dataSource);
		jobExplorerFactory.setTablePrefix(AbstractJdbcBatchMetadataDao.DEFAULT_TABLE_PREFIX);
		jobExplorerFactory.afterPropertiesSet();

		return jobExplorerFactory.getObject();
	}

	@Bean
	public JobOperator jobOperator(JobExplorer explorer, JobLauncher launcher, JobRegistry registry,
			JobRepository repository) {
		SimpleJobOperator operator = new SimpleJobOperator();
		operator.setJobExplorer(explorer);
		operator.setJobLauncher(launcher);
		operator.setJobRegistry(registry);
		operator.setJobRepository(repository);
		return operator;
	}

	@Bean
	public JobExecutionListener jobExecutionListener() {
		return new BatchJobListener();
	}

	@Bean
	public StepExecutionListener stepExecutionListener() {
		return new BatchStepListener();
	}

	@Bean
	public JobParametersIncrementer jobParameterIncrementer() {
		return new BatchJobParametersIncrementer();
	}

	@Override
	public PlatformTransactionManager getTransactionManager() throws Exception {
		return platformTransactionManager;
	}

}

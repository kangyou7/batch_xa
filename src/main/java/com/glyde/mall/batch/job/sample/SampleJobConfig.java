package com.glyde.mall.batch.job.sample;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.MyBatisCursorItemReader;
import org.mybatis.spring.batch.builder.MyBatisBatchItemWriterBuilder;
import org.mybatis.spring.batch.builder.MyBatisCursorItemReaderBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.JobParametersIncrementer;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.glyde.mall.batch.config.listener.BatchStepListener;
import com.glyde.mall.batch.job.sample.entity.TestDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class SampleJobConfig {
	
	@Autowired
	@Qualifier("sqlSessionFactoryGlyde")
	SqlSessionFactory sqlSessionFactory;

	@Autowired
	private JobParametersIncrementer incrementer;

	@Autowired
	private JobExecutionListener jobListener;

	@Autowired
	private StepExecutionListener stepListener;

	@Autowired
	JobBuilderFactory jobs;

	@Autowired
	StepBuilderFactory steps;

	private static final int CHUNK_SIZE = 100;

	/**
	 * Sample job job. Program argument 추가 후 BTStarter.exe 로 서비스 실행
	 * --spring.batch.job.names=com.glyde.mall.batch.job.sample.sampleJob.sampleJob
	 * --executor.name="BATCH_EXECUTOR"
	 *
	 * @return the job
	 * @throws Exception the exception
	 */
	@Bean(name = "com.glyde.mall.batch.job.sample.sampleJob")
	public Job sampleJob() throws Exception {
		return jobs.get("com.glyde.mall.batch.job.sample.sampleJob")
				.start(sampleStep())
				.next(taskletStep())
				.incrementer(incrementer)
				.listener(jobListener)
				.build();
	}

	/**
	 * Sample step step.
	 *
	 * @return the step
	 * @throws Exception the exception
	 */
	@Bean(name = "com.glyde.mall.batch.job.sample.sampleStep")
	@JobScope
	public Step sampleStep() throws Exception {
		return steps.get("com.glyde.mall.batch.job.sample.sampleStep")
				.listener(stepListener)
				.allowStartIfComplete(true)
				.<TestDto, TestDto>chunk(CHUNK_SIZE)
				.reader(sampleReader())
				.processor(sampleProcessor())
				.writer(sampleWriter())
				.build();
	}

	/**
	 * Sample reader item reader.
	 *
	 * @return the item reader
	 * @throws Exception the exception
	 */
	@Bean(name = "com.glyde.mall.batch.job.sample.sampleReader")
	@StepScope
	public MyBatisCursorItemReader<TestDto> sampleReader() throws Exception {
		return new MyBatisCursorItemReaderBuilder<TestDto>().sqlSessionFactory(sqlSessionFactory)
				.queryId("com.glyde.mall.batch.job.sample.mapper.TestMapper.select").build();
	}

	/**
	 * Sample processor item processor.
	 *
	 * @return the item processor
	 */
	@Bean(name = "com.glyde.mall.batch.job.sample.sampleProcessor")
	@StepScope
	public ItemProcessor<TestDto, TestDto> sampleProcessor() {
		return new SampleProcessor();
	}

	/**
	 * First writer item writer.
	 *
	 * @return the item writer
	 */
	@Bean(name = "com.glyde.mall.batch.job.sample.sampleWriter")
	@StepScope
	public MyBatisBatchItemWriter<TestDto> sampleWriter() {
		return new MyBatisBatchItemWriterBuilder<TestDto>().sqlSessionFactory(sqlSessionFactory)
				.statementId("com.glyde.mall.batch.job.sample.mapper.TestMapper.update").assertUpdates(true).build();
	}

	/**
	 * Tasklet step step.
	 *
	 * @return the step
	 */
	@Bean(name = "com.glyde.mall.batch.job.sample.taskletStep")
	@JobScope
	public Step taskletStep() {
		return steps.get("com.glyde.mall.batch.job.sample.taskletStep").listener(new BatchStepListener())
				.allowStartIfComplete(true).tasklet(sampleTasklet()).build();
	}

	/**
	 * Sample tasklet tasklet.
	 *
	 * @return the tasklet
	 */
	@Bean(name = "com.glyde.mall.batch.job.sample.sampleTasklet")
	@StepScope
	public Tasklet sampleTasklet() {
		return new SampleTasklet();
	}

}
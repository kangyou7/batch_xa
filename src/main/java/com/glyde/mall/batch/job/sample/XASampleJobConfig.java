package com.glyde.mall.batch.job.sample;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisCursorItemReader;
import org.mybatis.spring.batch.builder.MyBatisBatchItemWriterBuilder;
import org.mybatis.spring.batch.builder.MyBatisCursorItemReaderBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.JobParametersIncrementer;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.glyde.mall.batch.job.sample.entity.PeopleDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableBatchProcessing
public class XASampleJobConfig {

	@Autowired
	@Qualifier("sqlSessionFactoryGlyde")
	SqlSessionFactory DB_GLYDE;

	@Autowired
	@Qualifier("sqlSessionFactoryCJ")
	SqlSessionFactory DB_CJ;

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

	private static final int CHUNK_SIZE = 1;

	/**
	 * Sample job job. Program argument 추가 후 BTStarter.exe 로 서비스 실행
	 * --spring.batch.job.names=com.glyde.mall.batch.job.sample.XAsampleJob
	 * --executor.name="BATCH_EXECUTOR"
	 *
	 * insertGlydeToCJStep이 성공/실패와 관계없이 작업이 완료되면, updateCJToGlydeStep()을 작업을 진행한다.
	 * insertGlydeToCJStep에서 실패가 발생하면, step상태는 FIALED(실패)가 아니라, ABANDONED(재작업불가상태)
	 * 표시된다. 위 조건에선 step에 @Jobscope를 달지 않는다. start로 시작해서, on은 조건절. to 대상. from도 대상.
	 * 배치처리에서 chunk단위의 commit이 이루어지고, chunk단위의 rollback이 일어난다. 스텝과 스텝은 트랜젹션이 독립적이다.
	 * 
	 * @return the job
	 * @throws Exception the exception
	 */
	@Bean(name = "com.glyde.mall.batch.job.sample.XAsampleJob")
	public Job sampleJob() throws Exception {
		return this.jobs.get("com.glyde.mall.batch.job.sample.XAsampleJob").start(insertGlydeToCJStep()) // insertGlydeToCJStep
																											// 시작
				.on("*").to(updateCJToGlydeStep()) // insertGlydeToCJStep작업완료후,updateCJToGlydeStep진행.
				.from(insertGlydeToCJStep()).on("FAILED").to(updateCJToGlydeStep()) // insertGlydeToCJStep실패라도,
																					// updateCJToGlydeStep진행.
				.end().incrementer(incrementer).listener(jobListener).build();
	}

	/**
	 * read(glyde) -> insert step(cj)
	 *
	 * @return the step
	 * @throws Exception the exception
	 */
	@Bean(name = "com.glyde.mall.batch.job.sample.XAinsertStep")
	public Step insertGlydeToCJStep() throws Exception {
		return steps.get("com.glyde.mall.batch.job.sample.XAinsertStep").listener(stepListener)
				.allowStartIfComplete(true).<PeopleDto, PeopleDto>chunk(CHUNK_SIZE).reader(glydeReader())
				.writer(cjWriter()).build();
	}

	/**
	 * read(cj) -> update step(glyde)
	 *
	 * @return the step
	 * @throws Exception the exception
	 */
	@Bean(name = "com.glyde.mall.batch.job.sample.XAupdateStep")
	public Step updateCJToGlydeStep() throws Exception {
		return steps.get("com.glyde.mall.batch.job.sample.XAupdateStep").listener(stepListener)
				.allowStartIfComplete(true).<PeopleDto, PeopleDto>chunk(CHUNK_SIZE).reader(cjReader())
				.processor(sampleProcessor()).writer(glydeWriter()).build();
	}

	/**
	 * Glyde reader.
	 *
	 * @return the item reader
	 * @throws Exception the exception
	 */
	@Bean(name = "com.glyde.mall.batch.job.sample.glydeReader")
	@StepScope
	public MyBatisCursorItemReader<PeopleDto> glydeReader() throws Exception {
		return new MyBatisCursorItemReaderBuilder<PeopleDto>().sqlSessionFactory(DB_GLYDE)
				.queryId("com.glyde.mall.batch.job.sample.mapper.PeopleMapper.select").build();
	}

	/**
	 * CJ reader.
	 *
	 * @return the item reader
	 * @throws Exception the exception
	 */
	@Bean(name = "com.glyde.mall.batch.job.sample.cjReader")
	@StepScope
	public MyBatisCursorItemReader<PeopleDto> cjReader() throws Exception {
		return new MyBatisCursorItemReaderBuilder<PeopleDto>().sqlSessionFactory(DB_CJ)
				.queryId("com.glyde.mall.batch.job.sample.mapper.PeopleMapper.select").build();
	}

	/**
	 * Sample processor item processor.
	 *
	 * @return the item processor
	 */
	@Bean(name = "com.glyde.mall.batch.job.sample.XAsampleProcessor")
	@StepScope
	public ItemProcessor<PeopleDto, PeopleDto> sampleProcessor() {
		return new XAsampleProcessor();
	}

	/**
	 * CJ item writer.
	 *
	 * @return the item writer
	 */
	@Bean(name = "com.glyde.mall.batch.job.sample.cjWriter")
	@StepScope
	public ItemWriter<PeopleDto> cjWriter() {
		return new MyBatisBatchItemWriterBuilder<PeopleDto>().sqlSessionFactory(DB_CJ)
				.statementId("com.glyde.mall.batch.job.sample.mapper.PeopleMapper.insert").build();
	}

	/**
	 * Glyde item writer.
	 *
	 * @return the item writer
	 */
	@Bean(name = "com.glyde.mall.batch.job.sample.glydeWriter")
	@StepScope
	public ItemWriter<PeopleDto> glydeWriter() {
		return new MyBatisBatchItemWriterBuilder<PeopleDto>().sqlSessionFactory(DB_GLYDE)
				.statementId("com.glyde.mall.batch.job.sample.mapper.PeopleMapper.update").build();
	}

}
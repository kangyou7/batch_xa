package com.glyde.mall.batch.job.sample;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.builder.MyBatisBatchItemWriterBuilder;
import org.mybatis.spring.batch.builder.MyBatisPagingItemReaderBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.JobParametersIncrementer;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.glyde.mall.batch.job.sample.entity.TestDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class SampleJobParameterConfig {

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
     * Sample job job.
     * Program argument 추가 후 BTStarter.exe 로 서비스 실행
     * --spring.batch.job.names=com.glyde.mall.batch.job.sample.sampleJobParameter
     * --executor.name="BATCH_EXECUTOR"
     *
     * @return the job
     * @throws Exception the exception
     */
    @Bean(name="com.glyde.mall.batch.job.sample.sampleParameterJob")
    public Job sampleJob() throws Exception  {
        return jobs.get("com.glyde.mall.batch.job.sample.sampleParameterJob")
                .start(sampleStep(null)) // 아래코드 sampleStep에서 jobparameter를 사용한다.
                .incrementer(incrementer)
                .listener(jobListener)
                .build();
        
        /**
         *  이 job를 실행할 때, cmd line에 아래 예시와 같이 적는다.
         *  
         *  --spring.batch.job.names=com.glyde.mall.batch.job.sample.sampleParameterJob inputString=God
         *  
         *  위 job을 실행하면, select name from test를 했을 때, inputString과 동일한 값으로 업데이트 된다.
         *  
         *  여기서 jobparameter는 inputString 이다. 여기서는 jobparamter 가 1개이지만, 여러개 쓰고 싶으면
         *  inputString=God inputString2=God2 inputString3=God3 처럼 한칸 띄워쓰기하고 계속 적어주면 된다.
         *  
         * 
         */
        
        /**
         * Jobparameter는 @Jobscope와 @Stepscope 에서 사용할 수 있다.
         * 실제 작업이 일어나는 Step에 JobParameter를 사용하려면, job 선언시 null로 넣는다.
         * null로 넣는 이유는 Jobparameter는  @Jobscope와 @Stepscope 에서만 사용하기 때문이다.
         */
    }

    /**
     * Sample step step.
     *
     * @return the step
     * @throws Exception the exception
     */
    @Bean(name="com.glyde.mall.batch.job.sample.sampleParameterStep")
    @JobScope
    public Step sampleStep(  @Value("#{jobParameters[inputString]}") String  inputString) throws Exception {
    	
    	/**
    	 * jobparameter를 사용하는 방법은 @Value("#{jobParameters[파라미터명]}") 을 이용한다.
    	 * 여기서, jobParameters 스펠링을 주의하자. 복수형이다.
    	 * 
    	 * 여기는 @JobScope 영역이고, 여기서는 단순히 아래와 같이 문자열을 찍는다.	
    	 */
    	
    	System.out.println("=========================");
    	System.out.println("param========"+inputString);
    	System.out.println("=========================");
        return steps.get("com.glyde.mall.batch.job.sample.sampleParameterStep")
                .listener(stepListener)
                .allowStartIfComplete(true)
                .<TestDto,TestDto> chunk(CHUNK_SIZE)
                .reader(sampleReader())
                .processor(sampleProcessor(null)) // 아래, sampleProcessor에서 jobparameter를 사용한다.
                .writer(sampleWriter())
                .build();
    }

    /**
     * Sample reader item reader.
     *
     * @return the item reader
     * @throws Exception the exception
     */
    @Bean(name="com.glyde.mall.batch.job.sample.sampleParameterReader")
    @StepScope
    public ItemReader<TestDto> sampleReader() throws Exception {

        return new MyBatisPagingItemReaderBuilder<TestDto>()
                .sqlSessionFactory(sqlSessionFactory)
                .pageSize(CHUNK_SIZE)
                .queryId("com.glyde.mall.batch.job.sample.mapper.TestMapper.select")
                .build();
    }

    /**
     * Sample processor item processor.
     * @param param 
     *
     * @return the item processor
     */
    @Bean(name="com.glyde.mall.batch.job.sample.sampleParameterProcessor")
    @StepScope
    public ItemProcessor<TestDto, TestDto> sampleProcessor(@Value("#{jobParameters[inputString]}") String  inputString) {
    	
    	/**
    	 * @StepScope 에서 jobparameter는 sampleProcessor의 매개변수에 @Value("#{jobParameters[파라미터명]}" 을 선언한다.
    	 * 
    	 * 리턴은 jobparamter - inputString를 매개변수로 SampleParameterProcessor를 하고 있다.
    	 */

        return new SampleParameterProcessor(inputString);
    }

    /**
     * Sample writer item writer.
     *
     * @return the item writer
     */
    @Bean(name="com.glyde.mall.batch.job.sample.sampleParameterWriter")
    @StepScope
    public ItemWriter<TestDto> sampleWriter(){
        return new MyBatisBatchItemWriterBuilder<TestDto>()
                .sqlSessionFactory(sqlSessionFactory)
                .statementId("com.glyde.mall.batch.job.sample.mapper.TestMapper.update")
                .assertUpdates(true)
                .build();
    }


}
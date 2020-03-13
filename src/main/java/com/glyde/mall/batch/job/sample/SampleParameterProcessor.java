package com.glyde.mall.batch.job.sample;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;

import com.glyde.mall.batch.job.sample.entity.TestDto;

/**
 * The type Sample processor.
 */
public class SampleParameterProcessor implements ItemProcessor<TestDto, TestDto> {

	String param;
	
	public SampleParameterProcessor(String inputString) {
		this.param = inputString;
	}

	@Override
	@StepScope
	public TestDto process(TestDto sampleDto) throws Exception {
		
		/**
		 * 생성자를 이용해서 jobparameter를 받아 setName한다. 
		 * 즉, name을 jobparameter와 동일하게 변환했다.
		 */

		sampleDto.setName(param);
		return sampleDto;
	}
}

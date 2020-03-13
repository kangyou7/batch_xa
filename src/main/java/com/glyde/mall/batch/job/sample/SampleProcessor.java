package com.glyde.mall.batch.job.sample;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;

import com.glyde.mall.batch.job.sample.entity.TestDto;

/**
 * The type Sample processor.
 */
public class SampleProcessor implements ItemProcessor<TestDto, TestDto> {

    @Override
    @StepScope
    public TestDto process(TestDto sampleDto) throws Exception {
        sampleDto.setName(sampleDto.getName().toUpperCase());
        return sampleDto;
    }
}

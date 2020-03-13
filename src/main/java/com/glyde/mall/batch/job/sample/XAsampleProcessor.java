package com.glyde.mall.batch.job.sample;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;

import com.glyde.mall.batch.job.sample.entity.PeopleDto;

/**
 * The type Sample processor.
 */
public class XAsampleProcessor implements ItemProcessor<PeopleDto, PeopleDto> {

    @Override
    @StepScope
    public PeopleDto process(PeopleDto peopleDto) throws Exception {
    	peopleDto.setLastName("OK");
        return peopleDto;
    }
}

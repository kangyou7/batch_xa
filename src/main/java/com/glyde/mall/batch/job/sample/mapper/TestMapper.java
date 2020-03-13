package com.glyde.mall.batch.job.sample.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.glyde.mall.batch.job.sample.entity.TestDto;

@Mapper
public interface TestMapper  {

    public List<TestDto> select();

    public int update(TestDto dto);
}

package com.glyde.mall.batch.job.sample.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.glyde.mall.batch.job.sample.entity.PeopleDto;

@Mapper
public interface PeopleMapper  {

    public List<PeopleDto> select();

    public int update(PeopleDto dto);
    
    public int insert(PeopleDto dto);
    
    public int delete(PeopleDto dto); 
}

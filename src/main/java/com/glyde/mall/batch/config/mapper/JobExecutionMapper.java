package com.glyde.mall.batch.config.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.glyde.mall.batch.config.entity.BatchJob;
import com.glyde.mall.batch.config.entity.BatchLog;

@Mapper
public interface JobExecutionMapper {

	public BatchJob selectBatchInfo(BatchJob map);

	public int updateJobExecuteFlag(BatchJob map);

	public int mergeJobExecuteLog(BatchLog map);
}

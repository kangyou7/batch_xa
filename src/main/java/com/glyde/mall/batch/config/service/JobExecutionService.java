/* Copyright PT. Indo Lotte Makmur, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited Proprietary and confidential copying of this file, via any medium is strictly prohibited Proprietary and confidential
 *
 * Written by ECM project team
 */
package com.glyde.mall.batch.config.service;

import com.glyde.mall.batch.config.entity.BatchJob;
import com.glyde.mall.batch.config.entity.BatchLog;
import com.glyde.mall.batch.config.mapper.JobExecutionMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JobExecutionService {

    @Autowired
    private JobExecutionMapper jobExecutionRepository;

    public BatchJob getBatchInfo(BatchJob batchJob) throws Exception {
        return jobExecutionRepository.selectBatchInfo(batchJob);
    }

    public int updateJobExecuteFlag(BatchJob batchJob) throws Exception {
        return jobExecutionRepository.updateJobExecuteFlag(batchJob);
    }

    public int mergeJobExecuteLog(BatchLog batchLog) throws Exception {
        return jobExecutionRepository.mergeJobExecuteLog(batchLog);
    }

}

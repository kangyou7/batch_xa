/* Copyright PT. Indo Lotte Makmur, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited Proprietary and confidential copying of this file, via any medium is strictly prohibited Proprietary and confidential
 *
 * Written by ECM project team
 */
package com.glyde.mall.batch.config.listener;

import com.glyde.mall.batch.config.entity.BatchJob;
import com.glyde.mall.batch.config.service.JobExecutionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Slf4j
public class BatchJobListener implements JobExecutionListener {

    @Autowired
    private JobExecutionService jobExecutionService;

    @Override
    public void beforeJob(final JobExecution jobExecution) {
        log.info(">> Job [" + jobExecution.getJobInstance().getJobName() + "] is starting.");
        log.info(">> parameter : {} " , jobExecution.getJobParameters() );

        BatchJob parameter = new BatchJob();
        parameter.setBatchId(String.valueOf(jobExecution.getJobInstance().getInstanceId()));
        parameter.setBatchNm(jobExecution.getJobInstance().getJobName());
        parameter.setBatchSvrNm("BATCH");
        parameter.setBatchExeYn("Y");
        parameter.setLstModpId(StringUtils.defaultString(jobExecution.getJobParameters().getString("-executor.name"),"ANONYMOUS"));

        BatchJob job;
        try {
/*
            job = jobExecutionService.getBatchInfo(parameter);
            if ( job == null )  throw new RuntimeException("It is not a registered Batch or Unused Batch");
            log.info("Job : {}" , job.toString());
            int cnt = jobExecutionService.updateJobExecuteFlag(parameter);
            log.info("Job execution update {} : {}" , parameter.getBatchExeYn() , cnt > 0  );
*/

        } catch (Exception e) {
            throw new RuntimeException(jobExecution.getJobInstance().getJobName() , e);
        }
    }

    @Override
    public void afterJob(JobExecution jobExecution) {

        log.info(">> Job [" + jobExecution.getJobInstance().getJobName() + "] is finishing.");

        BatchJob parameter = new BatchJob();
        parameter.setBatchId(String.valueOf(jobExecution.getJobInstance().getInstanceId()));
        parameter.setBatchNm(jobExecution.getJobInstance().getJobName());
        parameter.setBatchSvrNm("BATCH");
        parameter.setBatchExeYn("N");
        parameter.setLstModpId(StringUtils.defaultString(jobExecution.getJobParameters().getString("-executor.name"),"ANONYMOUS"));

        try {
            List<Throwable> listThrowable = jobExecution.getAllFailureExceptions();
            if(CollectionUtils.isEmpty(listThrowable)) return;

            listThrowable.forEach(throwable -> {
                StringBuilder detailMessage = new StringBuilder(throwable.getMessage());
                if ( throwable.getCause() != null ) {
                    detailMessage.append( StringUtils.defaultString(throwable.getCause().getMessage()) );
                }
            });

/*
            int cnt = jobExecutionService.updateJobExecuteFlag(parameter);
            log.info("Job execution update {} : {}" , parameter.getBatchExeYn() , cnt > 0 );
*/

        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }

        log.info("Job execution {} : {}", jobExecution.getStatus() , jobExecution.getJobInstance());
    }
}

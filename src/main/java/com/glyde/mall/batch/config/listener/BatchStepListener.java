package com.glyde.mall.batch.config.listener;

import com.glyde.mall.batch.config.entity.BatchLog;
import com.glyde.mall.batch.config.service.JobExecutionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.util.Date;

@Slf4j
public class BatchStepListener implements StepExecutionListener {

    @Autowired
    private JobExecutionService jobExecutionService;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        log.info(">> Step [" + stepExecution.getStepName() + "] is starting.");
        log.info(">> Step summary : {} " , stepExecution.getSummary() );

        BatchLog batchLog = new BatchLog();
        batchLog.setBatchExeLogNo(String.valueOf(stepExecution.getId()));
        batchLog.setBatchNo(String.valueOf(stepExecution.getJobExecution().getJobInstance().getInstanceId()));
        batchLog.setBatchNm(stepExecution.getJobExecution().getJobInstance().getJobName());
        batchLog.setBatchProgStCd(stepExecution.getExitStatus().getExitCode());
        batchLog.setExeRstMsgCntt(stepExecution.getSummary());
        batchLog.setBatchStrtDtm(new Timestamp((new Date()).getTime()));
        String executor = StringUtils.defaultString(
                stepExecution.getJobExecution().getJobParameters().getString("-executor.name"),
                "ANONYMOUS");
        batchLog.setLstModpId(executor);
        batchLog.setFstRegpId(executor);

        try {
/*
            int cnt = jobExecutionService.mergeJobExecuteLog(batchLog);
            log.info("Step execution update {} : {}" , stepExecution.getExitStatus().getExitCode() , cnt > 0 );
*/
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info(">> Step [" + stepExecution.getStepName() + "] is finishing.");

        BatchLog batchLog = new BatchLog();
        batchLog.setBatchProgStCd(stepExecution.getExitStatus().getExitCode());
        batchLog.setBatchExeLogNo(String.valueOf(stepExecution.getId()));
        batchLog.setExeRstMsgCntt(stepExecution.getSummary());
        batchLog.setBatchEndDtm(new Timestamp((new Date()).getTime()));
        batchLog.setWrkCnt(Long.valueOf(stepExecution.getReadCount()));
        batchLog.setFailCnt(Long.valueOf(stepExecution.getWriteSkipCount()));
        String executor = StringUtils.defaultString(
                stepExecution.getJobExecution().getJobParameters().getString("-executor.name"),
                "ANONYMOUS");
        batchLog.setLstModpId(executor);

        try {
/*
            int cnt = jobExecutionService.mergeJobExecuteLog(batchLog);
            log.info("Step execution update {} : {}" , stepExecution.getExitStatus().getExitCode() , cnt > 0 );
*/
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }

        log.info("Step execution {} : {}", stepExecution.getExitStatus().getExitCode() , stepExecution.getStepName());

        return stepExecution.getExitStatus();
    }
}

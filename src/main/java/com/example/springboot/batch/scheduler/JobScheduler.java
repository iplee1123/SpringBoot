package com.example.springboot.batch.scheduler;

import com.example.springboot.batch.job.config.JobConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@EnableScheduling
public class JobScheduler {

    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private JobConfig jobConfig;
    @Autowired
    private Job jobWithTasklet;
    @Autowired
    private Job jobWithChunk;

    @Scheduled(cron = "1 * * * * *")
    public void runJob() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {

        // spring batch meta table 생성을 위한 schema file
        // ex) schema-h2.sqlR

        Map<String, JobParameter> jobParametersMap = new HashMap<>();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        String time = format.format(new Date());
        jobParametersMap.put("date", new JobParameter(time));
        jobParametersMap.put("name", new JobParameter(jobWithTasklet.getName()));

//        JobParameters parameters = new JobParameters(jobParametersMap);
//        JobExecution jobExecution = jobLauncher.run(jobWithTasklet, parameters);
//        printLog(jobExecution);

        jobParametersMap.put("name", new JobParameter(jobWithChunk.getName()));
        JobParameters parameters = new JobParameters(jobParametersMap);
        JobExecution jobExecution = jobLauncher.run(jobWithChunk, parameters);
        log.info("runJob End...");
      //  printLog(jobExecution);
    }

    public void printLog(JobExecution jobExecution) {
        log.info("Job Execution: " + jobExecution.getStatus());
        log.info("Job getJobConfigurationName: " + jobExecution.getJobConfigurationName());
        log.info("Job getJobId: " + jobExecution.getJobId());
        log.info("Job getExitStatus: " + jobExecution.getExitStatus());
        log.info("Job getJobInstance: " + jobExecution.getJobInstance());
        log.info("Job getStepExecutions: " + jobExecution.getStepExecutions());
        log.info("Job getLastUpdated: " + jobExecution.getLastUpdated());
        log.info("Job getFailureExceptions: " + jobExecution.getFailureExceptions());
    }
}

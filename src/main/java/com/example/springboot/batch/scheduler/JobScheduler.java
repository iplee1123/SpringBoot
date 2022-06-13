package com.example.springboot.batch.scheduler;

import com.example.springboot.batch.job.config.JobConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobScheduler {

    private final JobLauncher jobLauncher;
    private final JobConfig jobConfig;
    private final Job jobWithTasklet;
    private final Job jobWithChunk;
    private final Job mybatisTestJob;

    @Scheduled(cron = "1 * * * * *")
    public void runJob() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {

        // spring batch meta table 생성을 위한 schema file
        // ex) schema-h2.sql

        Map<String, JobParameter> jobParametersMap = new HashMap<>();
        // LocalDate startDate = LocalDate.now(ZoneId.of("Asia/Seoul"));
        // jobParametersMap.put("startDate", new JobParameter(startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));

        LocalDateTime startDate = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        jobParametersMap.put("startDate", new JobParameter(startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));

        jobParametersMap.put("jobName", new JobParameter(mybatisTestJob.getName()));
        JobParameters parameters = new JobParameters(jobParametersMap);
        JobExecution jobExecution = jobLauncher.run(mybatisTestJob, parameters);
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

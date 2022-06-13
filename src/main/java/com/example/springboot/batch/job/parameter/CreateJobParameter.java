package com.example.springboot.batch.job.parameter;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Getter
@NoArgsConstructor
public class CreateJobParameter {

    private LocalDateTime startDate;
    private String jobName;

    @Value("#{jobParameters[startDate]}")
    public void setStartDate(String startDate) {
        this.startDate = LocalDateTime.parse(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    @Value("#{jobParameters[jobName]}")
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }
}

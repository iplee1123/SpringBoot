package com.example.springboot.batch.job.config;

import com.example.springboot.batch.job.parameter.CreateJobParameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MybatisTestJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final Step mybatisTestTaskletStep;
    private final Step mybatisTestChunkStep;
    private final Step taskletStep; //jobConfig에 있다

    @Bean
    public Job mybatisTestJob() {
        return jobBuilderFactory
                .get("mybatisTestJob2")
                //.start(mybatisTestTaskletStep) // write_table 테이블 데이터 초기화
                //.next(mybatisTestChunkStep) // read_table -> write_table
                //.start(mybatisTestChunkStep)
                .start(taskletStep)
                .build();
    }

}

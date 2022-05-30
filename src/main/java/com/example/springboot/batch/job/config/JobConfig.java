package com.example.springboot.batch.job.config;

import com.example.springboot.batch.mapper.ReadMapper;
import com.example.springboot.batch.model.Read;
import com.example.springboot.batch.model.Write;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.mybatis.spring.batch.builder.MyBatisBatchItemWriterBuilder;
import org.mybatis.spring.batch.builder.MyBatisPagingItemReaderBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.item.*;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
@EnableBatchProcessing
public class JobConfig {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Autowired
    private ReadMapper readMapper;

    @Bean
    public Job jobWithTasklet() {
        return this.jobBuilderFactory.get("jobWithTasklet")
                .start(taskletStep(null, null))
                .build();
    }

    @Bean
    public Job jobWithChunk() {
        return this.jobBuilderFactory.get("jobWithChunk")
                .start(chunkStepWithMybatis())
                //.next(chunkStepTest())
                .build();
    }

    @Bean
    @JobScope
    public Step taskletStep(@Value("#{jobParameters[date]}") String date, @Value("#{jobParameters[name]}") String name) {
        return this.stepBuilderFactory.get("taskletStep")
                .tasklet((contribution, chunkContext) -> {
                    log.info("taskletStep process..");
                    log.info("taskletStep date : " + date);
                    log.info("taskletStep name : " + name);
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    @JobScope
    public Step chunkStepWithMybatis() {
        return this.stepBuilderFactory.get("chunkStepWithMybatis")
                .<Read, Write>chunk(10)
                .reader(mybatisReader())
                .processor(mybatisProcessor())
                .writer(mybatisWriter())
                .build();
    }

    @Bean
    @StepScope
    public MyBatisPagingItemReader<Read> mybatisReader() {
        log.info("mybatisReader... ");
        Map<String, Object> parameterValues = new HashMap<>();
        parameterValues.put("readId", "1");
        return new MyBatisPagingItemReaderBuilder<Read>()
                .pageSize(10)
                .sqlSessionFactory(sqlSessionFactory)
                .queryId("com.example.springboot.batch.mapper.ReadMapper.findAll")
                //.parameterValues(parameterValues)
                .build();

        //Mapper안에서도 Paging 처리 시 OrderBy는 필수!
        //query + limit #{_skiprows}, #{_pagesize}"
    }

    @Bean
    // @StepScope
    public ItemProcessor<Read, Write> mybatisProcessor(){
        log.info("mybatisProcessor... ");
        return new ItemProcessor<Read, Write>() {
            @Override
            public Write process(Read read) throws Exception {
                log.info("ItemProcess... ");
                Write write = new Write();
                write.setWriteId(read.getReadId());
                write.setWriteName(read.getReadName() + "님");
                return write;
            }
        };
    }

    @Bean
    @StepScope
    public MyBatisBatchItemWriter<Write> mybatisWriter(){
        log.info("mybatisWriter... ");
        return new MyBatisBatchItemWriterBuilder<Write>()
                .sqlSessionFactory(sqlSessionFactory)
                .statementId("com.example.springboot.batch.mapper.WriteMapper.save")
                .build();
    }

    @Bean
    @JobScope
    public Step chunkStepTest() {
        log.info("stepTest... ");
        return this.stepBuilderFactory.get("chunkStepTest")
                .<String, String>chunk(5)
                .reader(readerTest())
                .processor(processorTest())
                .writer(writerTest())
                .build();
    }
    @Bean
    @StepScope
    public ItemReader<String> readerTest() {
        log.info("readerTest... ");
        List<String> list = new ArrayList<>();
        return new ItemReader<String>() {
            @Override
            public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
                log.info("ItemReader.read()... ");
                list.add("123");
                return list.size() == 1 ? "test" : null;
            }
        };
    }

    @Bean
    // @StepScope
    public ItemProcessor<String, String> processorTest(){
        log.info("processorTest... ");
        return new ItemProcessor<String, String>() {
            @Override
            public String process(String str) throws Exception {
                log.info("ItemProcessor.process()... ");
                return str + "님";
            }
        };
    }

    @Bean
    @StepScope
    public ItemWriter<String> writerTest(){
        log.info("writerTest... ");
        return new ItemWriter<String>() {
            @Override
            public void write(List<? extends String> list) throws Exception {
                log.info("ItemWriter.write()... ");
                list.forEach(s -> log.info(s));
            }
        };
    }
}

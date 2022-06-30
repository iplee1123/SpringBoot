package com.example.springboot.batch.job.step;

import com.example.springboot.batch.job.parameter.CreateJobParameter;
import com.example.springboot.batch.mapper.WriteMapper;
import com.example.springboot.batch.model.Read;
import com.example.springboot.batch.model.Write;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.mybatis.spring.batch.builder.MyBatisBatchItemWriterBuilder;
import org.mybatis.spring.batch.builder.MyBatisPagingItemReaderBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MybatisTestStepConfig {

    private final StepBuilderFactory stepBuilderFactory;
    private final SqlSessionFactory sqlSessionFactory;
    private final SqlSessionTemplate sqlSessionTemplate;
    private final CreateJobParameter jobParameter;
    private final WriteMapper writeMapper;

    @Bean
    @JobScope
    public Step mybatisTestTaskletStep() {
        return this.stepBuilderFactory
                .get("mybatisTestTaskletStep")
                .tasklet((stepContribution, chunkContext) -> {
                    writeMapper.delete();
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    @JobScope
    public Step mybatisTestChunkStep() {
        return this.stepBuilderFactory
                .get("mybatisTestChunkStep")
                .<Read, Write>chunk(10)
                .reader(mybatisTestReader())
                .processor(mybatisTestProcessor())
                .writer(mybatisTestWriter())
                .build();
    }

    @Bean
    @StepScope
    public MyBatisPagingItemReader<Read> mybatisTestReader() {
        log.info("mybatisTestReader... ");
        log.info("startDate : " + jobParameter.getStartDate());
        log.info("jobName : " + jobParameter.getJobName());
        writeMapper.delete();

//        Map<String, Object> parameterValues = new HashMap<>();
//        parameterValues.put("readId", "1");
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
    @StepScope
    public ItemProcessor<Read, Write> mybatisTestProcessor(){
        log.info("mybatisTestProcessor... ");
        return read -> {
            Write write = new Write();
            write.setWriteId(read.getReadId());
            write.setWriteName(read.getReadName() + "님");
            return write;
        };
    }

    @Bean
    @StepScope
    public MyBatisBatchItemWriter<Write> mybatisTestWriter(){
        log.info("mybatisTestWriter... ");
        return new MyBatisBatchItemWriterBuilder<Write>()
                .sqlSessionFactory(sqlSessionFactory)
                .statementId("com.example.springboot.batch.mapper.WriteMapper.save")
                .build();
    }
}

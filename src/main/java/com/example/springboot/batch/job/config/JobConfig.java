package com.example.springboot.batch.job.config;

import com.example.springboot.batch.job.parameter.CreateJobParameter;
import com.example.springboot.batch.mapper.read.ReadMapper;
import com.example.springboot.batch.mapper.write.WriteMapper;
import com.example.springboot.batch.model.ContractCompany;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.*;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final CreateJobParameter jobParameter;
    private final ReadMapper readMapper;
    private final WriteMapper writeMapper;

    @Bean
    @JobScope
    public CreateJobParameter jobParameter() {
        return new CreateJobParameter();
    }

    //@Bean
    public Job jobWithTasklet() {
        return this.jobBuilderFactory.get("jobWithTasklet")
                .start(taskletStep())
                .build();
    }

    //@Bean
    public Job jobWithChunk() {
        return this.jobBuilderFactory.get("jobWithChunk")
                .start(chunkStepTest())
                .build();
    }

    @Bean
    @JobScope
    public Step taskletStep() {
        return this.stepBuilderFactory.get("taskletStep")
                .tasklet((contribution, chunkContext) -> {
                    log.info("taskletStep process..");
                   // log.info("taskletStep date : " + jobParameter.getStartDate());
                   // log.info("taskletStep name : " + jobParameter.getJobName());

                    Map<String, Integer> map = new HashMap<>();
                    int start = 5100;
                    int end = 5100;

                    List<ContractCompany> list = null;
                    do {
                        start = end;
                        end += 100;
                        map.put("start", start);
                        map.put("end", end);
                        list = readMapper.selectContractCompany(map);
                        list.forEach(cc -> {
                            try {
                                String formContent = cc.getFormContent();
                                String nameLoc = cc.getNameLoc();
                                String formName = cc.getFormName();
                                String description = cc.getDescription();
                                if (null != formContent && !"".equals(formContent)) {
                                    cc.setFormContent(new String(formContent.getBytes("8859_1"), "KSC5601"));
                                }
                                if (null != nameLoc && !"".equals(nameLoc)) {
                                    cc.setNameLoc(new String(nameLoc.getBytes("8859_1"), "KSC5601"));
                                }
                                if (null != formName && !"".equals(formName)) {
                                    cc.setFormName(new String(formName.getBytes("8859_1"), "KSC5601"));
                                }
                                if (null != description && !"".equals(description)) {
                                    cc.setDescription(new String(description.getBytes("8859_1"), "KSC5601"));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //log.debug(cc.toString());
                            writeMapper.insertContractCompany(cc);
                        });
                    } while(list.size() > 0);
                    return RepeatStatus.FINISHED;
                })
                .allowStartIfComplete(true)
                .build();
    }

    //@Bean
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
    //@Bean
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

    //@Bean
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

    //@Bean
   // @StepScope
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

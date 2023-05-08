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
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.*;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

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
        log.debug("taskletStep");
        return this.stepBuilderFactory.get("taskletStep")
                .tasklet(oracleToMssql())
                //.tasklet(csvToMssql())
                .allowStartIfComplete(true)
                .build();
    }

    public Tasklet csvToMssql() {
        return (contribution, chunkContext) -> {
            log.debug("csvToMssql");
            String path = "C:/emro/mig/";
            File dir = new File(path);
            File[] files = dir.listFiles();
            String fileName = "";
            boolean isFirst = false;

            assert files != null;
            String uuid = UUID.randomUUID().toString();
            for (File file : files) {

                if(!fileName.equals(file.getName())) {
                    fileName = file.getName();
                    isFirst = true;
                }
                String tableName = fileName.substring(0,  fileName.lastIndexOf("_"));
                List<Map<String, Object>> perRequests = null;
                try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "MS949"))) {

                    List<Map<String, Object>> list = new ArrayList<>();
                    HashMap<String, Object> map;
                    String[] keys = new String[0];
                    String[] values;
                    String line;

                    while((line = br.readLine()) != null) {
                        line = line.replaceAll("\"", "").trim();
                        if(isFirst) {
                            keys = line.split("\t");
                            isFirst = false;
                        } else {
                            map = new HashMap<>();
                            list.add(map);
                            map.put("IF_ID", uuid);
                            values = line.split("\t");
                            for(int i = 0; i < keys.length; i++) {
                                map.put(keys[i], values[i]);
                            }
                        }
                    }
                    log.debug(fileName);
                    log.debug(String.valueOf(list.size()));
                    System.out.println(fileName);
                    System.out.println(list.size());
                    if("IF_ESZCTHD".equals(tableName)) {
                        int skip = 0;
                        int limit = 100;
                        while (skip < list.size()) {
                            perRequests = list
                                    .stream()
                                    .skip(skip).limit(limit).collect(Collectors.toList());

                            skip += limit;
                            writeMapper.insert_ESZCTHD_IF(perRequests);
                        }
                         /*  for (Map<String, Object> map1: list) {
                            writeMapper.insert_ESZCTHD_IF(map1);
                        }*/

                    } else if("IF_ESZCTDT".equals(tableName)) {
                        int skip = 0;
                        int limit = 100;
                        while (skip < list.size()) {
                            perRequests = list
                                    .stream()
                                    .skip(skip).limit(limit).collect(Collectors.toList());

                            skip += limit;
                            writeMapper.insert_ESZCTDT_IF(perRequests);
                        }
                        /*for (Map<String, Object> map1: list) {
                            writeMapper.insert_ESZCTDT_IF(map1);
                        }*/
                    }

                } catch (Exception e) {
                   // e.printStackTrace();
                    System.out.println(perRequests);
                    throw new RuntimeException(e);

                }
            }
            return RepeatStatus.FINISHED;
        };
    }
    public Tasklet oracleToMssql() {
        return (contribution, chunkContext) -> {
            log.info("taskletStep process..");
            // log.info("taskletStep date : " + jobParameter.getStartDate());
            // log.info("taskletStep name : " + jobParameter.getJobName());

            Map<String, Integer> map = new HashMap<>();
            int start = 0;
            int end = 900;

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
        };
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

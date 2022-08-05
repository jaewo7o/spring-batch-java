package com.jaewoo.batch.app.basic;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@AllArgsConstructor
@Configuration
public class TaskletJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job taskletJob_batchBuilder() {
        return jobBuilderFactory.get("taskletJob")
                .start(taskletJob_step01())
                .next(taskletJob_step02(null))
                .build();
    }

    @Bean
    public Step taskletJob_step01() {
        return stepBuilderFactory.get("taskletJob_step01")
                .tasklet((x, y) -> {
                    log.info("tasklet log Job -> Step");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    @JobScope
    public Step taskletJob_step02(@Value("#{jobParameters[date]}") String date) {
        return stepBuilderFactory.get("taskletJob_step02")
                .tasklet((x, y) -> {
                    log.info("tasklet log Step01 -> Step02, date : " + date);
                    return RepeatStatus.FINISHED;
                }).build();
    }
}

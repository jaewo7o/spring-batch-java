package com.jaewoo.batch.app.basic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class TaskletJob {

    private final static String JOB_NAME = "taskletJob";
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean(JOB_NAME)
    public Job taskletJobBuilder() {
        return jobBuilderFactory.get(JOB_NAME)
                .incrementer(new RunIdIncrementer())
                .start(taskletJobStep01())
                .build();
    }

    @Bean(JOB_NAME + "Step01")
    public Step taskletJobStep01() {
        return stepBuilderFactory.get(JOB_NAME + "Step01")
                .tasklet((stepContribution, context) -> {
                    log.info("tasklet log Job -> Step");
                    return RepeatStatus.FINISHED;
                }).build();
    }
}

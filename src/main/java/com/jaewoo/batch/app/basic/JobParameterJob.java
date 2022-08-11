package com.jaewoo.batch.app.basic;

import com.jaewoo.batch.core.util.DataShare;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class JobParameterJob {
    private static final String BATCH_NAME = "JobParameterJob";

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    @Bean(BATCH_NAME)
    public Job jobParameterJobBuilder() {
        return jobBuilderFactory.get(BATCH_NAME)
                .incrementer(new RunIdIncrementer())
                .validator(validator())
                .start(jobParameterJobStep01(null, null, null))
                .build();
    }

    @Bean
    public JobParametersValidator validator() {
        DefaultJobParametersValidator validator = new DefaultJobParametersValidator();

        validator.setRequiredKeys(new String[]{"name"});
        //validator.setOptionalKeys(new String[]{"orderDate", "amount"});

        return validator;
    }

    @JobScope
    @Bean(BATCH_NAME + "Step01")
    public Step jobParameterJobStep01(@Value("#{jobParameters[orderDate]}") Date orderDate,
                                      @Value("#{jobParameters[amount]}") Double amount,
                                      @Value("#{jobParameters[name]}") String name) {
        log.info("orderDate : {}, amount : {}, name : {} ", orderDate, amount, name);
        log.info("jobName : {}", DataShare.getParameter("jobName"));
        return stepBuilderFactory.get(BATCH_NAME + "Step01")
                .tasklet((contribution, context) -> {
                    log.info("name : {}", name);
                    return RepeatStatus.FINISHED;
                }).build();
    }
}

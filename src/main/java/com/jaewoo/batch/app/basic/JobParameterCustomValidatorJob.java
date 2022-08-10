package com.jaewoo.batch.app.basic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class JobParameterCustomValidatorJob {

    private static final String BATCH_NAME = "JobParameterCustomValidatorJob";

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    @Bean(BATCH_NAME)
    public Job jobParameterJobBuilder() {
        return jobBuilderFactory.get(BATCH_NAME)
                .incrementer(new RunIdIncrementer())
                .validator(new CustomJobParametersValidator())
                .start(jobParameterJobStep01(null))
                .build();
    }

    @JobScope
    @Bean(BATCH_NAME + "Step01")
    public Step jobParameterJobStep01(@Value("#{jobParameters[fileName]}") String fileName) {
        log.info("fileName : {} ", fileName);
        return stepBuilderFactory.get(BATCH_NAME + "Step01")
                .tasklet((contribution, context) -> {
                    log.info("fileName : {}", fileName);
                    return RepeatStatus.FINISHED;
                }).build();
    }

    private class CustomJobParametersValidator implements JobParametersValidator {

        @Override
        public void validate(JobParameters jobParameters) throws JobParametersInvalidException {
            String fileName = jobParameters.getString("fileName");

            if (!StringUtils.hasText(fileName)) {
                throw new JobParametersInvalidException("fileName parameter is missing.");
            }
            else if (!StringUtils.endsWithIgnoreCase(fileName, "csv")) {
                throw new JobParametersInvalidException("fileName parameter does not use the csv file extension.");
            }
        }
    }

}

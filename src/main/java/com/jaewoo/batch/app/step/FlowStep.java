package com.jaewoo.batch.app.step;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class FlowStep {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job flowStepJob() {

        return jobBuilderFactory.get("flowStepJob")
                .start(stepA())
                .on("*")
                .to(stepB())

                .from(stepA())
                .on("FAILED")
                .to(stepC())
                .end()
                .build();
    }

    @Bean
    public Step stepA() {
        return stepBuilderFactory.get("stepA")
                .tasklet((contribution, chunkContext) -> {
                    log.info("Start Step A!");

                    //String result = "COMPLETED";
                    String result = "FAIL";

                    //Flow에서 on은 RepeatStatus가 아닌 ExitStatus를 바라본다.
                    if (result.equals("COMPLETED")) {
                        contribution.setExitStatus(ExitStatus.COMPLETED);
                    } else if (result.equals("FAIL")) {
                        contribution.setExitStatus(ExitStatus.FAILED);
                    }

                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Step stepB() {
        return stepBuilderFactory.get("stepB")
                .tasklet((contribution, chunkContext) -> {
                    log.info("StepB Execute!!");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Step stepC() {
        return stepBuilderFactory.get("stepC")
                .tasklet((contribution, chunkContext) -> {
                    log.info("StepC Execute!!");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }
}

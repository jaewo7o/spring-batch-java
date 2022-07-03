package com.jaewoo.batch.step;

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
                .start(flowStepStartStep())
                .on("FAILED") //startStep의 ExitStatus가 FAILED일 경우
                .to(flowStepFailOverStep()) //failOver Step을 실행 시킨다.
                .on("*") //failOver Step의 결과와 상관없이
                .to(flowStepWriteStep()) //write Step을 실행 시킨다.
                .on("*") //write Step의 결과와 상관없 이
                .end() //Flow를 종료시킨다.

                .from(flowStepStartStep()) //startStep이 FAILED가 아니고
                .on("COMPLETED") //COMPLETED일 경우
                .to(flowStepProcessStep()) //process Step을 실행 시킨다
                .on("*") //process Step의 결과와 상관없이
                .to(flowStepWriteStep()) // write Step을 실행 시킨다.
                .on("*") //wrtie Step의 결과와 상관없이
                .end() //Flow를 종료 시킨다.

                .from(flowStepStartStep()) //startStep의 결과가 FAILED, COMPLETED가 아닌
                .on("*") //모든 경우
                .to(flowStepWriteStep()) //write Step을 실행시킨다.
                .on("*") //write Step의 결과와 상관없이
                .end() //Flow를 종료시킨다.
                .end()
                .build();
    }

    @Bean
    public Step flowStepStartStep() {
        return stepBuilderFactory.get("flowStepStartStep")
                .tasklet((contribution, chunkContext) -> {
                    log.info("Start Step!");

                    String result = "COMPLETED";
                    //String result = "FAIL";
                    //String result = "UNKNOWN";

                    //Flow에서 on은 RepeatStatus가 아닌 ExitStatus를 바라본다.
                    if (result.equals("COMPLETED")) {
                        contribution.setExitStatus(ExitStatus.COMPLETED);
                    } else if (result.equals("FAIL")) {
                        contribution.setExitStatus(ExitStatus.FAILED);
                    } else if (result.equals("UNKNOWN")) {
                        contribution.setExitStatus(ExitStatus.UNKNOWN);
                    }

                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Step flowStepFailOverStep() {
        return stepBuilderFactory.get("flowStepFailOverStep")
                .tasklet((contribution, chunkContext) -> {
                    log.info("FailOver Step!");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Step flowStepProcessStep() {
        return stepBuilderFactory.get("flowStepProcessStep")
                .tasklet((contribution, chunkContext) -> {
                    log.info("Process Step!");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }


    @Bean
    public Step flowStepWriteStep() {
        return stepBuilderFactory.get("flowStepWriteStep")
                .tasklet((contribution, chunkContext) -> {
                    log.info("Write Step!");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }
}

package com.jaewoo.batch.app.basic;

import com.jaewoo.batch.core.test.BatchTestConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBatchTest
@SpringBootTest(classes = { HelloWorldJob.class, BatchTestConfig.class }) // (2)
class HelloWorldJobTest {

    @Autowired
    protected JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    @DisplayName("Hello World Test")
    void helloWorldJob_batchBuilder() throws Exception {
        // given
        JobParameters jobParameters = jobLauncherTestUtils.getUniqueJobParameters();
        jobParameters.getParameters().put("date", new JobParameter("20220211"));

        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        // then
        Assertions.assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
    }

    @Test
    @DisplayName("Hello World Test - Tasklet Step")
    void helloWorldJob_step() {
        // given
        String stepName = "helloWorldJob_step01";

        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchStep(stepName);

        // then
        Assertions.assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
    }
}
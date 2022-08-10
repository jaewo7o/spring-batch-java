package com.jaewoo.batch.app.basic;

import com.jaewoo.batch.core.test.BatchTestConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBatchTest
@SpringBootTest(classes = { JobParameterCustomValidatorJob.class, BatchTestConfig.class })
class JobParameterCustomValidatorJobTest {

    @Autowired
    protected JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    @DisplayName("Custom Validator Test - Success")
    void jobParameterJobBuilder() throws Exception {
        // given
        JobParameters params = new JobParametersBuilder()
                .addString("fileName", "uploadFile.csv")
                .addString("id", "1")
                .toJobParameters();

        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(params);

        // then
        Assertions.assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
    }

    @Test
    @DisplayName("Custom Validator Test - Fail")
    void jobParameterJobBuilderFail() throws Exception {
        // given
        JobParameters params = new JobParametersBuilder()
                .addString("fileName", "uploadFile.txt")
                .addString("id", "2")
                .toJobParameters();

        // when & then
        Assertions.assertThatThrownBy(() -> jobLauncherTestUtils.launchJob(params))
                .isInstanceOf(JobParametersInvalidException.class);
    }
}
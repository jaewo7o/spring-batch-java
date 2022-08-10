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

import java.util.Date;

@SpringBatchTest
@SpringBootTest(classes = { JobParameterJob.class, BatchTestConfig.class }) // (2)
class JobParameterJobTest {

    @Autowired
    protected JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    @DisplayName("JOB PARAMETER TEST")
    void jobParameterJobBuilder() throws Exception {
        // given
        JobParameters params = new JobParametersBuilder()
                .addDate("orderDate", new Date())
                .addString("name", "TEST NAME")
                .addDouble("amount", 1234567.12345)
                .toJobParameters();

        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(params);

        // then
        Assertions.assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
    }

    @Test
    @DisplayName("JOB PARAMETER TEST - Parameter Validation Error")
    void jobParameterJobBuilderJobParameterValidation() throws Exception {
        // given
        JobParameters params = new JobParametersBuilder()
                .addDate("orderDate", new Date())
                .addDouble("amount", 1234567.12345)
                .toJobParameters();

        // when & then
        Assertions.assertThatThrownBy(() -> jobLauncherTestUtils.launchJob(params))
                .isInstanceOf(JobParametersInvalidException.class);
    }
}
package com.jaewoo.batch.app.basic;

import com.jaewoo.batch.core.test.BatchTestConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBatchTest
@SpringBootTest(classes = {CsvJob2.class, BatchTestConfig.class})
class CsvJob2Test {

    @Autowired
    protected JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    @DisplayName("CSV TO CSV 파일 테스트")
    void csvJob2Build() throws Exception {
        // given
        JobParameters uniqueJobParameters = jobLauncherTestUtils.getUniqueJobParameters();

        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(uniqueJobParameters);

        // then
        Assertions.assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
    }
}
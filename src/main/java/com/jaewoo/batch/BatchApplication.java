package com.jaewoo.batch;

import com.jaewoo.batch.core.util.DataShare;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableBatchProcessing
@SpringBootApplication
public class BatchApplication {

    public static void main(String[] args) {
        if (args.length < 1) {
            throw new IllegalArgumentException("Batch requires job name in the first argument");
        }

        String firstArgument = args[0];
        if (!firstArgument.startsWith("--job.name")) {
            throw new IllegalArgumentException("Batch requires job name in the first argument");
        }

        DataShare.addParameter("jobName", firstArgument.substring(firstArgument.indexOf("=") + 1));

        System.out.println(DataShare.getParameter("jobName"));

        SpringApplication.run(BatchApplication.class, args);
    }
}

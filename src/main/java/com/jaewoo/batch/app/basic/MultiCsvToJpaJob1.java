package com.jaewoo.batch.app.basic;

import com.jaewoo.batch.app.basic.domain.Dept;
import com.jaewoo.batch.app.basic.dto.TwoToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;

import javax.persistence.EntityManagerFactory;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class MultiCsvToJpaJob1 {
    private static final int CHUNK_SIZE = 5;

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    private final EntityManagerFactory entityManagerFactory;

    private final ResourceLoader resourceLoader;

    @Bean
    public Job multiCsvToJpaJob1_buildBatch() {
        return jobBuilderFactory.get("multiCsvToJpaJob1")
                .start(multiCsvToJpaJob1_step01())
                .build();
    }

    @Bean
    public Step multiCsvToJpaJob1_step01() {
        return stepBuilderFactory.get("multiCsvToJpaJob1_step01")
                .<TwoToken, Dept>chunk(CHUNK_SIZE)
                .reader(multiCsvToJpaJob1_readCsvMain())
                .processor(multiCsvToJpaJob1_process())
                .writer(multiCsvToJpaJob1_writeJpa())
                .faultTolerant()
                .skip(FlatFileParseException.class)
                .skipLimit(10)
                .build();
    }

    private JpaItemWriter<Dept> multiCsvToJpaJob1_writeJpa() {
        return new JpaItemWriterBuilder<Dept>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

    private ItemProcessor<TwoToken, Dept> multiCsvToJpaJob1_process() {
        return item -> new Dept(Integer.parseInt(item.getOne()), item.getTwo(), "ETC");
    }

    private MultiResourceItemReader<TwoToken> multiCsvToJpaJob1_readCsvMain() {
        MultiResourceItemReader<TwoToken> itemReader = new MultiResourceItemReader<>();

        try {
            itemReader.setResources(
                    ResourcePatternUtils.getResourcePatternResolver(resourceLoader)
                            .getResources("classpath:sample/multiCsvToJpaJob1/*.csv")
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        itemReader.setDelegate(multiCsvToJpaJob1_readCsv());

        return itemReader;
    }

    private FlatFileItemReader<TwoToken> multiCsvToJpaJob1_readCsv() {
        FlatFileItemReader<TwoToken> itemReader = new FlatFileItemReader<>();
        itemReader.setLineMapper((line, lineNumber) -> {
            String[] tokens = line.split(":");
            return new TwoToken(tokens[0], tokens[1]);
        });

        return itemReader;
    }
}

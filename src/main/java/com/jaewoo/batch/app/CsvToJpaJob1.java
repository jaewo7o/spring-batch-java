package com.jaewoo.batch.app;

import com.jaewoo.batch.app.domain.Dept;
import com.jaewoo.batch.app.dto.TwoToken;
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
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.persistence.EntityManagerFactory;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class CsvToJpaJob1 {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    private final static int CHUNK_SIZE = 5;

    @Bean
    public Job csvToJpaJob1_buildBatch() {
        return jobBuilderFactory.get("csvToJpaJob1")
                .start(csvToJpaJob1_step01())
                .build();
    }

    @Bean
    public Step csvToJpaJob1_step01() {
        return stepBuilderFactory.get("csvToJpaJob1_step01")
                .<TwoToken, Dept>chunk(CHUNK_SIZE)
                .reader(csvToJpaJob1_readCsv())
                .processor(csvToJpaJob1_process())
                .writer(csvToJpaJob1_writeJpa())
                .build();
    }

    private JpaItemWriter<Dept> csvToJpaJob1_writeJpa() {
        return new JpaItemWriterBuilder<Dept>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

    private ItemProcessor<TwoToken, Dept> csvToJpaJob1_process() {
        return item -> new Dept(Integer.parseInt(item.getOne()), item.getTwo(), "");
    }

    private FlatFileItemReader<TwoToken> csvToJpaJob1_readCsv() {
        FlatFileItemReader<TwoToken> itemReader = new FlatFileItemReader<>();
        itemReader.setLinesToSkip(1);
        itemReader.setResource(new ClassPathResource("sample/csvToJpaJob1_input.csv"));

        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setDelimiter(":");
        tokenizer.setNames("one", "two");

        BeanWrapperFieldSetMapper<TwoToken> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(TwoToken.class);

        DefaultLineMapper<TwoToken> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        itemReader.setLineMapper(lineMapper);

        return itemReader;
    }
}

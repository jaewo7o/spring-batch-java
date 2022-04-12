package com.jaewoo.batch.app;

import com.jaewoo.batch.app.dto.TwoToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.FixedLengthTokenizer;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class FixedLengthJob1 {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    private static final int CHUNK_SIZE = 5;

    @Bean
    public Job fixedLengthJob1_buildBatch() {
        return jobBuilderFactory.get("fixedLengthJob1")
                .start(fixedLengthJob1_step01())
                .build();
    }

    @Bean
    public Step fixedLengthJob1_step01() {
        return stepBuilderFactory.get("fixedLengthJob1_step01")
                .<TwoToken, TwoToken>chunk(CHUNK_SIZE)
                .reader(fixedLengthJob1_fileReader())
                .writer(items -> items.forEach(System.out::println))
                .build();
    }

    @Bean
    public FlatFileItemReader<TwoToken> fixedLengthJob1_fileReader() {
        FlatFileItemReader<TwoToken> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(new ClassPathResource("sample/fixedLengthJob1_input.txt"));
        itemReader.setLinesToSkip(1);

        FixedLengthTokenizer lineTokenizer = new FixedLengthTokenizer();
        lineTokenizer.setNames("one", "two");
        lineTokenizer.setColumns(new Range(1, 5), new Range(6, 10));

        BeanWrapperFieldSetMapper<TwoToken> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(TwoToken.class);

        DefaultLineMapper<TwoToken> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        itemReader.setLineMapper(lineMapper);
        return itemReader;
    }
}

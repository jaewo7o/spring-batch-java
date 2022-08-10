package com.jaewoo.batch.app.basic;

import com.jaewoo.batch.app.basic.dto.OneLine;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Slf4j
@AllArgsConstructor
@Configuration
public class TextJob1 {
    private static final int CHUNK_SIZE = 5;

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job textJob1_buildBatch() {
        return jobBuilderFactory.get("textJob1")
                .start(textJob1_step01())
                .build();
    }

    @Bean
    public Step textJob1_step01() {
        return stepBuilderFactory.get("textJob1_step01")
                .chunk(CHUNK_SIZE)
                .reader(textJob1_textReader())
                .writer(item -> item.forEach(System.out::println))
                .build();
    }

    @Bean
    public FlatFileItemReader<OneLine> textJob1_textReader() {
        FlatFileItemReader<OneLine> itemReader = new FlatFileItemReader<>();
        itemReader.setName("textJob1_textReader");
        itemReader.setResource(new ClassPathResource("sample/textJob1_input.txt"));
        itemReader.setLineMapper((line, lineNumber) -> new OneLine(lineNumber + "_" + line));
        return itemReader;
    }
}

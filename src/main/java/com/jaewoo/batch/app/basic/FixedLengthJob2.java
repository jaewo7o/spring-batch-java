package com.jaewoo.batch.app.basic;

import com.jaewoo.batch.app.basic.dto.TwoToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.FixedLengthTokenizer;
import org.springframework.batch.item.file.transform.FormatterLineAggregator;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class FixedLengthJob2 {
    private static final int CHUNK_SIZE = 5;

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job fixedLengthJob2_buildBatch() {
        return jobBuilderFactory.get("fixedLengthJob2")
                .start(fixedLengthJob2_step01())
                .build();
    }

    @Bean
    public Step fixedLengthJob2_step01() {
        return stepBuilderFactory.get("fixedLengthJob2_step01")
                .<TwoToken, TwoToken>chunk(CHUNK_SIZE)
                .reader(fixedLengthJob2_fileReader())
                .writer(fixedLengthJob2_fileWriter())
                .build();
    }

    private FlatFileItemWriter<TwoToken> fixedLengthJob2_fileWriter() {
        Resource resource = new FileSystemResource("output/fixedLengthJob2_output.txt");

        BeanWrapperFieldExtractor<TwoToken> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[] { "one", "two" });
        fieldExtractor.afterPropertiesSet();

        FormatterLineAggregator<TwoToken> lineAggregator = new FormatterLineAggregator<>();
        lineAggregator.setFormat("%-5s###%5s");
        lineAggregator.setFieldExtractor(fieldExtractor);

        return new FlatFileItemWriterBuilder<TwoToken>()
                .name("fixedLengthJob2_fileWriter")
                .resource(resource)
                .lineAggregator(lineAggregator)
                .build();
    }

    @Bean
    public FlatFileItemReader<TwoToken> fixedLengthJob2_fileReader() {
        FlatFileItemReader<TwoToken> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(new ClassPathResource("sample/fixedLengthJob2_input.txt"));
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

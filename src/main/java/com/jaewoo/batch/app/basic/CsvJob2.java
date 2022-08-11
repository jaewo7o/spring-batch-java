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
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

@RequiredArgsConstructor
@Slf4j
@Configuration
public class CsvJob2 {
    private static final String BATCH_JOB = "csvJob2";

    private static final int CHUNK_SIZE = 5;

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    @Bean(BATCH_JOB)
    public Job csvJob2Build() {
        return jobBuilderFactory.get(BATCH_JOB)
                .start(csvJob2Step01())
                .build();
    }

    @Bean(BATCH_JOB + "Step01")
    public Step csvJob2Step01() {
        return stepBuilderFactory.get(BATCH_JOB + "Step01")
                .<TwoToken, TwoToken>chunk(CHUNK_SIZE)
                .reader(csvJob2FileReader())
                .writer(csvJob2FileWriter())
                .build();
    }

    @Bean
    public FlatFileItemReader<TwoToken> csvJob2FileReader() {
        FlatFileItemReader<TwoToken> itemReader = new FlatFileItemReader<>();
        itemReader.setName("csvJob2_fileReader");
        itemReader.setLinesToSkip(1);
        itemReader.setResource(new ClassPathResource("sample/csvJob2_input.csv"));

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setNames("one", "two");
        lineTokenizer.setDelimiter(":");

        BeanWrapperFieldSetMapper<TwoToken> fieldMapper = new BeanWrapperFieldSetMapper<>();
        fieldMapper.setTargetType(TwoToken.class);

        DefaultLineMapper<TwoToken> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldMapper);

        itemReader.setLineMapper(lineMapper);

        return itemReader;
    }

    @Bean
    public FlatFileItemWriter<TwoToken> csvJob2FileWriter() {

        BeanWrapperFieldExtractor<TwoToken> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[]{"one", "two"});
        fieldExtractor.afterPropertiesSet();

        DelimitedLineAggregator<TwoToken> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter("@");
        lineAggregator.setFieldExtractor(fieldExtractor);

        Resource resource = new FileSystemResource("output/csvJob2_output.csv");

        return new FlatFileItemWriterBuilder<TwoToken>()
                .name("csvJob2_fileWriter")
                .resource(resource)
                .lineAggregator(lineAggregator)
                .build();
    }
}

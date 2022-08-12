package com.jaewoo.batch.app.basic;

import com.jaewoo.batch.app.basic.dto.TwoToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.batch.item.support.builder.CompositeItemProcessorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

@RequiredArgsConstructor
@Slf4j
@Configuration
public class CsvProcessorJob3 {
    private static final String BATCH_JOB = "csvProcessorJob3Job";

    private static final int CHUNK_SIZE = 5;

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    @Bean(BATCH_JOB)
    public Job job() {
        return jobBuilderFactory.get(BATCH_JOB)
                .incrementer(new RunIdIncrementer())
                .start(step01())
                .build();
    }

    @Bean(BATCH_JOB + "Step01")
    public Step step01() {
        return stepBuilderFactory.get(BATCH_JOB + "Step01")
                .<TwoToken, TwoToken>chunk(CHUNK_SIZE)
                .reader(reader())
                .processor(compositeProcessor())
                .writer(writer())
                .build();
    }

    @Bean
    public FlatFileItemReader<TwoToken> reader() {
        FlatFileItemReader<TwoToken> itemReader = new FlatFileItemReader<>();
        itemReader.setName(BATCH_JOB + "Reader");
        itemReader.setLinesToSkip(1);
        itemReader.setResource(new ClassPathResource("sample/csvProcessorJob3_input.csv"));

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
    public CompositeItemProcessor compositeProcessor() {
        return new CompositeItemProcessorBuilder<>()
                .delegates(processor1(), processor2())
                .build();
    }

    @Bean
    public ItemProcessor<TwoToken, TwoToken> processor1() {
        return item -> {
            boolean isIgnore = Integer.parseInt(item.getOne()) % 2 == 0;
            if (isIgnore) {
                log.info("This item is ignored : {}, {}", item.getOne(), item.getTwo());
                return null;
            }

            return item;
        };
    }

    @Bean
    public ItemProcessor<TwoToken, TwoToken> processor2() {
        return item -> {
            String newOne = item.getOne() + "_ADD1";
            String newTwo = item.getTwo() + "_ADD2";

            item.setOne(newOne);
            item.setTwo(newTwo);

            return item;
        };
    }


    @Bean
    public FlatFileItemWriter<TwoToken> writer() {
        FlatFileItemWriter itemWriter = new FlatFileItemWriter();
        itemWriter.setName(BATCH_JOB + "Writer");
        itemWriter.setResource(new FileSystemResource("output/csvProcessorJob3_output.csv"));

        BeanWrapperFieldExtractor<TwoToken> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[] {"one", "two"});
        fieldExtractor.afterPropertiesSet();

        DelimitedLineAggregator<TwoToken> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter(",");
        lineAggregator.setFieldExtractor(fieldExtractor);

        itemWriter.setLineAggregator(lineAggregator);
        itemWriter.setAppendAllowed(false);
        itemWriter.setHeaderCallback(writer -> writer.write("첫번째,두번째"));

        return itemWriter;
    }
}

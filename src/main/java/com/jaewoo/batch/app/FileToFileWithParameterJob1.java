package com.jaewoo.batch.app;

import com.jaewoo.batch.app.dto.TwoToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.separator.SimpleRecordSeparatorPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class FileToFileWithParameterJob1 {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    private static final int CHUNK_SIZE = 5;

    @Bean
    public Job fileToFileWithParameterJob1_buildBatch() {
        return jobBuilderFactory.get("fileToFileWithParameterJob1")
                .start(fileToFileWithParameterJob1_step01(null))
                .build();
    }

    @Bean
    @JobScope
    public Step fileToFileWithParameterJob1_step01(@Value("#{jobParameters[version]}") String version) {
        log.info("==========================");
        log.info(version);
        log.info("==========================");

        return stepBuilderFactory.get("fileToFileWithParameterJob1_step01")
                .<TwoToken, TwoToken>chunk(CHUNK_SIZE)
                .reader(fileToFileWithParameterJob1_fileRead(null))
                .processor(fileToFileWithParameterJob1_processor(null))
                .writer(fileToFileWithParameterJob1_writer(null))
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemWriter<TwoToken> fileToFileWithParameterJob1_writer(@Value("#{jobParameters[outFileName]}") String outFileName) {
        return new FlatFileItemWriterBuilder<TwoToken>()
                .name("fileToFileWithParameterJob1_writer")
                .resource(new FileSystemResource("output/" + outFileName))
                .lineAggregator(item -> item.getOne() + "=====" + item.getTwo())
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<TwoToken, TwoToken> fileToFileWithParameterJob1_processor(@Value("#{jobParameters[version]}") String version) {
        log.info("process version : " + version);
        return twoToken -> new TwoToken(twoToken.getTwo(), twoToken.getOne());
    }

    @Bean
    @StepScope
    public FlatFileItemReader<TwoToken> fileToFileWithParameterJob1_fileRead(@Value("#{jobParameters[inFileName]}") String inFileName) {
        return new FlatFileItemReaderBuilder<TwoToken>()
                .name("fileToFileWithParameterJob1_fileRead")
                .resource(new ClassPathResource("sample/" + inFileName))
                .delimited().delimiter(":")
                .names("one", "two")
                .targetType(TwoToken.class)
                .recordSeparatorPolicy(new SimpleRecordSeparatorPolicy() {
                    @Override
                    public String postProcess(String record) {
                        log.info("policy : " + record);

                        // 파서 대상이 아니면 무시
                        if (!record.contains(":")) {
                            return null;
                        }

                        return record.trim();
                    }
                }).build();
    }
}

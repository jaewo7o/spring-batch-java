package com.jaewoo.batch.app.basic;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.jaewoo.batch.app.basic.dto.TwoToken;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Configuration
public class CsvJob1 {
	private static final String BATCH_JOB = "csvJob1";

	private static final int CHUNK_SIZE = 5;

	private final JobBuilderFactory jobBuilderFactory;

	private final StepBuilderFactory stepBuilderFactory;

	@Bean(BATCH_JOB)
	public Job csvJob1_batchBuild() {
		return jobBuilderFactory.get(BATCH_JOB)
			.start(csvJob1Step01())
			.build();
	}

	@Bean(BATCH_JOB + "Step01")
	public Step csvJob1Step01() {
		return stepBuilderFactory.get(BATCH_JOB + "Step01")
			.chunk(CHUNK_SIZE)
			.reader(csvJob1FileReader())
			.writer(items -> items.forEach(System.out::println))
			.build();
	}

	@Bean
	public FlatFileItemReader<TwoToken> csvJob1FileReader() {
		FlatFileItemReader<TwoToken> itemReader = new FlatFileItemReader<>();
		itemReader.setName("csvJob1_fileReader");
		itemReader.setResource(new ClassPathResource("sample/csvJob1_input.csv"));
		itemReader.setLinesToSkip(1);

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
}

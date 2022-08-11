package com.jaewoo.batch.app.basic;

import com.jaewoo.batch.app.basic.domain.CoinMarket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class JsonJob2 {
    private static final int CHUNK_SIZE = 5;

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job jsonJob2_batchBuild() {
        return jobBuilderFactory.get("jsonJob2")
                .start(jsonJob2_step01())
                .build();
    }

    public Step jsonJob2_step01() {
        return stepBuilderFactory.get("jsonJob2_step01")
                .<CoinMarket, CoinMarket>chunk(CHUNK_SIZE)
                .reader(jsonJob2_jsonReader())
                .processor(jsonJob2_process())
                .writer(jsonJob2_jsonWriter())
                .build();
    }

    @Bean
    public ItemProcessor<CoinMarket, CoinMarket> jsonJob2_process() {
        return item -> {
            if (item.getMarket().startsWith("KRW-")) {
                return item;
            } else {
                return null;
            }
        };
    }

    @Bean
    public JsonFileItemWriter<CoinMarket> jsonJob2_jsonWriter() {
        return new JsonFileItemWriterBuilder<CoinMarket>()
                .name("jsonJob2_jsonWriter")
                .resource(new FileSystemResource("output/jsonJob2_output.json"))
                .jsonObjectMarshaller(new JacksonJsonObjectMarshaller<>())
                .build();
    }

    @Bean
    public JsonItemReader<CoinMarket> jsonJob2_jsonReader() {
        return new JsonItemReaderBuilder<CoinMarket>()
                .name("jsonJob2_jsonReader")
                .jsonObjectReader(new JacksonJsonObjectReader<>(CoinMarket.class))
                .resource(new ClassPathResource("sample/jsonJob2_input.json"))
                .build();
    }
}

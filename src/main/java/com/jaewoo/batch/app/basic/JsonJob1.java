package com.jaewoo.batch.app.basic;

import com.jaewoo.batch.app.basic.domain.CoinMarket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class JsonJob1 {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final static int CHUNK_SIZE = 5;

    @Bean
    public Job jsonJob1_batchBuild() {
        return jobBuilderFactory.get("jsonJob1")
                .start(jsonJob1_step01())
                .build();
    }

    public Step jsonJob1_step01() {
        return stepBuilderFactory.get("jsonJob1_step01")
                .<CoinMarket, CoinMarket>chunk(CHUNK_SIZE)
                .reader(jsonJob1_jsonReader())
                .writer(items -> items.forEach(System.out::println))
                .build();
    }

    private JsonItemReader<CoinMarket> jsonJob1_jsonReader() {
        return new JsonItemReaderBuilder<CoinMarket>()
                .name("jsonJob1_jsonReader")
                .jsonObjectReader(new JacksonJsonObjectReader<>(CoinMarket.class))
                .resource(new ClassPathResource("sample/jsonJob1_input.json"))
                .build();
    }
}

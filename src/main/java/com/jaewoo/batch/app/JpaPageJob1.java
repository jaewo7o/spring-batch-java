package com.jaewoo.batch.app;

import com.jaewoo.batch.app.domain.Dept;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;

@AllArgsConstructor
@Slf4j
@Configuration
public class JpaPageJob1 {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    private final static int CHUNK_SIZE = 10;

    @Bean
    public Job jpaPageJob1_buildBatch() {
        return jobBuilderFactory.get("jpaPageJob1")
                .start(jpaPageJob1_step01())
                .build();
    }

    @Bean
    public Step jpaPageJob1_step01() {
        return stepBuilderFactory.get("jpaPageJob1_step01")
                .<Dept, Dept>chunk(CHUNK_SIZE)
                .reader(jpaPageJob1_dbItemReader())
                .writer(jpaPageJob1_printItemWriter())
                .build();
    }

    @Bean
    public JpaPagingItemReader<Dept> jpaPageJob1_dbItemReader() {
        return new JpaPagingItemReaderBuilder<Dept>()
                .name("jpaPageJob1_dbItemReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(CHUNK_SIZE)
                .queryString("SELECT d FROM Dept d ORDER BY d.deptId ASC")
                .build();
    }

    @Bean
    public ItemWriter<Dept> jpaPageJob1_printItemWriter() {
        return list -> {
            for (Dept dept : list) {
                log.info(dept.toString());
            }
        };
    }
}

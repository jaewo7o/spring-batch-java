package com.jaewoo.batch.app;

import com.jaewoo.batch.app.domain.Dept;
import com.jaewoo.batch.app.domain.Dept2;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;

@AllArgsConstructor
@Slf4j
@Configuration
public class JpaPageJob2 {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    private final static int CHUNK_SIZE = 10;

    @Bean
    public Job jpaPageJob2_buildBatch() {
        return jobBuilderFactory.get("jpaPageJob2")
                .start(jpaPageJob2_step01())
                .build();
    }

    @Bean
    public Step jpaPageJob2_step01() {
        return stepBuilderFactory.get("jpaPageJob2_step01")
                .<Dept, Dept2>chunk(CHUNK_SIZE)
                .reader(jpaPageJob2_dbItemReader())
                .processor(jpaPageJob2_processor())
                .writer(jpaPageJob2_dbItemWriter())
                .build();
    }

    private ItemProcessor<Dept, Dept2> jpaPageJob2_processor() {
        return dept -> new Dept2(dept.getDeptId(), "NEW_" + dept.getDeptName(), "NEW_" + dept.getDeptLocation());
    }

    @Bean
    public JpaPagingItemReader<Dept> jpaPageJob2_dbItemReader() {
        return new JpaPagingItemReaderBuilder<Dept>()
                .name("jpaPageJob2_dbItemReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(CHUNK_SIZE)
                .queryString("SELECT d FROM Dept d ORDER BY d.deptId ASC")
                .build();
    }

    @Bean
    public JpaItemWriter<Dept2> jpaPageJob2_dbItemWriter() {
        return new JpaItemWriterBuilder<Dept2>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }
}

package com.jaewoo.batch.app.basic;

import com.jaewoo.batch.app.basic.domain.Dept;
import com.jaewoo.batch.app.basic.domain.Dept2;
import javax.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JpaPageParallelJob1 {
    private static final int CHUNK_SIZE = 10;

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    private final EntityManagerFactory entityManagerFactory;

    @Bean
    public Job jpaPageParallelJob1_batchBuild() {
        Flow flow1 = new FlowBuilder<Flow>("flow1")
                .start(jpaPageParallelJob1_step1())
                .build();

        Flow flow2 = new FlowBuilder<Flow>("flow2")
                .start(jpaPageParallelJob1_step2())
                .build();

        Flow flowParallel = new FlowBuilder<Flow>("parallelFlow")
                .split(new SimpleAsyncTaskExecutor())
                .add(flow1, flow2)
                .build();

        return jobBuilderFactory.get("jpaPageParallelJob1")
                .start(flowParallel)
                .build()
                .build();
    }

    @Bean
    public Step jpaPageParallelJob1_step1() {
        return stepBuilderFactory.get("jpaPageParallelJob1_step1")
                .<Dept, Dept2>chunk(CHUNK_SIZE)
                .reader(jpaPageParallelJob1_step1_reader())
                .processor(jpaPageParallelJob1_step1_processor())
                .writer(jpaPageParallelJob1_step1_writer())
                .build();
    }

    @Bean
    public JpaPagingItemReader<Dept> jpaPageParallelJob1_step1_reader() {
        return new JpaPagingItemReaderBuilder<Dept>()
                .name("jpaPageParallelJob1_step1_reader")
                .pageSize(CHUNK_SIZE)
                .queryString("SELECT D FROM Dept D WHERE dept_id < 50")
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

    private ItemProcessor<Dept, Dept2> jpaPageParallelJob1_step1_processor() {
        return dept -> new Dept2(dept.getDeptId(), "NEW_" + dept.getDeptName(),
                "NEW_" + dept.getDeptLocation());
    }

    @Bean
    public JpaItemWriter<Dept2> jpaPageParallelJob1_step1_writer() {
        JpaItemWriter<Dept2> itemWriter = new JpaItemWriter<>();
        itemWriter.setEntityManagerFactory(entityManagerFactory);
        return itemWriter;
    }

    @Bean
    public Step jpaPageParallelJob1_step2() {
        return stepBuilderFactory.get("jpaPageParallelJob1_step2")
                .<Dept, Dept2>chunk(CHUNK_SIZE)
                .reader(jpaPageParallelJob1_step2_reader())
                .processor(jpaPageParallelJob1_step2_processor())
                .writer(jpaPageParallelJob1_step2_writer())
                .build();
    }

    @Bean
    public JpaPagingItemReader<Dept> jpaPageParallelJob1_step2_reader() {
        return new JpaPagingItemReaderBuilder<Dept>()
                .name("jpaPageParallelJob1_step2_reader")
                .pageSize(CHUNK_SIZE)
                .queryString("SELECT D FROM Dept D WHERE dept_id >= 50")
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

    private ItemProcessor<Dept, Dept2> jpaPageParallelJob1_step2_processor() {
        return dept -> new Dept2(dept.getDeptId(), "NEW_" + dept.getDeptName(),
                "NEW_" + dept.getDeptLocation());
    }

    @Bean
    public JpaItemWriter<Dept2> jpaPageParallelJob1_step2_writer() {
        JpaItemWriter<Dept2> itemWriter = new JpaItemWriter<>();
        itemWriter.setEntityManagerFactory(entityManagerFactory);
        return itemWriter;
    }
}

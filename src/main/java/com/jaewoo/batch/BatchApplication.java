package com.jaewoo.batch;

import com.jaewoo.batch.app.domain.Dept;
import com.jaewoo.batch.app.domain.DeptRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.stream.IntStream;

@Slf4j
@AllArgsConstructor
@EnableBatchProcessing
@SpringBootApplication
public class BatchApplication implements CommandLineRunner {

    private final DeptRepository deptRepository;

    public static void main(String[] args) {
        SpringApplication.run(BatchApplication.class, args);
    }

    @Override
    public void run(String... args) {
//        deptRepository.deleteAll();
//
//        IntStream.range(1, 100)
//                .forEach((index) -> deptRepository.save(new Dept(index, "dept name - " + index, "dept loc - " + index)));
    }
}

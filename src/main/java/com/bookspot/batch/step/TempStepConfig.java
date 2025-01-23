package com.bookspot.batch.step;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class TempStepConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    @Bean
    public Step tempStep() {
        return new StepBuilder("tempStep", jobRepository)
                .tasklet(tempLogTasklet(null), platformTransactionManager)
                .build();
    }

    @Bean
    @StepScope
    public Tasklet tempLogTasklet(@Value("#{jobParameters['filePath']}") String filePath) {
        return (contribution, chunkContext) -> {
            System.out.println("파일 경로 => " + filePath);
            return RepeatStatus.FINISHED;
        };
    }

}

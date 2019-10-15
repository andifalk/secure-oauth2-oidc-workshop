package com.example.library.client.credentials.config;

import com.example.library.client.credentials.batch.BookFieldSetMapper;
import com.example.library.client.credentials.batch.BookLineMapper;
import com.example.library.client.credentials.batch.WebClientItemWriter;
import com.example.library.client.credentials.web.BookResource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class BatchJobConfiguration {

  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;
  private final WebClient webClient;

  @Value("${library.server}")
  private String libraryServer;

  public BatchJobConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, WebClient webClient) {
    this.jobBuilderFactory = jobBuilderFactory;
    this.stepBuilderFactory = stepBuilderFactory;
    this.webClient = webClient;
  }

  @Bean
  public Job importBooksJob(PlatformTransactionManager transactionManager) {
    return this.jobBuilderFactory.get("importBooksJob")
                  .start(importStep(transactionManager))
                  .build();
  }

  @Bean
  public Step importStep(PlatformTransactionManager transactionManager) {
    return this.stepBuilderFactory.get("importStep")
            .transactionManager(transactionManager)
            .<BookResource, BookResource>chunk(10)
            .reader(itemReader())
            .writer(itemWriter())
            .build();
  }

  @Bean
  public ItemWriter<BookResource> itemWriter() {
    return new WebClientItemWriter<>(this.webClient, libraryServer);
  }

  @Bean
  public ItemReader<BookResource> itemReader() {
    BookLineMapper bookLineMapper = new BookLineMapper();
    DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer(";");
    lineTokenizer.setNames("isbn", "title", "description", "authors");
    bookLineMapper.setLineTokenizer(lineTokenizer);
    bookLineMapper.setFieldSetMapper(new BookFieldSetMapper());

    FlatFileItemReader<BookResource> fileItemReader = new FlatFileItemReader<>();
    fileItemReader.setLineMapper(bookLineMapper);
    fileItemReader.setResource(new ClassPathResource("books.csv"));
    return fileItemReader;
  }

}

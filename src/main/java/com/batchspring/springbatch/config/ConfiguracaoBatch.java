package com.batchspring.springbatch.config;

import com.batchspring.springbatch.controller.ProcessadorItemPessoa;
import com.batchspring.springbatch.dominio.Pessoa;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class ConfiguracaoBatch {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;


    @Bean
    public FlatFileItemReader<Pessoa> reader(){
        return new FlatFileItemReaderBuilder<Pessoa>()
                .name("leituraItemPessoa")
                .resource(new ClassPathResource("sample-data.csv"))
                .delimited()
                .names("primeiroNome", "ultimoNome")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<Pessoa>(){{
                    setTargetType(Pessoa.class);
                }})
                .build();
    }

    @Bean
    public ProcessadorItemPessoa processor(){
        return new ProcessadorItemPessoa();
    }


    @Bean
    public JdbcBatchItemWriter<Pessoa> writer(DataSource dataSource){

        return new JdbcBatchItemWriterBuilder<Pessoa>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO pessoa (primeiroNome, ultimoNome) VALUES(:primeiroNome, :ultimoNome)")
                .dataSource(dataSource)
                .build();

    }

    @Bean
    public Job importUserJob(JobCompletionNotificationListener listener, Step step1) {
        return jobBuilderFactory.get("importUserJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1)
                .end()
                .build();
    }

    @Bean
    public Step step1(JdbcBatchItemWriter<Pessoa> writer) {
        return stepBuilderFactory.get("step1")
                .<Pessoa, Pessoa> chunk(10)
                .reader(reader())
                .processor(processor())
                .writer(writer)
                .build();
    }

}

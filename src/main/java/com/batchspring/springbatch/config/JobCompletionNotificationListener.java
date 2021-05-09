package com.batchspring.springbatch.config;

import com.batchspring.springbatch.dominio.Pessoa;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

    private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void beforeJob(JobExecution jobExecution){
        if(jobExecution.getStatus() == BatchStatus.STARTED){
            log.info("Job Iniciado");
        }
    }

    public void afterJob(JobExecution jogJobExecution){
        if(jogJobExecution.getStatus() == BatchStatus.COMPLETED){
            log.info("Job finalizado");

            jdbcTemplate.query("SELECT primeiroNome, ultimoNome FROM pessoa",
                    (rs, row) -> new Pessoa(
                            rs.getString(1),
                            rs.getString(2))
            ).forEach(p -> log.info("Found <" + p + "> in the database."));
        }
    }


}

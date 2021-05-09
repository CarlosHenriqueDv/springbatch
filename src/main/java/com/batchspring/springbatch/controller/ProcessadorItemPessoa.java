package com.batchspring.springbatch.controller;

import com.batchspring.springbatch.dominio.Pessoa;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class ProcessadorItemPessoa implements ItemProcessor<Pessoa, Pessoa> {

    private static final Logger log = LoggerFactory.getLogger(ProcessadorItemPessoa.class);

    @Override
    public Pessoa process(final Pessoa pessoa) throws Exception {

        final String primeiroNome = pessoa.getPrimeiroNome().toUpperCase();
        final String ultimoNome = pessoa.getUltimoNome().toUpperCase();

        final Pessoa pessoaProcessada = new Pessoa(primeiroNome, ultimoNome);

        log.info("Convertendo ( " + pessoa + ") em ( "+ pessoaProcessada + " )" );

        return pessoaProcessada;
    }
}

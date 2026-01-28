package com.elasticsearch.esvectordb;

import org.springframework.boot.SpringApplication;

public class TestEsVectordbApplication {

    public static void main(String[] args) {
        SpringApplication.from(EsVectordbApplication::main).with(TestcontainersConfiguration.class).run(args);
    }
}

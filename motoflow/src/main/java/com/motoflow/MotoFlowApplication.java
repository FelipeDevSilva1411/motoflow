package com.motoflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MotoFlowApplication {

    public static void main(String[] args) {
        SpringApplication.run(MotoFlowApplication.class, args);
    }
}
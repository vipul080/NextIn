package com.vipul.nextin;

import org.springframework.boot.SpringApplication;

public class TestNextInApplication {

    public static void main(String[] args) {
        SpringApplication.from(NextInApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}

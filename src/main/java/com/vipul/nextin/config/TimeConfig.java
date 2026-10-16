package com.vipul.nextin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

@Configuration
public class TimeConfig {

    // "Today" for a queue means today at the clinic, not on the server
    // (a server in UTC would switch to tomorrow at 5:30 AM in India).
    // Injecting a Clock also lets tests use a fixed date.
    @Bean
    public Clock clock() {
        return Clock.system(ZoneId.of("Asia/Kolkata"));
    }
}

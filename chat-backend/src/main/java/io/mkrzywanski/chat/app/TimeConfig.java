package io.mkrzywanski.chat.app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

@Configuration
class TimeConfig {

    @Bean
    Clock clock() {
        return Clock.system(ZoneId.of("UTC"));
    }

}

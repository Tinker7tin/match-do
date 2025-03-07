package org.matcher.me;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class MatcherApplication {

    public static void main(String[] args) {
        SpringApplication.run(MatcherApplication.class, args);
    }

}

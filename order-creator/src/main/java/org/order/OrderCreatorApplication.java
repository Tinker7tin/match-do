package org.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class OrderCreatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderCreatorApplication.class, args);
    }

}

package org.order.me.model;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendMessage(String topic, byte[] message) {
        if (!StringUtils.isBlank(topic)){
            this.kafkaTemplate.send(topic,message);

        }
    }
}

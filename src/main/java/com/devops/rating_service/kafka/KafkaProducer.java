package com.devops.rating_service.kafka;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

@Slf4j
@NoArgsConstructor
@Component
public class KafkaProducer {
    private KafkaTemplate<String, Serializable> kafkaTemplate;

    @Autowired
    public KafkaProducer(KafkaTemplate<String, Serializable> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(String topic, Serializable message) {
        CompletableFuture<SendResult<String, Serializable>> future = CompletableFuture.supplyAsync(() -> {
            try {
                return kafkaTemplate.send(topic, message).get();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (ExecutionException e) {
                return null;
            }
            return null;
        });


        future.thenAccept(result -> {
            log.info("Message sent successfully with offset = {}", result.getRecordMetadata().offset());
        }).exceptionally(throwable -> {
            Throwable originalException = throwable.getCause(); // Get the original exception
            log.error("Unable to send message = {} due to: {}", message.toString(), originalException.getMessage());
            return null;
        });


    }
}

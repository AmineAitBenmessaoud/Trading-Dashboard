package com.trading.backend.watchlistservice.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class WatchlistEventProducer {
    
    private static final String TOPIC = "watchlist-events";
    private final KafkaTemplate<String, WatchlistEvent> kafkaTemplate;
    
    /**
     * Publish watchlist event to Kafka topic
     */
    public void publishEvent(WatchlistEvent event) {
        try {
            log.info("Publishing watchlist event: {}", event);
            kafkaTemplate.send(TOPIC, event.getUserId(), event)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Failed to publish event: {}", ex.getMessage(), ex);
                        } else {
                            log.info("Successfully published event to topic: {} with partition: {}", 
                                TOPIC, result.getRecordMetadata().partition());
                        }
                    });
        } catch (Exception e) {
            log.error("Error publishing event: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Publish add to watchlist event
     */
    public void publishAddEvent(String userId, String symbol) {
        WatchlistEvent event = WatchlistEvent.builder()
                .eventType("WATCHLIST")
                .userId(userId)
                .stockSymbol(symbol)
                .action("ADD")
                .timestamp(LocalDateTime.now())
                .description("Stock added to watchlist: " + symbol)
                .build();
        publishEvent(event);
    }
    
    /**
     * Publish remove from watchlist event
     */
    public void publishRemoveEvent(String userId, String symbol) {
        WatchlistEvent event = WatchlistEvent.builder()
                .eventType("WATCHLIST")
                .userId(userId)
                .stockSymbol(symbol)
                .action("REMOVE")
                .timestamp(LocalDateTime.now())
                .description("Stock removed from watchlist: " + symbol)
                .build();
        publishEvent(event);
    }
    
    /**
     * Publish update watchlist event
     */
    public void publishUpdateEvent(String userId, String description) {
        WatchlistEvent event = WatchlistEvent.builder()
                .eventType("WATCHLIST")
                .userId(userId)
                .action("UPDATE")
                .timestamp(LocalDateTime.now())
                .description(description)
                .build();
        publishEvent(event);
    }
}

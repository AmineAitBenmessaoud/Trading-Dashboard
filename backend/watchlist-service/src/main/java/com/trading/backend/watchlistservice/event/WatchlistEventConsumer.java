package com.trading.backend.watchlistservice.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WatchlistEventConsumer {
    
    /**
     * Listen for market events and invalidate related cache entries
     */
    @KafkaListener(topics = "market-events", groupId = "watchlist-service-group")
    public void handleMarketEvent(String event) {
        log.info("Received market event: {}", event);
        // Handle market events if needed
        // Could be used to invalidate watchlist cache when market data changes
    }
    
    /**
     * Listen for auth events
     */
    @KafkaListener(topics = "auth-events", groupId = "watchlist-service-group")
    public void handleAuthEvent(String event) {
        log.info("Received auth event: {}", event);
        // Handle auth events like user logout or deletion
    }
}

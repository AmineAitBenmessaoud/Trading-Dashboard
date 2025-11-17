package com.trading.backend.watchlistservice.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WatchlistEvent {
    
    @JsonProperty("event_type")
    private String eventType;
    
    @JsonProperty("user_id")
    private String userId;
    
    @JsonProperty("stock_symbol")
    private String stockSymbol;
    
    @JsonProperty("action")
    private String action; // ADD, REMOVE, UPDATE
    
    @JsonProperty("timestamp")
    private LocalDateTime timestamp;
    
    @JsonProperty("description")
    private String description;
}

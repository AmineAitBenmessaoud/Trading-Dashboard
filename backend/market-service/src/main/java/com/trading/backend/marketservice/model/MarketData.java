package com.trading.backend.marketservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketData {
    private String symbol;
    private double price;
    private double change;
    
    @JsonProperty("changePercent")
    private double percentChange;
    
    private long volume;
    private long timestamp;
}
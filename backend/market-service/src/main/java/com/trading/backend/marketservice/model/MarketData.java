package com.trading.backend.marketservice.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketData {
    private String symbol;
    private double price;
    private double change;
    private double percentChange;
    private long timestamp;
}
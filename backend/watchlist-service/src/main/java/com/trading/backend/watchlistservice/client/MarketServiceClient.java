package com.trading.backend.watchlistservice.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

@Slf4j
@Component
@RequiredArgsConstructor
public class MarketServiceClient {
    
    private final RestTemplate restTemplate;
    
    @Value("${market-service.url:http://market-service:8082}")
    private String marketServiceUrl;
    
    /**
     * Fetch market data for a stock symbol
     */
    public MarketDataResponse getMarketData(String symbol) {
        try {
            String url = marketServiceUrl + "/market/" + symbol;
            log.debug("Fetching market data from: {}", url);
            return restTemplate.getForObject(url, MarketDataResponse.class);
        } catch (RestClientException e) {
            log.warn("Failed to fetch market data for symbol: {}. Error: {}", symbol, e.getMessage());
            return null;
        }
    }
    
    /**
     * Get current price for a stock symbol
     */
    public Double getCurrentPrice(String symbol) {
        try {
            MarketDataResponse data = getMarketData(symbol);
            if (data != null && data.getPrice() != null) {
                return data.getPrice();
            }
        } catch (Exception e) {
            log.warn("Failed to get current price for symbol: {}", symbol, e);
        }
        return 0.0;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MarketDataResponse {
        private String symbol;
        private Double price;
        private Double change;
        private Double changePercent;
        private Long volume;
        private Long timestamp;
    }
}

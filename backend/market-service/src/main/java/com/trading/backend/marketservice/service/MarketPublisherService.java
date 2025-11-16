package com.trading.backend.marketservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trading.backend.marketservice.model.MarketData;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MarketPublisherService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void publishMarketData(MarketData marketData) {
        try {
            String json = objectMapper.writeValueAsString(marketData);
            kafkaTemplate.send("market-data", marketData.getSymbol(), json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

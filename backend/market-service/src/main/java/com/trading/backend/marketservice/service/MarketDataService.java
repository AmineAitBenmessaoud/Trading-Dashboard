package com.trading.backend.marketservice.service;

import com.trading.backend.marketservice.model.MarketData;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MarketDataService {

    private final RedisTemplate<String, MarketData> redisTemplate;
    private final ExternalApiService externalApiService;

    public MarketData getMarketData(String symbol) {
        MarketData cachedData = (MarketData) redisTemplate.opsForValue().get(symbol);
        if (cachedData != null) {
            return cachedData;
        }
        MarketData newData = externalApiService.fetchMarketData(symbol);
        redisTemplate.opsForValue().set(symbol, newData);
        return newData;
    }
}
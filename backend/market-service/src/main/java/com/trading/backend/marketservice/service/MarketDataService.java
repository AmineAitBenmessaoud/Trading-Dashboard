package com.trading.backend.marketservice.service;

import com.trading.backend.marketservice.model.MarketData;
import com.trading.backend.marketservice.model.Symbol;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MarketDataService {

    private final RedisTemplate<String, MarketData> redisTemplate;
    private final ExternalApiService externalApiService;

    private static final List<String> DEFAULT_SYMBOLS = Arrays.asList(
            "AAPL", "GOOGL", "MSFT", "AMZN", "TSLA", "META",
            "NVDA", "JPM", "V", "JNJ", "WMT", "PG"
    );

    public MarketData getMarketData(String symbol) {
        MarketData cachedData = (MarketData) redisTemplate.opsForValue().get(symbol);
        if (cachedData != null) {
            return cachedData;
        }
        MarketData newData = externalApiService.fetchMarketData(symbol);
        redisTemplate.opsForValue().set(symbol, newData);
        return newData;
    }

    public List<MarketData> getTopMovers() {
        return DEFAULT_SYMBOLS.stream()
                .map(this::getMarketData)
                .sorted(Comparator.comparingDouble(MarketData::getPercentChange).reversed())
                .limit(6)
                .collect(Collectors.toList());
    }

    public List<MarketData> getTrendingSymbols() {
        return DEFAULT_SYMBOLS.stream()
                .map(this::getMarketData)
                .sorted(Comparator.comparingDouble(MarketData::getPrice).reversed())
                .limit(5)
                .collect(Collectors.toList());
    }

    public List<Symbol> searchSymbols(String query) {
        if (query == null || query.trim().isEmpty()) {
            return Collections.emptyList();
        }
        String upperQuery = query.toUpperCase();
        List<Symbol> mockSymbols = Arrays.asList(
                new Symbol("AAPL", "Apple Inc.", "NASDAQ"),
                new Symbol("GOOGL", "Alphabet Inc.", "NASDAQ"),
                new Symbol("MSFT", "Microsoft Corporation", "NASDAQ"),
                new Symbol("AMZN", "Amazon.com Inc.", "NASDAQ"),
                new Symbol("TSLA", "Tesla Inc.", "NASDAQ"),
                new Symbol("META", "Meta Platforms Inc.", "NASDAQ"),
                new Symbol("NVDA", "NVIDIA Corporation", "NASDAQ"),
                new Symbol("JPM", "JPMorgan Chase & Co.", "NYSE"),
                new Symbol("V", "Visa Inc.", "NYSE"),
                new Symbol("JNJ", "Johnson & Johnson", "NYSE"),
                new Symbol("WMT", "Walmart Inc.", "NYSE"),
                new Symbol("PG", "Procter & Gamble Co.", "NYSE")
        );
        return mockSymbols.stream()
                .filter(s -> s.getTicker().contains(upperQuery) || s.getName().toUpperCase().contains(upperQuery))
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getHistoricalData(String symbol, String period) {
        // Return mock historical data
        List<Map<String, Object>> history = new ArrayList<>();
        for (int i = 29; i >= 0; i--) {
            Map<String, Object> dayData = new HashMap<>();
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -i);
            dayData.put("date", cal.getTime());
            dayData.put("open", 150 + Math.random() * 20);
            dayData.put("high", 155 + Math.random() * 20);
            dayData.put("low", 145 + Math.random() * 20);
            dayData.put("close", 152 + Math.random() * 20);
            dayData.put("volume", 50000000 + (long) (Math.random() * 50000000));
            history.add(dayData);
        }
        return history;
    }
}
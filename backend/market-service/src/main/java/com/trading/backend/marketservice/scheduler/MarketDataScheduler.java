package com.trading.backend.marketservice.scheduler;

import com.trading.backend.marketservice.model.MarketData;
import com.trading.backend.marketservice.service.MarketDataService;
import com.trading.backend.marketservice.service.MarketPublisherService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MarketDataScheduler {

    private final MarketDataService marketDataService;
    private final MarketPublisherService publisherService;
    //todo change this accordingly to watchlist
    private static final String[] WATCHED_SYMBOLS = {"AAPL", "GOOGL", "MSFT", "AMZN"};

    @Scheduled(fixedRateString = "${market.scheduler.refresh-interval}")
    public void fetchMarketUpdates() {
        for (String symbol : WATCHED_SYMBOLS) {
            MarketData data = marketDataService.getMarketData(symbol);
            publisherService.publishMarketData(data);
        }
    }
}
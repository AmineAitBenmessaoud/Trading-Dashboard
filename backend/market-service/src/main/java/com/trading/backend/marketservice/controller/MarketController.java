package com.trading.backend.marketservice.controller;

import com.trading.backend.marketservice.model.MarketData;
import com.trading.backend.marketservice.service.MarketDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/market")
@RequiredArgsConstructor
public class MarketController {

    private final MarketDataService marketDataService;

    @GetMapping("/{symbol}")
    public MarketData getMarketData(@PathVariable String symbol) {
        return marketDataService.getMarketData(symbol);
    }
}

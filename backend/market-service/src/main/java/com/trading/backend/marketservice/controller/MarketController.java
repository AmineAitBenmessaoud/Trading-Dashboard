package com.trading.backend.marketservice.controller;

import com.trading.backend.marketservice.model.MarketData;
import com.trading.backend.marketservice.model.Symbol;
import com.trading.backend.marketservice.service.MarketDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/market")
@RequiredArgsConstructor
public class MarketController {

    private final MarketDataService marketDataService;

    @GetMapping("/{symbol}")
    public MarketData getMarketData(@PathVariable String symbol) {
        return marketDataService.getMarketData(symbol);
    }

    @GetMapping("/data/{symbol}")
    public ResponseEntity<MarketData> getMarketDataBySymbol(@PathVariable String symbol) {
        try {
            MarketData data = marketDataService.getMarketData(symbol);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/top-movers")
    public ResponseEntity<List<MarketData>> getTopMovers() {
        try {
            List<MarketData> topMovers = marketDataService.getTopMovers();
            return ResponseEntity.ok(topMovers);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/trending")
    public ResponseEntity<List<MarketData>> getTrendingSymbols() {
        try {
            List<MarketData> trending = marketDataService.getTrendingSymbols();
            return ResponseEntity.ok(trending);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Symbol>> searchSymbols(@RequestParam("q") String query) {
        try {
            List<Symbol> results = marketDataService.searchSymbols(query);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/history/{symbol}")
    public ResponseEntity<List<Map<String, Object>>> getHistoricalData(
            @PathVariable String symbol,
            @RequestParam(value = "period", defaultValue = "1M") String period) {
        try {
            List<Map<String, Object>> history = marketDataService.getHistoricalData(symbol, period);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}

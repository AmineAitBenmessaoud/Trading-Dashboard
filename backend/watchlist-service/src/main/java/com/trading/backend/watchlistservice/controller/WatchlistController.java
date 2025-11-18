package com.trading.backend.watchlistservice.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.trading.backend.watchlistservice.dto.WatchlistDto;
import com.trading.backend.watchlistservice.dto.WatchlistItemDto;
import com.trading.backend.watchlistservice.service.WatchlistService;
import com.trading.backend.watchlistservice.util.JwtUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

@RestController
@RequestMapping("/watchlist")
@RequiredArgsConstructor
@Slf4j
public class WatchlistController {
    
    private final WatchlistService watchlistService;
    private final JwtUtil jwtUtil;
    
    /**
     * Get user's watchlist
     */
    @GetMapping
    public ResponseEntity<WatchlistDto> getWatchlist(@RequestHeader("Authorization") String authHeader) {
        String userId = extractUserIdFromAuth(authHeader);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        WatchlistDto watchlist = watchlistService.getWatchlist(userId);
        return ResponseEntity.ok(watchlist);
    }
    
    /**
     * Add stock to watchlist
     */
    @PostMapping("/add")
    public ResponseEntity<WatchlistItemDto> addToWatchlist(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody AddToWatchlistRequest request) {
        String userId = extractUserIdFromAuth(authHeader);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        log.info("Received add request - Raw request object: {}", request);
        log.info("Request is null: {}", request == null);
        if (request != null) {
            log.info("Request symbol: '{}'", request.getSymbol());
        }
        
        try {
            if (request == null || request.getSymbol() == null || request.getSymbol().trim().isEmpty()) {
                log.error("Invalid request - Symbol is null or empty. Request object: {}", request);
                return ResponseEntity.badRequest().build();
            }
            String symbol = request.getSymbol().trim().toUpperCase();
            log.info("Adding symbol '{}' to watchlist for user '{}'", symbol, userId);
            WatchlistItemDto item = watchlistService.addToWatchlist(userId, symbol);
            return ResponseEntity.status(HttpStatus.CREATED).body(item);
        } catch (IllegalArgumentException e) {
            log.error("IllegalArgumentException: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Exception in addToWatchlist: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Remove stock from watchlist
     */
    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> removeFromWatchlist(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long itemId) {
        String userId = extractUserIdFromAuth(authHeader);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            watchlistService.removeFromWatchlist(userId, itemId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Update watchlist
     */
    @PutMapping
    public ResponseEntity<WatchlistDto> updateWatchlist(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody UpdateWatchlistRequest request) {
        String userId = extractUserIdFromAuth(authHeader);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        WatchlistDto watchlist = watchlistService.updateWatchlist(userId, request.getItems());
        return ResponseEntity.ok(watchlist);
    }
    
    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Watchlist Service is running");
    }
    
    private String extractUserIdFromAuth(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        String token = authHeader.substring(7);
        return jwtUtil.extractUserId(token);
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddToWatchlistRequest {
        @JsonProperty("symbol")
        @NotBlank(message = "Symbol cannot be blank")
        private String symbol;
        
        @Override
        public String toString() {
            return "AddToWatchlistRequest{" +
                    "symbol='" + symbol + '\'' +
                    '}';
        }
    }
    
    @Data
    public static class UpdateWatchlistRequest {
        private List<WatchlistItemDto> items;
    }
}

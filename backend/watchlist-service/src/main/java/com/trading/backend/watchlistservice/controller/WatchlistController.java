package com.trading.backend.watchlistservice.controller;

import com.trading.backend.watchlistservice.dto.WatchlistDto;
import com.trading.backend.watchlistservice.dto.WatchlistItemDto;
import com.trading.backend.watchlistservice.service.WatchlistService;
import com.trading.backend.watchlistservice.util.JwtUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/watchlist")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
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
            @RequestBody AddToWatchlistRequest request) {
        String userId = extractUserIdFromAuth(authHeader);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            WatchlistItemDto item = watchlistService.addToWatchlist(userId, request.getSymbol());
            return ResponseEntity.status(HttpStatus.CREATED).body(item);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
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
    public static class AddToWatchlistRequest {
        private String symbol;
    }
    
    @Data
    public static class UpdateWatchlistRequest {
        private List<WatchlistItemDto> items;
    }
}

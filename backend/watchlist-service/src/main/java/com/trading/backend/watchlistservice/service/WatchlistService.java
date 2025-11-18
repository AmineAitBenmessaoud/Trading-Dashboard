package com.trading.backend.watchlistservice.service;

import com.trading.backend.watchlistservice.dto.WatchlistDto;
import com.trading.backend.watchlistservice.dto.WatchlistItemDto;
import com.trading.backend.watchlistservice.event.WatchlistEventProducer;
import com.trading.backend.watchlistservice.model.Watchlist;
import com.trading.backend.watchlistservice.repository.WatchlistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WatchlistService {
    
    private final WatchlistRepository watchlistRepository;
    private final CacheService cacheService;
    private final WatchlistEventProducer eventProducer;
    
    /**
     * Get user's watchlist with caching
     */
    public WatchlistDto getWatchlist(String userId) {
        log.info("Fetching watchlist for user: {}", userId);
        
        // Try to get from cache first
        WatchlistDto cached = cacheService.getWatchlistFromCache(userId);
        if (cached != null) {
            log.info("Returning cached watchlist for user: {}", userId);
            return cached;
        }
        
        // Fetch from database
        List<Watchlist> items = watchlistRepository.findByUserId(userId);
        WatchlistDto watchlist = buildWatchlistDto(userId, items);
        
        // Save to cache
        cacheService.saveWatchlistToCache(userId, watchlist);
        return watchlist;
    }
    
    /**
     * Add stock to watchlist with Kafka event and cache invalidation
     */
    public WatchlistItemDto addToWatchlist(String userId, String symbol) {
        log.info("Adding stock {} to watchlist for user: {}", symbol, userId);
        
        // Check if already exists
        if (watchlistRepository.existsByUserIdAndStockSymbol(userId, symbol)) {
            throw new IllegalArgumentException("Stock already in watchlist");
        }
        
        Watchlist watchlist = Watchlist.builder()
                .userId(userId)
                .stockSymbol(symbol)
                .build();
        
        Watchlist saved = watchlistRepository.save(watchlist);
        
        // Publish event to Kafka
        eventProducer.publishAddEvent(userId, symbol);
        
        // Invalidate cache
        cacheService.invalidateWatchlistCache(userId);
        
        return convertToWatchlistItemDto(saved);
    }
    
    /**
     * Remove stock from watchlist with Kafka event and cache invalidation
     */
    public void removeFromWatchlist(String userId, Long itemId) {
        log.info("Removing watchlist item {} for user: {}", itemId, userId);
        
        Watchlist watchlist = watchlistRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Watchlist item not found"));
        
        if (!watchlist.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized");
        }
        
        String symbol = watchlist.getStockSymbol();
        watchlistRepository.deleteById(itemId);
        
        // Publish event to Kafka
        eventProducer.publishRemoveEvent(userId, symbol);
        
        // Invalidate cache
        cacheService.invalidateWatchlistCache(userId);
    }
    
    /**
     * Remove stock by symbol with Kafka event and cache invalidation
     */
    public void removeFromWatchlistBySymbol(String userId, String symbol) {
        log.info("Removing stock {} from watchlist for user: {}", symbol, userId);
        
        watchlistRepository.deleteByUserIdAndStockSymbol(userId, symbol);
        
        // Publish event to Kafka
        eventProducer.publishRemoveEvent(userId, symbol);
        
        // Invalidate cache
        cacheService.invalidateWatchlistCache(userId);
    }
    
    /**
     * Update watchlist items with Kafka event and cache invalidation
     */
    public WatchlistDto updateWatchlist(String userId, List<WatchlistItemDto> items) {
        log.info("Updating watchlist for user: {}", userId);
        
        // Delete all existing items
        List<Watchlist> currentItems = watchlistRepository.findByUserId(userId);
        watchlistRepository.deleteAll(currentItems);
        
        // Add new items
        List<Watchlist> newItems = new ArrayList<>();
        for (WatchlistItemDto item : items) {
            Watchlist watchlist = Watchlist.builder()
                    .userId(userId)
                    .stockSymbol(item.getSymbol())
                    .build();
            newItems.add(watchlist);
        }
        watchlistRepository.saveAll(newItems);
        
        // Publish event to Kafka
        eventProducer.publishUpdateEvent(userId, "Watchlist updated with " + items.size() + " items");
        
        // Invalidate cache
        cacheService.invalidateWatchlistCache(userId);
        
        return buildWatchlistDto(userId, newItems);
    }
    
    private WatchlistDto buildWatchlistDto(String userId, List<Watchlist> items) {
        List<WatchlistItemDto> itemDtos = items.stream()
                .map(this::convertToWatchlistItemDto)
                .collect(Collectors.toList());
        
        return WatchlistDto.builder()
                .userId(userId)
                .items(itemDtos)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    private WatchlistItemDto convertToWatchlistItemDto(Watchlist watchlist) {
        return WatchlistItemDto.builder()
                .id(watchlist.getId().toString())
                .symbol(watchlist.getStockSymbol())
                .name(watchlist.getStockSymbol())
                .currentPrice(0.0) // Prices will be fetched from frontend separately
                .addedAt(watchlist.getAddedAt())
                .build();
    }
}

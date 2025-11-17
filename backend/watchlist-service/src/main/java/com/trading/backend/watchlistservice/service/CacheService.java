package com.trading.backend.watchlistservice.service;

import com.trading.backend.watchlistservice.dto.WatchlistDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class CacheService {
    
    private static final String WATCHLIST_CACHE_KEY_PREFIX = "watchlist:";
    private static final long CACHE_TTL_HOURS = 24;
    private final RedisTemplate<String, WatchlistDto> watchlistRedisTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    
    /**
     * Get watchlist from cache
     */
    public WatchlistDto getWatchlistFromCache(String userId) {
        String cacheKey = getCacheKey(userId);
        try {
            WatchlistDto cached = watchlistRedisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                log.info("Cache hit for watchlist: {}", userId);
            }
            return cached;
        } catch (Exception e) {
            log.error("Error retrieving from cache: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Save watchlist to cache
     */
    public void saveWatchlistToCache(String userId, WatchlistDto watchlist) {
        String cacheKey = getCacheKey(userId);
        try {
            watchlistRedisTemplate.opsForValue().set(cacheKey, watchlist, CACHE_TTL_HOURS, TimeUnit.HOURS);
            log.info("Saved watchlist to cache: {}", userId);
        } catch (Exception e) {
            log.error("Error saving to cache: {}", e.getMessage());
        }
    }
    
    /**
     * Invalidate watchlist cache
     */
    public void invalidateWatchlistCache(String userId) {
        String cacheKey = getCacheKey(userId);
        try {
            Boolean deleted = redisTemplate.delete(cacheKey);
            if (Boolean.TRUE.equals(deleted)) {
                log.info("Invalidated cache for watchlist: {}", userId);
            }
        } catch (Exception e) {
            log.error("Error invalidating cache: {}", e.getMessage());
        }
    }
    
    /**
     * Invalidate all watchlist cache
     */
    public void invalidateAllWatchlistCache() {
        try {
            redisTemplate.delete(redisTemplate.keys(WATCHLIST_CACHE_KEY_PREFIX + "*"));
            log.info("Invalidated all watchlist cache");
        } catch (Exception e) {
            log.error("Error invalidating all cache: {}", e.getMessage());
        }
    }
    
    /**
     * Cache existence check for specific stock symbol per user
     */
    public boolean isStockInCache(String userId, String symbol) {
        String cacheKey = getStockCacheKey(userId, symbol);
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(cacheKey));
        } catch (Exception e) {
            log.error("Error checking cache: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Cache stock symbol for user
     */
    public void cacheStockSymbol(String userId, String symbol) {
        String cacheKey = getStockCacheKey(userId, symbol);
        try {
            redisTemplate.opsForValue().set(cacheKey, "true", 1, TimeUnit.HOURS);
        } catch (Exception e) {
            log.error("Error caching stock symbol: {}", e.getMessage());
        }
    }
    
    private String getCacheKey(String userId) {
        return WATCHLIST_CACHE_KEY_PREFIX + userId;
    }
    
    private String getStockCacheKey(String userId, String symbol) {
        return WATCHLIST_CACHE_KEY_PREFIX + userId + ":stock:" + symbol;
    }
}

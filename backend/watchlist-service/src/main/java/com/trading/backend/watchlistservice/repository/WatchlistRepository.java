package com.trading.backend.watchlistservice.repository;

import com.trading.backend.watchlistservice.model.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {
    
    List<Watchlist> findByUserId(String userId);
    
    Optional<Watchlist> findByUserIdAndStockSymbol(String userId, String stockSymbol);
    
    void deleteByUserIdAndStockSymbol(String userId, String stockSymbol);
    
    boolean existsByUserIdAndStockSymbol(String userId, String stockSymbol);
}

package com.trading.backend.watchlistservice.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WatchlistItemDto {
    private String id;
    private String symbol;
    private String name;
    private double currentPrice;
    private LocalDateTime addedAt;
}

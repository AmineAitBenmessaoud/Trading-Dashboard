package com.trading.backend.watchlistservice.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WatchlistDto {
    private String id;
    private String userId;
    private List<WatchlistItemDto> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

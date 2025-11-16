package com.trading.backend.marketservice.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Symbol {
    private String ticker;
    private String name;
    private String market;
}
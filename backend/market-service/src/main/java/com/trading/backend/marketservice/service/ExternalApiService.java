package com.trading.backend.marketservice.service;

import com.trading.backend.marketservice.model.MarketData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.json.JSONObject;

@Service
public class ExternalApiService {

    @Value("${market.api.base-url}")
    private String baseUrl;

    @Value("${market.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public MarketData fetchMarketData(String symbol) {
        try {
            String url = baseUrl + "/query?function=GLOBAL_QUOTE&symbol=" + symbol + "&apikey=" + apiKey;
            String response = restTemplate.getForObject(url, String.class);
            JSONObject json = new JSONObject(response).getJSONObject("Global Quote");

            return MarketData.builder()
                    .symbol(symbol)
                    .price(json.getDouble("05. price"))
                    .change(json.getDouble("09. change"))
                    .percentChange(Double.parseDouble(json.getString("10. change percent").replace("%", "")))
                    .timestamp(System.currentTimeMillis())
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Error fetching data for symbol: " + symbol);
        }
    }
}

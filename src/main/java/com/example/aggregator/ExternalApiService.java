package com.example.aggregator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class ExternalApiService {
    private static final Logger logger = LoggerFactory.getLogger(ExternalApiService.class);
    private static final RestTemplate restTemplate = new RestTemplate();

    @Async
    public CompletableFuture<ResponseEntity<List<Transaction>>> getTransactionsAsync(String account, String apiUrl, Map<String, String> params) {
        for (int i = 0; i < 5; i++) {
            try {
                ResponseEntity<List<Transaction>> response = restTemplate.exchange(
                        apiUrl,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<>() {
                        },
                        params
                );
                if (response.getStatusCode() == HttpStatus.OK) {
                    logger.info("Attempt {} success {} {}", i + 1, apiUrl, response.getBody());
                    return CompletableFuture.completedFuture(response);
                }
            } catch (HttpServerErrorException | HttpClientErrorException e) {
                logger.info("Attempt {} fail {} {}", i + 1, apiUrl, e.getStatusCode());
            }

        }
        return CompletableFuture.completedFuture(ResponseEntity.ok(Collections.emptyList()));
    }
}
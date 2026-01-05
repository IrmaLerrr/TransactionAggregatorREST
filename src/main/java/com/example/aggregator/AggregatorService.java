package com.example.aggregator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class AggregatorService {
    @Autowired
    private ExternalApiService externalApiService;

    private final List<String> apiUrls = List.of(
            "http://localhost:8888/",
            "http://localhost:8889/"
    );
    private static final Logger logger = LoggerFactory.getLogger(AggregatorService.class);
    private static final RestTemplate restTemplate = new RestTemplate();

    public List<Transaction> aggregateTransactions(String account) {

        List<Transaction> list = new ArrayList<>();
        List<CompletableFuture<ResponseEntity<List<Transaction>>>> completableFuture = new ArrayList<>();

        for (String apiUrl : apiUrls) {
            apiUrl = apiUrl + "transactions?account={account}";
            Map<String, String> params = new HashMap<>();
            params.put("account", account);
            completableFuture.add(externalApiService.getTransactionsAsync(account, apiUrl, params));
        }

        try {
            for (CompletableFuture<ResponseEntity<List<Transaction>>> responseEntityCompletableFuture : completableFuture) {
                list.addAll(Objects.requireNonNull(responseEntityCompletableFuture.get().getBody()));
            }
        } catch (InterruptedException | ExecutionException ex) {
            logger.error(ex.getMessage());
        }

        list.sort(Comparator.comparing(Transaction::getTimestamp).reversed());

        return list;
    }
}


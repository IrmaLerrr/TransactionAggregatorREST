package com.example.aggregator;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionDao {
    private final AggregatorService aggregatorService;

    public TransactionDao(AggregatorService aggregatorService) {
        this.aggregatorService = aggregatorService;
    }

    @Cacheable(cacheNames = "transactions", key = "#account")
    public List<Transaction> getTransactions(String account) {
        return aggregatorService.aggregateTransactions(account);
    }
}

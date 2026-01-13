package com.banking.ledger.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Metrics configuration for observability.
 * Provides counters and timers for critical ledger operations.
 */
@Configuration
public class MetricsConfig {

    @Bean
    public Counter transactionCounter(MeterRegistry registry) {
        return Counter.builder("ledger.transaction.count")
                .description("Total number of transactions processed")
                .tag("service", "ledger")
                .register(registry);
    }

    @Bean
    public Counter transactionSuccessCounter(MeterRegistry registry) {
        return Counter.builder("ledger.transaction.success")
                .description("Number of successful transactions")
                .tag("service", "ledger")
                .register(registry);
    }

    @Bean
    public Counter transactionFailureCounter(MeterRegistry registry) {
        return Counter.builder("ledger.transaction.failure")
                .description("Number of failed transactions")
                .tag("service", "ledger")
                .register(registry);
    }

    @Bean
    public Timer transactionTimer(MeterRegistry registry) {
        return Timer.builder("ledger.transaction.duration")
                .description("Transaction processing duration")
                .tag("service", "ledger")
                .register(registry);
    }

    @Bean
    public Counter accountCreationCounter(MeterRegistry registry) {
        return Counter.builder("ledger.account.created")
                .description("Number of accounts created")
                .tag("service", "ledger")
                .register(registry);
    }
}

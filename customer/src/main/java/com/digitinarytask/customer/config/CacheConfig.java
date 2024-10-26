
package com.digitinarytask.customer.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@EnableCaching
@Configuration
public class CacheConfig {

    /**
     * Configure different TTLs for different cache types
     */
    private static final int SHORT_TTL = 5;
    private static final int MEDIUM_TTL = 15;
    private static final int LONG_TTL = 60;

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();

        // Configure default cache settings
        cacheManager.setCaffeine(Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(MEDIUM_TTL, TimeUnit.MINUTES)
            .recordStats());

        // Register all caches
        cacheManager.setCacheNames(Arrays.asList(
            // Account related caches
            "accounts",                    // Individual accounts// Account summaries by customer
            "customerAccounts",           // All accounts for a customer
            "topAccounts",               // Top accounts by balance

            // Organization related caches
            "organizations",              // Individual organizations
            "organizationsByCustomer",    // Organizations by customer

            // Customer related caches
            "customers",                  // Individual customers// Customer summaries

            // Address related caches
            "addresses"                   // Individual addresses
        ));

        customizeCaches(cacheManager);

        return cacheManager;
    }

    /**
     * Configure specific caches with custom settings
     */
    private void customizeCaches(CaffeineCacheManager cacheManager) {

        configureCacheSettings(cacheManager, "topAccounts",
            Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(SHORT_TTL, TimeUnit.MINUTES)
                .recordStats());

        // Medium TTL for semi-static data
        configureCacheSettings(cacheManager, "customers",
            Caffeine.newBuilder()
                .maximumSize(10000)
                .expireAfterWrite(MEDIUM_TTL, TimeUnit.MINUTES)
                .recordStats());

        configureCacheSettings(cacheManager, "customerAccounts",
            Caffeine.newBuilder()
                .maximumSize(5000)
                .expireAfterWrite(MEDIUM_TTL, TimeUnit.MINUTES)
                .recordStats());

        // Long TTL for relatively static data
        configureCacheSettings(cacheManager, "organizations",
            Caffeine.newBuilder()
                .maximumSize(5000)
                .expireAfterWrite(LONG_TTL, TimeUnit.MINUTES)
                .recordStats());

    }

    private void configureCacheSettings(CaffeineCacheManager cacheManager, String cacheName,
                                        Caffeine<Object, Object> settings) {
        cacheManager.registerCustomCache(cacheName, settings.build());
    }
}

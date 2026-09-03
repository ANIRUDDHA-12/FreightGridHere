package com.example.FreightGrid.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.boot.cache.autoconfigure.CacheProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager(){
        CaffeineCacheManager manager=new CaffeineCacheManager();

        manager.registerCustomCache("chatMemory", Caffeine.newBuilder()
                .expireAfterAccess(30, TimeUnit.SECONDS)
                .maximumSize(1000)
                .build());

        manager.registerCustomCache("rateLimits", Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .maximumSize(5000)
                .build());
        return manager;
    }
}

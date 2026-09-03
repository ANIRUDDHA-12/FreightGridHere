package com.example.FreightGrid.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RateLimiter extends OncePerRequestFilter {
    private final Cache rateLimitCache;
    private static final int max_requests_per_minute=5;

    public RateLimiter(CacheManager cacheManager){
        this.rateLimitCache=cacheManager.getCache("rateLimits");

    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String clientIp=request.getRemoteAddr();

        if(request.getRequestURI().startsWith("/actuator")){
            filterChain.doFilter(request,response);
            return;
        }
        Integer requests=rateLimitCache.get(clientIp,Integer.class);
        if (requests == null) {

            requests=0;
        }
        if(requests>=max_requests_per_minute){
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("429 Too many Requests.Rate Limit Exceeded");
            return;
        }
        rateLimitCache.put(clientIp,requests+1);

        filterChain.doFilter(request,response);
    }
}

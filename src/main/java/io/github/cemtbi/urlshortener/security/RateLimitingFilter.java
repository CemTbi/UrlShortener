package io.github.cemtbi.urlshortener.security;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RateLimitingFilter implements Filter {

	// Store the Buckets for every IP-Adress in RAM
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    // Only 10 Requests per minute
    private Bucket createNewBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(10)
                        .refillIntervally(10, Duration.ofMinutes(1))
                        .build())
                .build();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String path = httpRequest.getRequestURI();

        if ("POST".equalsIgnoreCase(httpRequest.getMethod()) && path.equals("/api/urls")) {
            
            // Retreives the real IP-Adress - Not the Proxy 
            String ip = httpRequest.getRemoteAddr();
            
            // Retreives the IP for the bucket and creates a new one
            Bucket bucket = buckets.computeIfAbsent(ip, k -> createNewBucket());

            // Tries to retreive a token
            if (!bucket.tryConsume(1)) {
                httpResponse.setStatus(429); // HTTP 429 Too Many Requests
                httpResponse.setContentType("application/json;charset=UTF-8");
                httpResponse.getWriter().write("{\"error\": \"Too many requests. Please wait.\"}");
                return;
            }
        }

        // Normal or GET Requests still work
        chain.doFilter(request, response);
    }
}


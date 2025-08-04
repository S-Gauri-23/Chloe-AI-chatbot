package com.springboot.chatgpt.Config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.ServerRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter implements Filter {
    private final Map<String, Bucket> bucketCache = new ConcurrentHashMap<>();
    private Object response;

    private Bucket createNewBucket(){
        Refill refill = Refill.greedy(5, Duration.ofMinutes(1));
        Bandwidth limit = Bandwidth.classic(5, refill);
        return Bucket.builder().addLimit(limit).build();
    }

    private Bucket resolveBucket(String ip){
        return bucketCache.computeIfAbsent(ip, k -> createNewBucket());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String ip = req.getRemoteAddr();
        Bucket bucket = resolveBucket(ip);

//        String key = ip + ":" + req.getRequestURI();
//        Bucket bucket = resolveBucket(key);

        if(bucket.tryConsume(1)){
            chain.doFilter(request, response);
        }
        else{
            res.setStatus(429);
            res.getWriter().write("Too many requests - Please try again later.");
        }
    }
}
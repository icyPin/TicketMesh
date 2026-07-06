package com.example.api_gateway;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.http.HttpHeaders;

@Component
public class WaitingRoomFilter implements GlobalFilter, Ordered {

    private final ReactiveStringRedisTemplate redisTemplate;

    public WaitingRoomFilter(ReactiveStringRedisTemplate redisTemplate){
        this.redisTemplate=redisTemplate;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        System.out.println("server running");

        if(path.contains("/api/orders/buy")){
            String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");
            String eventName = exchange.getRequest().getHeaders().getFirst("X-Event-Name");
            String seatNumber = exchange.getRequest().getHeaders().getFirst("X-Seat-Number");

            if(userId==null || eventName==null || seatNumber==null){
                exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
                return exchange.getResponse().setComplete();
            }

            String payload = String.format(
                    "{\\\"userId\\\":\\\"%s\\\", \\\"eventName\\\":\\\"%s\\\", \\\"seatNumber\\\":\\\"%s\\\"}",
                    userId,eventName,seatNumber
            );
            return redisTemplate.opsForList().leftPush("waiting-room-queue", payload).flatMap(
                    queuePosition -> {
                        exchange.getResponse().setStatusCode(HttpStatus.ACCEPTED);
                        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
                        String response = "{\"message\": \"Traffic is high. You are in the waiting room!\", \"queue_position\": " + queuePosition + "}";
                        byte[] bytes = response.getBytes();

                        var buffer = exchange.getResponse().bufferFactory().wrap(bytes);

                        return exchange.getResponse().writeWith(Mono.just(buffer));
                    });
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder(){
        return -1;
    }
}

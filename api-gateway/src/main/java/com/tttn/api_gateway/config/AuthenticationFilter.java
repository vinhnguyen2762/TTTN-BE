package com.tttn.api_gateway.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tttn.api_gateway.dto.ApiResponse;
import com.tttn.api_gateway.dto.TokenDto;
import com.tttn.api_gateway.service.IdentityTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Configuration
@Slf4j
public class AuthenticationFilter implements GlobalFilter, Ordered {
    private final IdentityTokenService identityTokenService;
    private final ObjectMapper objectMapper;

    public AuthenticationFilter(IdentityTokenService identityTokenService, ObjectMapper objectMapper) {
        this.identityTokenService = identityTokenService;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("Enter authentication filter");
        // get token form authorization header
        List<String> authHeaders = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION);
        if (CollectionUtils.isEmpty(authHeaders)) {
            return unauthenticated(exchange.getResponse());
        }

        String token = authHeaders.get(0).replace("Bearer ", "");

        log.info("Token: {}", token);

        TokenDto tokenDto = new TokenDto(token);

        return identityTokenService.checkToken(tokenDto).flatMap(response -> {
           if (response.equals(1L)) {
               return chain.filter(exchange);
           } else {
               return unauthenticated(exchange.getResponse());
           }
        }).onErrorResume(throwable -> unauthenticated(exchange.getResponse()));
    }

    @Override
    public int getOrder() {
        return -1;
    }

    Mono<Void> unauthenticated(ServerHttpResponse response) {
        ApiResponse apiResponse = new ApiResponse(401, "Unauthenticated");

        String body;
        try {
            body = objectMapper.writeValueAsString(apiResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes())));
    }

}

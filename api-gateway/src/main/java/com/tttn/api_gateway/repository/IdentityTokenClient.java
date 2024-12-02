package com.tttn.api_gateway.repository;

import com.tttn.api_gateway.dto.TokenDto;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Mono;

public interface IdentityTokenClient {
    @PostExchange(url = "/auth/check-token", contentType = MediaType.APPLICATION_JSON_VALUE)
    Mono<ResponseEntity<Long>> checkToken(@RequestBody TokenDto tokenDto);
}

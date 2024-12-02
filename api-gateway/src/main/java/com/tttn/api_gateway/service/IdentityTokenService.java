package com.tttn.api_gateway.service;

import com.tttn.api_gateway.dto.TokenDto;
import com.tttn.api_gateway.repository.IdentityTokenClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class IdentityTokenService {
    private final IdentityTokenClient identityTokenClient;

    public IdentityTokenService(IdentityTokenClient identityTokenClient) {
        this.identityTokenClient = identityTokenClient;
    }

    public Mono<Long> checkToken(TokenDto tokenDto) {
        return identityTokenClient.checkToken(tokenDto)  // Trả về Mono<ResponseEntity<Long>>
                .map(response -> response.getBody());  // Chuyển đổi Mono<ResponseEntity<Long>> thành Mono<Long>
    }
}

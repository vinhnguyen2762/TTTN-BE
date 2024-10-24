package com.tttn.api_gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

public class TestController {
    @RestController
    public class RoutesController {

        @Autowired
        private RouteDefinitionLocator locator;

        @GetMapping("/routes")
        public Flux<RouteDefinition> getRoutes() {
            return locator.getRouteDefinitions();
        }
    }
}

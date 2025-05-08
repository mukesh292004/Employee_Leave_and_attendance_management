package com.example.Apigateway.filter;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;

import com.example.Apigateway.util.JwtUtil;
import com.google.common.net.HttpHeaders;

import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired
    private RouteValidator validator;

    @Autowired
    private JwtUtil util;

    public static class Config {}

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if (validator.isSecured.test(exchange.getRequest())) {
                try {
                    if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                        return onError(exchange.getResponse(), HttpStatus.BAD_REQUEST,
                                "Authorization header is missing.", exchange.getRequest().getPath().toString());
                    }

                    String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
                    if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        authHeader = authHeader.substring(7);
                    }

                    util.validateToken(authHeader);
                    String role = util.extractRolesFromToken(authHeader);
                    String requestedPath = exchange.getRequest().getPath().toString();
                    String method = exchange.getRequest().getMethod().name();

                    if (!isAuthorized(role, requestedPath, method)) {
                        return onError(exchange.getResponse(), HttpStatus.FORBIDDEN,
                                "You do not have permission to access this resource.", requestedPath);
                    }

                } catch (Exception e) {
                    return onError(exchange.getResponse(), HttpStatus.UNAUTHORIZED,
                            "Invalid or expired token.", exchange.getRequest().getPath().toString());
                }
            }
            return chain.filter(exchange);
        };
    }

    private Mono<Void> onError(ServerHttpResponse response, HttpStatus status, String message, String path) {
        response.setStatusCode(status);
        response.getHeaders().add("Content-Type", "application/json");

        String body = String.format(
                "{\"timestamp\":\"%s\",\"status\":%d,\"error\":\"%s\",\"message\":\"%s\",\"path\":\"%s\"}",
                LocalDateTime.now(), status.value(), status.getReasonPhrase(), message, path
        );

        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    private boolean isAuthorized(String role, String path, String method) {
        if ("MANAGER".equalsIgnoreCase(role)) {
            return path.startsWith("/employees") || path.startsWith("/leave/approve")
                    || path.startsWith("/leave/history") || path.startsWith("/leave/reject")
                    || path.startsWith("/shifts/approveSwap") || path.startsWith("/shifts/rejectSwap")
                    || path.startsWith("/shifts/findall") || path.startsWith("/shifts/save")
                    || path.startsWith("/shifts/delete") || path.startsWith("/shifts/findById");
        } else if ("EMPLOYEE".equalsIgnoreCase(role)) {
            return path.startsWith("/attendance/clockin") || path.startsWith("/attendance/clockout")
                    || path.startsWith("/leave/apply") || path.startsWith("/leave/balance")
                    || path.startsWith("/leave/history/Pending") || path.startsWith("/leave/delete")
                    || path.startsWith("/attendance/history") || path.startsWith("/shifts/requestSwap");
        }
        return false;
    }
}

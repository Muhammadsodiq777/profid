package com.profid.profid.utils.v1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

@Component
public class GenericWebClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenericWebClient.class);

    private final WebClient.Builder webClientBuilder;

    public GenericWebClient() {
        this.webClientBuilder = WebClient.builder()
                .defaultHeaders(headers -> headers.add("Content-Type", "application/json"));
    }

    public <T> Mono<T> get(String url, Map<String, String> headers, ParameterizedTypeReference<T> responseType, long timeoutSeconds) {
        WebClient webClient = webClientBuilder.baseUrl(url).build();

        return webClient.get()
                .headers(httpHeaders -> headers.forEach(httpHeaders::add))
                .retrieve()
                .bodyToMono(responseType)
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .doOnSuccess(response -> LOGGER.info("GET request successful for URL: {}", url))
                .doOnError(WebClientResponseException.class, ex -> {
                    LOGGER.error("Error in GET request to URL: {}. Status Code: {}, Response Body: {}",
                            url, ex.getStatusCode(), ex.getResponseBodyAsString(), ex);
                })
                .doOnError(ex -> LOGGER.error("Unexpected error during GET request to URL: {}", url, ex));
    }

    public <T, R> Mono<T> post(String url, Map<String, String> headers, R requestBody, Class<T> responseType, long timeoutSeconds) {
        if (url == null || url.isEmpty()) {
            return Mono.error(new IllegalArgumentException("URL cannot be null or empty"));
        }

        WebClient webClient = webClientBuilder.baseUrl(url).build();

        return webClient.post()
                .headers(httpHeaders -> headers.forEach(httpHeaders::add))
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(responseType)
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .doOnSuccess(response -> LOGGER.info("POST request successful for URL: {}", url))
                .doOnError(ex -> LOGGER.error("Error during POST request to URL: {}", url, ex));
    }
}
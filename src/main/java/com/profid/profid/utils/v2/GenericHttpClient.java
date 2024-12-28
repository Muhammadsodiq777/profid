package com.profid.profid.utils.v2;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class GenericHttpClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenericHttpClient.class);

    private final CloseableHttpClient httpClient;
    private final ObjectMapper objectMapper;

    public GenericHttpClient() {
        this.httpClient = HttpClients.custom().build();
        this.objectMapper = new ObjectMapper();

        LOGGER.info("GenericHttpClient initialized");
    }

    public <T> T get(String url, Map<String, String> headers, TypeReference<T> responseType, int timeoutInSeconds) throws IOException {
        LOGGER.info("Executing GET request to URL: {}", url);

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofSeconds(timeoutInSeconds))
                .setResponseTimeout(Timeout.ofSeconds(timeoutInSeconds))
                .build();

        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);
        headers.forEach(httpGet::addHeader);

        HttpClientResponseHandler<T> responseHandler = response -> {
            int statusCode = response.getCode();
            String responseBody = EntityUtils.toString(response.getEntity());

            LOGGER.info("GET request to URL: {} returned status code: {}", url, statusCode);

            if (statusCode >= 200 && statusCode < 300) {
                return objectMapper.readValue(responseBody, responseType);
            } else {
                LOGGER.error("GET request failed with status code: {} and response: {}", statusCode, responseBody);
                throw new IOException("GET request failed with status code: " + statusCode);
            }
        };

        try {
            return httpClient.execute(httpGet, responseHandler);
        } catch (IOException e) {
            LOGGER.error("Error executing GET request to URL: {}", url, e);
            throw e;
        }
    }


    public <T, R> T post(String url, Map<String, String> headers, R requestBody, Class<T> responseType, int timeoutInSeconds) throws IOException {
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("URL cannot be null or empty");
        }

        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(buildRequestConfig(timeoutInSeconds));
        headers.forEach(httpPost::addHeader);

        if (requestBody != null) {
            String requestJson = objectMapper.writeValueAsString(requestBody);
            httpPost.setEntity(new org.apache.hc.core5.http.io.entity.StringEntity(requestJson, org.apache.hc.core5.http.ContentType.APPLICATION_JSON));
            LOGGER.debug("POST request body: {}", requestJson);
        }

        HttpClientResponseHandler<T> responseHandler = response -> {
            int statusCode = response.getCode();
            String responseBody = EntityUtils.toString(response.getEntity());

            if (statusCode >= 200 && statusCode < 300) {
                return objectMapper.readValue(responseBody, responseType);
            } else {
                LOGGER.error("POST request failed with status code: {} and response: {}", statusCode, responseBody);
                throw new IOException("POST request failed with status code: " + statusCode);
            }
        };

        return httpClient.execute(httpPost, responseHandler);
    }

    private RequestConfig buildRequestConfig(int timeoutInSeconds) {
        return RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofSeconds(timeoutInSeconds))
                .setResponseTimeout(Timeout.ofSeconds(timeoutInSeconds))
                .build();
    }
}
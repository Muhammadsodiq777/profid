RESTful API Integration with Third-Party Vendor Service
Integration Overview
This project implements a RESTful API using Java Spring Boot that integrates with a third-party vendor service for fetching and storing data in a PostgreSQL database. The integration handles two main operations:

GET Endpoint: Fetches data from the vendor's API.
POST Endpoint: Pushes data to the vendor's API after transforming the data.
Integration Details
The API communicates with the third-party service using the WebClient class from Spring WebFlux for asynchronous communication.
The responses from the external API are transformed into Java objects (DTOs) and stored in the PostgreSQL database using Spring Data JPA.
The GenericWebClient class is used to handle the communication with the external API, providing reusable methods for GET and POST requests.
Error Handling
The application includes centralized error handling through @ControllerAdvice and custom exception handlers. This ensures that errors during API communication are caught and handled appropriately.

API Error Handling: If the external API responds with an error (such as a 4xx or 5xx status code), the error is logged, and the appropriate response is returned to the client.
Timeout Handling: A custom handler catches timeout exceptions, providing an informative message that the request has timed out.
Generic Error Handling: Any unexpected errors are caught, and a generic error message is returned to the client, ensuring that sensitive information is not exposed.
Exception Handlers
WebClientResponseException: Handles errors from the external API, such as HTTP status codes 4xx or 5xx.
TimeoutException: Handles timeouts in API requests and returns a specific timeout message to the client.
Generic Exception: Catches any other unexpected errors and returns a generic error response with HTTP status 500.
Example:

Copy code
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<String> handleApiException(WebClientResponseException ex) {
        return ResponseEntity.status(ex.getRawStatusCode()).body("API error: " + ex.getMessage());
    }

    @ExceptionHandler(TimeoutException.class)
    public ResponseEntity<String> handleTimeoutException(TimeoutException ex) {
        return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body("Request timed out: " + ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
    }
}
Optimizations
Several optimizations have been applied to improve performance, reliability, and maintainability:

Asynchronous Communication: The application uses WebClient for non-blocking, asynchronous HTTP requests, ensuring the API is responsive even under heavy loads.
Timeout Handling: Timeouts are configured for both connection and response, allowing the application to quickly fail and avoid hanging indefinitely in case of external API delays.
Data Transformation: The data received from the external API is automatically transformed into DTOs (Data Transfer Objects) using Jackson's ObjectMapper for easy manipulation and storage in PostgreSQL.
Logging: Detailed logging is enabled for both successful requests and errors, making it easier to trace API calls and debug issues.
Example:

java
Copy code
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
Unit Testing
Unit tests are included for key components of the integration:

API Communication: Tests verify that the GenericWebClient correctly handles both successful responses and errors.
Service Layer: Unit tests validate the logic for transforming the API responses and saving them to the database.
Error Handling: Tests ensure that custom exceptions are thrown and handled as expected.
Security Considerations
Headers and Authentication: When making requests to the external API, headers can be added to include authorization tokens or other security measures. This can be extended based on the third-party vendor's authentication requirements.
Data Validation: Input data is validated using Java's validation annotations (e.g., @NotNull, @Size, etc.) to prevent invalid data from being processed or sent to the external API.

package org.swd392.apigateway.filter;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.util.List;

@Component
public class FilterUtility {

    public static final String CORRELATION_ID_HEADER = "Swd391-Correlation-Id";

    /**
     * Retrieves the correlation ID from the request headers.
     *
     * @param requestHeaders the HTTP headers of the request
     * @return the correlation ID if present, otherwise null
     */
    public String getCorrelationId(HttpHeaders requestHeaders) {
        if (requestHeaders.get(CORRELATION_ID_HEADER) != null) {
            List<String> requestHeaderList = requestHeaders.get(CORRELATION_ID_HEADER);
            return requestHeaderList.stream().findFirst().get();
        } else return null;
    }

    /**
     * Sets the correlation ID in the request headers of the given ServerWebExchange.
     *
     * @param exchange the ServerWebExchange to modify
     * @param name     the correlation ID to set
     * @param value    the value of the correlation ID
     * @return a new ServerWebExchange with the updated request headers
     */
    public ServerWebExchange setRequestHeader(ServerWebExchange exchange, String name, String value) {

        ServerHttpRequest currentExchange = exchange.getRequest().mutate().build();

        // add updates the specified header
        currentExchange = currentExchange.mutate()
                .header(name, value)
                .build();

        // create a new ServerWebExchange with the updated request
        ServerWebExchange newExchange = exchange.mutate().request(currentExchange).build();
        return newExchange;
    }


    public ServerWebExchange setCorrelationId(ServerWebExchange exchange, String correlationId) {
        return this.setRequestHeader(exchange, CORRELATION_ID_HEADER, correlationId);
    }
}

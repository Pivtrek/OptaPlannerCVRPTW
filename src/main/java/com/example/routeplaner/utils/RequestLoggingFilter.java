package com.example.routeplaner.utils;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RequestLoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, jakarta.servlet.ServletException {
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;

            // Logowanie szczegółów zapytania
            System.out.println("=== HTTP Request ===");
            System.out.println("Method: " + httpRequest.getMethod());
            System.out.println("URI: " + httpRequest.getRequestURI());
            System.out.println("Headers: ");
            httpRequest.getHeaderNames().asIterator().forEachRemaining(header ->
                    System.out.println(header + ": " + httpRequest.getHeader(header))
            );
        }
        chain.doFilter(request, response);
    }
}

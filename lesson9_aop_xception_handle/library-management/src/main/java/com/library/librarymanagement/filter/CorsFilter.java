package com.library.librarymanagement.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CorsFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("[FILTER] CorsFilter initialized");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Allow all origins (cho development, production nên specify domain cụ thể)
        httpResponse.setHeader("Access-Control-Allow-Origin", "*");

        // Allow methods
        httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");

        // Allow headers
        httpResponse.setHeader("Access-Control-Allow-Headers",
                "Content-Type, Authorization, X-Requested-With");

        // Allow credentials
        httpResponse.setHeader("Access-Control-Allow-Credentials", "true");

        // Max age for preflight
        httpResponse.setHeader("Access-Control-Max-Age", "3600");

        // Handle preflight OPTIONS request
        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        System.out.println("[FILTER] CorsFilter destroyed");
    }
}

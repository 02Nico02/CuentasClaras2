package com.grupo7.cuentasclaras2.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.lang.Nullable;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Logger;

public class AccessLogInterceptor implements HandlerInterceptor {

    private static final Logger logger = Logger.getLogger(AccessLogInterceptor.class.getName());

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String remoteAddr = request.getRemoteAddr();
        String method = request.getMethod();
        String uri = request.getRequestURI();
        long currentTimeMillis = System.currentTimeMillis();

        String logEntry = String.format("%s - - [%s] \"%s %s\"",
                remoteAddr, formatDateTime(currentTimeMillis), method, uri);

        logger.info(logEntry);

        return true;
    }

    private String formatDateTime(long currentTimeMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z", Locale.US);
        return dateFormat.format(new Date(currentTimeMillis));
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
            @Nullable Exception ex) {
        String remoteAddr = request.getRemoteAddr();
        String method = request.getMethod();
        String uri = request.getRequestURI();
        int statusCode = response.getStatus();
        long currentTimeMillis = System.currentTimeMillis();

        String logEntry = String.format("%s - - [%s] \"%s %s\" %d",
                remoteAddr, formatDateTime(currentTimeMillis), method, uri, statusCode);

        logger.info(logEntry);
    }

}

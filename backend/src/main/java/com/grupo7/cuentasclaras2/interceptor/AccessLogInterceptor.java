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

    /**
     * Registra información de acceso antes de que se maneje la solicitud.
     *
     * @param request  La solicitud HTTP recibida.
     * @param response La respuesta HTTP que se enviará.
     * @param handler  El objeto manejador que procesará la solicitud.
     * @return `true` si el procesamiento de la solicitud debe continuar, `false` de
     *         lo contrario.
     */
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

    /**
     * Registra información de acceso después de que se ha completado la
     * manipulación de la solicitud.
     *
     * @param request  La solicitud HTTP recibida.
     * @param response La respuesta HTTP que se ha enviado.
     * @param handler  El objeto manejador que ha procesado la solicitud.
     * @param ex       Excepción que puede haber ocurrido durante el manejo de la
     *                 solicitud (puede ser nula).
     */
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

    /**
     * Formatea una fecha en el formato específico del registro.
     *
     * @param currentTimeMillis La fecha en milisegundos.
     * @return La cadena formateada de la fecha y hora.
     */
    private String formatDateTime(long currentTimeMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z", Locale.US);
        return dateFormat.format(new Date(currentTimeMillis));
    }

}

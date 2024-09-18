package com.grupo7.cuentasclaras2.services;

import java.util.HashSet;
import java.util.Set;

/**
 * Clase para manejar la lista negra de tokens.
 */
public class TokenBlacklist {
    private static Set<String> blacklistedTokens = new HashSet<>();

    /**
     * Agrega un token a la lista negra.
     *
     * @param token El token a agregar a la lista negra.
     */
    public static void blacklistToken(String token) {
        blacklistedTokens.add(token);
    }

    /**
     * Verifica si un token está en la lista negra.
     *
     * @param token El token a verificar.
     * @return true si el token está en la lista negra, false de lo contrario.
     */
    public static boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }
}

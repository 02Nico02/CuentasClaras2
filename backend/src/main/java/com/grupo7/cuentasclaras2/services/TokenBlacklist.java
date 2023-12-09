package com.grupo7.cuentasclaras2.services;

import java.util.HashSet;
import java.util.Set;

public class TokenBlacklist {
    private static Set<String> blacklistedTokens = new HashSet<>();

    public static void blacklistToken(String token) {
        blacklistedTokens.add(token);
    }

    public static boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }
}

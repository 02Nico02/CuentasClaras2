package com.grupo7.cuentasclaras2.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Service;

/**
 * Servicio para la gestión de tokens JWT.
 */
@Service
public class TokenServices {
    private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    /**
     * Genera un token de autorización para el usuario.
     *
     * @param username Nombre de usuario que se guarda dentro del token.
     * @param segundos Tiempo de validez del token en segundos.
     * @return Token JWT.
     */
    public String generateToken(String username, int segundos) {
        Date exp = getExpiration(new Date(), segundos);
        return Jwts.builder().setSubject(username).signWith(key).setExpiration(exp).compact();
    }

    /**
     * Obtiene la fecha de vencimiento del token a partir de la fecha de inicio y la
     * duración en segundos.
     *
     * @param startDate Fecha de inicio del token.
     * @param segundos  Tiempo de validez del token en segundos.
     * @return Fecha de vencimiento del token.
     */
    private Date getExpiration(Date startDate, int segundos) {
        return new Date(startDate.getTime() + segundos * 1000);
    }

    /**
     * Valida la autenticidad y la vigencia de un token.
     *
     * @param token El token a validar.
     * @return true si el token es válido, false de lo contrario.
     */
    public static boolean validateToken(String token) {
        String prefix = "Bearer";
        try {
            if (token.startsWith(prefix)) {
                System.out.println("Entré en la linea 55");
                token = token.substring(prefix.length()).trim();
            }
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
            return true;
        } catch (ExpiredJwtException exp) {
            return false;
        } catch (JwtException e) {

            return false;
        }
    }

    /**
     * Obtiene el nombre de usuario desde un token JWT.
     *
     * @param token El token JWT.
     * @return El nombre de usuario.
     * @throws JwtException Si hay un error al obtener el nombre de usuario desde el
     *                      token.
     */
    public static String getUsernameFromToken(String token) {
        String prefix = "Bearer";
        try {
            if (token.startsWith(prefix)) {
                token = token.substring(prefix.length()).trim();
            }
            Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
            return claims.getSubject();
        } catch (ExpiredJwtException exp) {
            throw new JwtException("Error al obtener username del token", exp);
        } catch (JwtException e) {
            throw new JwtException("Error al obtener username del token", e);
        }
    }
}

package com.pos.proiect.playlists.util;


import com.pos.proiect.playlists.exception.AccessForbiddenException;
import com.pos.proiect.playlists.exception.JwsExpiredException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwsUtil implements Serializable {

    public static final String USER_ID_CLAIM = "sub";
    public static final String USER_NAME_CLAIM = "name";
    public static final String USER_ROLES_CLAIM = "roles";

    @Value("${jwt.secret}")
    private String secret;

    public Integer getUserIdFromToken(String token) {
        final Map<String, Object> claims = getAllClaimsFromToken(token);
        String userIdString = claims.get(USER_ID_CLAIM).toString();
        return Integer.parseInt(userIdString);
    }

    public String getUserNameFromToken(String token) {
        final Map<String, Object> claims = getAllClaimsFromToken(token);
        return claims.get(USER_NAME_CLAIM).toString();
    }

    public List<String> getUserRolesFromToken(String token) {
        final Map<String, Object> claims = getAllClaimsFromToken(token);
        List<String> userRoles = (ArrayList<String>) claims.get(USER_ROLES_CLAIM);
        return userRoles;
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret.getBytes()).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) throws JwsExpiredException {
        final Date expiration = getExpirationDateFromToken(token);
        if(expiration.before(new Date())) throw new JwsExpiredException();
        return false;
    }

    public Boolean validateToken(String token, Integer userId) throws JwsExpiredException, AccessForbiddenException {
        try {
            final Integer id = getUserIdFromToken(token);
            if(!id.equals(userId)) throw new AccessForbiddenException();
            return !isTokenExpired(token);
        } catch (ExpiredJwtException ex) {
            throw new JwsExpiredException();
        }
    }
}

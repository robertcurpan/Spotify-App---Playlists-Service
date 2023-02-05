package com.pos.proiect.playlists.service;


import com.pos.proiect.playlists.exception.*;
import com.pos.proiect.playlists.util.JwsUtil;
import com.pos.proiect.playlists.model.RolesEnum;
import com.pos.proiect.playlists.model.UserAndRoles;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class AuthorizationService {

    @Autowired
    private JwsUtil jwsUtil;


    public UserAndRoles validateTokenAndReturnUserAndRoles(String token, Integer userId) throws JwsSignatureNotValidException, JwsFormatNotValidException, JwsTokenCouldNotBeValidatedException, JwsExpiredException, AccessForbiddenException {
        try {
            boolean isTokenValid = jwsUtil.validateToken(token, userId);
            if(isTokenValid) {
                String userName = jwsUtil.getUserNameFromToken(token);
                List<String> userRoles = jwsUtil.getUserRolesFromToken(token);
                return new UserAndRoles(userId, userName, userRoles);
            }
        } catch (SignatureException ex) {
            throw new JwsSignatureNotValidException();
        } catch (MalformedJwtException ex) {
            throw new JwsFormatNotValidException();
        }

        throw new JwsTokenCouldNotBeValidatedException();
    }

    public boolean authorizeRole(UserAndRoles userAndRoles, RolesEnum requiredRole) {
        for(String role : userAndRoles.getUserRoles()) {
            if(role.equals(requiredRole.getRole())) {
                return true;
            }
        }

        return false;
    }

    public String getJwsFromRequest(HttpServletRequest request) throws AuthorizationHeaderMissingException {
        String authorizationHeader = request.getHeader("Authorization");
        if(authorizationHeader == null) throw new AuthorizationHeaderMissingException();
        String jws = authorizationHeader.substring(7);
        return jws;
    }
}

package org.wierzbadev.scoreservice.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class TokenConvert {

    public long getLongIdFromToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("no logged user");
        }

        if (authentication.getPrincipal() instanceof Jwt jwt) {
            Object userIdObj = jwt.getClaim("userId");

            if (userIdObj instanceof Long) {
                return (long) userIdObj;
            } else if (userIdObj instanceof String) {
                try {
                    return Long.parseLong((String) userIdObj);
                } catch (NumberFormatException e) {
                    throw new RuntimeException("Invalid user ID type in token!" + e, (e));
                }
            }
        }
        throw new RuntimeException("Invalid token format");
    }
}

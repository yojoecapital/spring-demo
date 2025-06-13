package com.learning.sprintsecurity.config;

import java.util.List;
import java.util.function.Supplier;

import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ScopedAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {
    private static final String SCOPE_PREFIX = "SCOPE_";
    private final String resource;

    private List<String> getScopes(Authentication authentication) {
        return authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .filter(auth -> auth.startsWith(SCOPE_PREFIX))
            .map(auth -> auth.substring(SCOPE_PREFIX.length()))
            .toList();
    }

    private String getBusinessFromJwt(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof Jwt jwt) {
            Object claimsObj = jwt.getClaim("claims");
            if (claimsObj instanceof String claimsStr) {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    var claimsJson = mapper.readTree(claimsStr);
                    var businessNode = claimsJson.get("business");
                    return businessNode != null ? businessNode.asText() : null;
                } catch (Exception e) {
                    return null;
                }
            }
        }
        return null;
    }

    public boolean hasPermissionFor(
        Authentication authentication, 
        String resource, String permission, String business
    ) {
        String targetScope = String.format("%s:%s", permission, resource);
        return getScopes(authentication).contains(targetScope) && 
               getBusinessFromJwt(authentication).equals(business);
    }

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext context) {
        HttpServletRequest request = context.getRequest();
        String method = request.getMethod();
        String permission = switch (method) {
            case "GET" -> "read";
            case "POST", "PUT", "DELETE" -> "write";
            default -> null;
        };
        if (permission == null) return new AuthorizationDecision(false);
        Authentication auth = authentication.get();
        String business = request.getParameter("business");
        if (business == null || business.isEmpty()) return new AuthorizationDecision(false);
        return new AuthorizationDecision(hasPermissionFor(auth, resource, permission, business));
    }
}

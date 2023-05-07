package com.doublel401.java.bff.filters;

import com.doublel401.java.bff.constant.AppConstants;
import com.doublel401.java.bff.entity.User;
import com.doublel401.java.bff.enums.RequestMethodEnum;
import com.doublel401.java.bff.exception.CustomAuthenticationEntryPoint;
import com.doublel401.java.bff.service.UserService;
import com.doublel401.java.bff.utils.TokenUtils;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@Slf4j
public class CustomAuthorizationFilter extends OncePerRequestFilter {
    private static final String TAG = "CustomAuthorizationFilter";

    private final UserService userService;
    private final TokenUtils tokenUtils;
    private final CustomAuthenticationEntryPoint authEntryPoint;

    private final String signInUrl;
    private String path;

    public CustomAuthorizationFilter(UserService userService, TokenUtils tokenUtils,
                                     CustomAuthenticationEntryPoint authEntryPoint, String signInUrl) {
        this.userService = userService;
        this.tokenUtils = tokenUtils;
        this.authEntryPoint = authEntryPoint;
        this.signInUrl = signInUrl;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException
    {
        log.info("{} | DO_FILTER_INTERNAL | ... Is Authentication required?", TAG);

        // Set path uri for returning authentication and authorization error
        path = request.getRequestURI();
        // Get bearer token from header authorization
        String bearerToken = request.getHeader(AppConstants.AUTHORIZATION_HEADER);
        // Get user from bearer token
        String username = getUserFromBearerToken(bearerToken);
        User user = (User) userService.loadUserByUsername(username);
        // If user not null, process authorization or call next filter in the chain
        if (Objects.nonNull(user)) {
            // Check user has permission for the resource
            boolean hasPermission = user.getRole().getPermissions().stream()
                    .anyMatch(permission -> request.getRequestURI().matches(permission.getResource())
                            && RequestMethodEnum.getByAction(permission.getAction())
                            == RequestMethodEnum.getByMethod(request.getMethod()));
            if (!hasPermission) {
                updateAuthorizationError("User does not have permission to access resource.");
            } else {
                // Set authenticated user to security context
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(username, null, user.getAuthorities());
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(authentication);
                SecurityContextHolder.setContext(context);

                log.info("{} | Successfully Authorized!", TAG);
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Helper method for get username for bearer token
     *
     * @param bearerToken bearer token
     * @return String
     */
    private String getUserFromBearerToken(String bearerToken) {
        if (StringUtils.isBlank(bearerToken) || !StringUtils.startsWith(bearerToken, AppConstants.BEARER_PREFIX)) {
            log.info("{} | Authorization Bearer (JWT) not provided or invalid.", TAG);
            updateAuthenticationError("Authorization Bearer (JWT) not provided or invalid.");
            return null;
        }

        try {
            String token = StringUtils.removeStart(bearerToken, AppConstants.BEARER_PREFIX).trim();
            return tokenUtils.getUsernameFromAccessToken(token);
        } catch (ExpiredJwtException e) {
            log.info("{} | JWT token has expired.", TAG);
            updateAuthenticationError("JWT token has expired.");
        } catch (Exception e) {
            log.error("{} | Error when processing JWT token.", TAG, e);
            updateAuthenticationError("Error when processing JWT token.");
        }

        return null;
    }

    /**
     * Update authorization error
     *
     * @param message error message
     */
    private void updateAuthorizationError(String message) {
        this.authEntryPoint.setStatus(HttpStatus.FORBIDDEN.value());
        this.authEntryPoint.setError(HttpStatus.FORBIDDEN.getReasonPhrase());
        this.authEntryPoint.setAuthorizeMessage(message);
        this.authEntryPoint.setPath(path);
    }

    /**
     * Update authentication error
     *
     * @param message error message
     */
    private void updateAuthenticationError(String message) {
        this.authEntryPoint.initialValues();
        this.authEntryPoint.setAuthenticateMessage(message);
        this.authEntryPoint.setPath(path);
    }
}

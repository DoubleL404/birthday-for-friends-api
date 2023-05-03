package com.doublel401.java.bff.filters;

import com.doublel401.java.bff.entity.RefreshToken;
import com.doublel401.java.bff.entity.User;
import com.doublel401.java.bff.repository.RefreshTokenRepository;
import com.doublel401.java.bff.utils.TokenUtils;
import com.doublel401.java.bff.vo.ResponseVO;
import com.doublel401.java.bff.vo.SignInVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Slf4j
@Builder
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private static final String TAG = "CustomAuthenticationFilter";
    private static final String USERNAME_FIELD = "username";
    private static final String PASSWORD_FIELD = "password";

    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenUtils tokenUtils;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,HttpServletResponse response)
            throws AuthenticationException
    {
        /*
           Performs actual authentication.
           The implementation should do one of the following:
           1. Return a populated authentication token for the authenticated user, indicating successful authentication
           2. Return null, indicating that the authentication process is still in progress.
              Before returning, the implementation should perform any additional work required to complete the process.
           3. Throw an AuthenticationException if the authentication process fails
           So, it should return either the authenticated user token, or null if authentication is incomplete.
        */

        log.info("{} | Attempting Authentication ...", TAG);

        String username = request.getParameter(USERNAME_FIELD);
        String password = request.getParameter(PASSWORD_FIELD);

        UsernamePasswordAuthenticationToken unAuthenticationToken =
                new UsernamePasswordAuthenticationToken(username, password);

        return authenticationManager.authenticate(unAuthenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult)
            throws IOException, ServletException
    {
        /*
           Default behavior for successful authentication:
           - Sets the successful Authentication object on the SecurityContextHolder
           - Informs the configured RememberMeServices of the successful login
           - Fires an InteractiveAuthenticationSuccessEvent via the configured ApplicationEventPublisher
           - Delegates additional behavior to the AuthenticationSuccessHandler.
           Subclasses can override this method to continue the FilterChain after successful authentication.
        */

        // Get principal from authentication
        User user = (User) authResult.getPrincipal();

        // Get issuer -> the request URL
        String issuer = request.getRequestURL().toString();

        String accessToken = tokenUtils.generateAccessToken(user.getUsername(), issuer);
        RefreshToken refreshToken = tokenUtils.generateRefreshToken(user.getUsername());

        // Save refresh token of the user to db
        refreshTokenRepository.save(refreshToken);

        SignInVO signInVO = new SignInVO(accessToken, refreshToken.getToken().toString());
        ResponseVO<SignInVO> responseVO = ResponseVO.created("Successful Authentication", signInVO);

        log.info("{} | Successful Authentication!", TAG);

        // Return response to client
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), responseVO);
    }
}

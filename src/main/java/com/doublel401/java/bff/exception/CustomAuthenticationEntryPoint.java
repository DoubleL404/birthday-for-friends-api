package com.doublel401.java.bff.exception;

import com.doublel401.java.bff.utils.JsonUtils;
import com.doublel401.java.bff.vo.ErrorResponseVO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Setter
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Value("${bff.user.sign.in.url}")
    private String signInUrl;

    private int status;
    private String error;
    private String authenticateMessage;
    private String authorizeMessage;
    private String path;

    public CustomAuthenticationEntryPoint() {
        initialValues();
    }

    public void initialValues() {
        this.status = HttpStatus.UNAUTHORIZED.value();
        this.error = HttpStatus.UNAUTHORIZED.getReasonPhrase();
        this.authenticateMessage = "";
        this.authorizeMessage = "";
        this.path = signInUrl;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException
    {
        // Add authentication exception message for more details
        String message = StringUtils.isBlank(authenticateMessage)
                ? authException.getMessage() :  authenticateMessage + " - " + authException.getMessage();

        if (StringUtils.isNotBlank(authorizeMessage)) {
            message = authorizeMessage;
        }

        // Construct error response
        ErrorResponseVO errorResponse = new ErrorResponseVO(status, error, message, path);

        // Reset custom message
        authenticateMessage = StringUtils.EMPTY;

        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(JsonUtils.toJsonString(errorResponse));
    }
}

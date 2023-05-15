package com.doublel401.java.bff.config;

import com.doublel401.java.bff.exception.CustomAccessDeniedHandler;
import com.doublel401.java.bff.exception.CustomAuthenticationEntryPoint;
import com.doublel401.java.bff.filters.CustomAuthenticationFilter;
import com.doublel401.java.bff.filters.CustomAuthorizationFilter;
import com.doublel401.java.bff.repository.RefreshTokenRepository;
import com.doublel401.java.bff.service.UserService;
import com.doublel401.java.bff.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationEntryPointFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private static final String AUTH_URL_PATTERN = "/api/auth/**";

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final TokenUtils tokenUtils;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CustomAuthenticationEntryPoint authEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    @Value("${bff.user.sign.in.url}")
    private String signInUrl;

    public SecurityConfig(PasswordEncoder passwordEncoder, UserService userService,
                          TokenUtils tokenUtils, RefreshTokenRepository refreshTokenRepository, CustomAuthenticationEntryPoint authEntryPoint, CustomAccessDeniedHandler accessDeniedHandler)
    {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.tokenUtils = tokenUtils;
        this.refreshTokenRepository = refreshTokenRepository;
        this.authEntryPoint = authEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling().authenticationEntryPoint(authEntryPoint)
                .and()
                .exceptionHandling().accessDeniedHandler(accessDeniedHandler)
                .and()
                .authorizeHttpRequests().requestMatchers(HttpMethod.POST, AUTH_URL_PATTERN).permitAll()
                .anyRequest().authenticated();

        // Add filters to security filter chain
        http.addFilter(customAuthenticationFilter());
        http.addFilterBefore(customAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        authenticationProvider.setUserDetailsService(userService);
        return authenticationProvider;
    }

    private CustomAuthenticationFilter customAuthenticationFilter() {
        CustomAuthenticationFilter authenticationFilter = new CustomAuthenticationFilter(new ProviderManager(authenticationProvider()));
        authenticationFilter.setTokenUtils(tokenUtils);
        authenticationFilter.setRefreshTokenRepository(refreshTokenRepository);
        authenticationFilter.setAuthEntryPoint(authEntryPoint);
        authenticationFilter.setAuthenticationFailureHandler(new AuthenticationEntryPointFailureHandler(authEntryPoint));
        // Customize sign-in endpoint
        authenticationFilter.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(signInUrl, "POST"));

        return authenticationFilter;
    }

    private CustomAuthorizationFilter customAuthorizationFilter() {
        return new CustomAuthorizationFilter(userService, tokenUtils, authEntryPoint, signInUrl);
    }
}

package com.doublel401.java.bff.config;

import com.doublel401.java.bff.filters.CustomAuthenticationFilter;
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
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String AUTH_URL_PATTERN = "/api/auth/**";

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final TokenUtils tokenUtils;
    private final RefreshTokenRepository refreshTokenRepository;


    @Value("${bff.user.sign.in.url}")
    private String signInUrl;

    public SecurityConfig(PasswordEncoder passwordEncoder, UserService userService,
                          TokenUtils tokenUtils, RefreshTokenRepository refreshTokenRepository)
    {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.tokenUtils = tokenUtils;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests().requestMatchers(HttpMethod.POST, AUTH_URL_PATTERN).permitAll()
                .anyRequest().authenticated();

        http.addFilter(customAuthenticationFilter());


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
        CustomAuthenticationFilter customAuthenticationFilter = CustomAuthenticationFilter.builder()
                .authenticationManager(new ProviderManager(authenticationProvider()))
                .tokenUtils(tokenUtils)
                .refreshTokenRepository(refreshTokenRepository)
                .build();

        // Customize sign-in endpoint
        customAuthenticationFilter.setRequiresAuthenticationRequestMatcher(
                new AntPathRequestMatcher(signInUrl, "POST"));

        return customAuthenticationFilter;
    }
}

package com.teknisio.config;

import com.teknisio.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
      .csrf(AbstractHttpConfigurer::disable)
      .httpBasic(AbstractHttpConfigurer::disable)
      .formLogin(AbstractHttpConfigurer::disable)
      .sessionManagement(session -> session
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      )
      .exceptionHandling(exception -> exception
        .authenticationEntryPoint((request, response, authException) ->
          writeErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")
        )
        .accessDeniedHandler((request, response, accessDeniedException) ->
          writeErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "Forbidden")
        )
      )
      .authorizeHttpRequests(auth -> auth
        // Public auth endpoints
        .requestMatchers(HttpMethod.POST, "/api/auth/register/customer").permitAll()
        .requestMatchers(HttpMethod.POST, "/api/auth/register/technician").permitAll()
        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
        .requestMatchers(HttpMethod.POST, "/api/auth/refresh").permitAll()

        // Public endpoints if needed
        .requestMatchers("/api/public/**").permitAll()
        .requestMatchers("/actuator/health").permitAll()

        // Authenticated auth endpoints
        .requestMatchers(HttpMethod.GET, "/api/auth/profile").authenticated()
        .requestMatchers(HttpMethod.POST, "/api/auth/logout").authenticated()

        // Role-based endpoints
        .requestMatchers("/api/customers/**").hasRole("CUSTOMER")
        .requestMatchers("/api/technicians/**").hasRole("TECHNICIAN")
        .requestMatchers("/api/admin/**").hasRole("ADMIN")

        // Default: all other endpoints require authentication
        .anyRequest().authenticated()
      )
      .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
      .build();
  }

  private void writeErrorResponse(
    HttpServletResponse response,
    int status,
    String message
  )
  throws IOException {
    response.setStatus(status);
    response.setContentType("application/json");
    response.getWriter().write(
      "{\"success\":false,\"message\":\"" + message + "\",\"data\":null,\"errors\":{}}"
    );
  }
}

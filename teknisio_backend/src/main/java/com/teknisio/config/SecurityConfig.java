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
        .authenticationEntryPoint((request, response, authException) -> {
          response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
          response.setContentType("application/json");
          response.getWriter().write(
            "{\"success\":false,\"message\":\"Unauthorized\",\"errors\":{}}"
          );
        })
        .accessDeniedHandler((request, response, accessDeniedException) -> {
          response.setStatus(HttpServletResponse.SC_FORBIDDEN);
          response.setContentType("application/json");
          response.getWriter().write(
            "{\"success\":false,\"message\":\"Forbidden\",\"errors\":{}}"
          );
        })
      )
      .authorizeHttpRequests(auth -> auth
        // Public auth endpoints
        .requestMatchers(HttpMethod.POST, "/api/auth/register/customer").permitAll()
        .requestMatchers(HttpMethod.POST, "/api/auth/register/teknisi").permitAll()
        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
        .requestMatchers(HttpMethod.POST, "/api/auth/refresh").permitAll()

        // Public endpoints jika diperlukan
        .requestMatchers("/api/public/**").permitAll()
        .requestMatchers("/actuator/health").permitAll()

        // Authenticated profile
        .requestMatchers(HttpMethod.GET, "/api/auth/profile").authenticated()

        // Role-based endpoints
        .requestMatchers("/api/customer/**").hasRole("CUSTOMER")
        .requestMatchers("/api/teknisi/**").hasRole("TEKNISI")
        .requestMatchers("/api/admin/**").hasRole("ADMIN")

        // Default: endpoint lain wajib login
        .anyRequest().authenticated()
      )
      .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
      .build();
  }
}

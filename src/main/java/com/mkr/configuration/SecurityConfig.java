
package com.mkr.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.mkr.security.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
            // Disable CSRF for stateless REST API
            .csrf(csrf -> csrf.disable())

            // Disable default browser-based authentication
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())

            // Configure endpoint access
            .authorizeHttpRequests(auth -> auth

                // Public login and registration
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/users/register").permitAll()
                .requestMatchers(HttpMethod.POST, "/recruiters/register").permitAll()

                // Public job browsing
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/jobs/getAllJobs",
                    "/api/jobs/getOpenJobs",
                    "/api/jobs/filter"
                ).permitAll()

                // Admin-only user management APIs
                .requestMatchers(
                    HttpMethod.GET,
                    "/users",
                    "/users/getById/**",
                    "/users/search",
                    "/users/role/**",
                    "/users/search/filter"
                ).hasRole("ADMIN")

                // Admin-only endpoints
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers(
                    "/recruiters/approve/**",
                    "/recruiters/reject/**",
                    "/recruiters/status/**"
                ).hasRole("ADMIN")

                // Job Seeker's own profile APIs
                .requestMatchers(
                    "/users/me",
                    "/users/me/**"
                ).hasRole("JOB_SEEKER")

                // Recruiter job management
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/jobs/createJob"
                ).hasRole("RECRUITER")

                .requestMatchers(
                    HttpMethod.PUT,
                    "/api/jobs/updateJob/**"
                ).hasRole("RECRUITER")

                .requestMatchers(
                    HttpMethod.DELETE,
                    "/api/jobs/deleteJob/**"
                ).hasRole("RECRUITER")

                .requestMatchers(
                    HttpMethod.PATCH,
                    "/api/jobs/closeJob/**",
                    "/api/jobs/reopenJob/**"
                ).hasRole("RECRUITER")

                // Job Seeker application operations
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/applications/apply/**"
                ).hasRole("JOB_SEEKER")

                .requestMatchers(
                    HttpMethod.GET,
                    "/api/applications/my"
                ).hasRole("JOB_SEEKER")

                .requestMatchers(
                    HttpMethod.PATCH,
                    "/api/applications/*/withdraw"
                ).hasRole("JOB_SEEKER")

                // Recruiter application operations
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/applications/job/**"
                ).hasRole("RECRUITER")

                .requestMatchers(
                    HttpMethod.PATCH,
                    "/api/applications/*/status"
                ).hasRole("RECRUITER")
                
                // Recruiter DashBoard 
                .requestMatchers("/api/recruiter/dashboard").hasRole("RECRUITER")
                
                // Job Seeker DashBoard
                .requestMatchers("/api/job-seeker/dashboard").hasRole("JOB_SEEKER")

                // Job Seeker and Recruiter can view individual applications
                // Service layer validates ownership
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/applications/*"
                ).hasAnyRole("JOB_SEEKER", "RECRUITER")
                
                
             // Allows public access to Swagger UI and OpenAPI documentation without requiring JWT authentication.
                .requestMatchers(
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v3/api-docs/**"
                ).permitAll()

                // All remaining endpoints require authentication
                .anyRequest().authenticated()
            )

            // Stateless JWT configuration
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // Add JWT filter before UsernamePasswordAuthenticationFilter
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}

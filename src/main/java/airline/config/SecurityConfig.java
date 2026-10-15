package airline.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import airline.entity.enums.UserRole;

@Configuration
public class SecurityConfig {

  private String FLIGHT = "/api/flights";
  private String USER = "/api/users";
  private String BOOKING = "/api/bookings";
  private String ADMIN = "ADMIN";

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/error").permitAll()
                // Flight
                .requestMatchers(HttpMethod.GET, FLIGHT + "/**").permitAll() //flightNumber, search
                .requestMatchers(HttpMethod.POST, FLIGHT).hasRole(ADMIN)
                .requestMatchers(HttpMethod.PUT, FLIGHT + "/**").hasRole(ADMIN)
                .requestMatchers(HttpMethod.DELETE, FLIGHT + "/**").hasRole(ADMIN)
                // User
                .requestMatchers(HttpMethod.POST, USER).permitAll()
                .requestMatchers(HttpMethod.GET, USER).hasRole(ADMIN)
                .requestMatchers(HttpMethod.GET, USER + "/search").hasRole(ADMIN)
                // Booking
                .requestMatchers(HttpMethod.GET, BOOKING).hasRole(ADMIN)
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults())
            .formLogin(Customizer.withDefaults());
        return http.build();
    }
    
}
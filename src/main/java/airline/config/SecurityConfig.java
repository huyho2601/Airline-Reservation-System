package airline.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableMethodSecurity 
public class SecurityConfig {

  private static final String FLIGHT = "/api/flights";
  private static final String USER = "/api/users";
  private static final String BOOKING = "/api/bookings";
  private static final String ADMIN = "ADMIN";

    @Bean @Order(1)
    SecurityFilterChain apiChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/api/**")
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Flight
                        .requestMatchers(HttpMethod.GET, FLIGHT + "/**").permitAll() //flightNumber, search, seats on that flights
                        .requestMatchers(HttpMethod.POST, FLIGHT).hasRole(ADMIN)
                        .requestMatchers(HttpMethod.PUT, FLIGHT + "/**").hasRole(ADMIN)
                        .requestMatchers(HttpMethod.DELETE, FLIGHT + "/**").hasRole(ADMIN)
                        // User
                        .requestMatchers(HttpMethod.POST, USER).permitAll()
                        .requestMatchers(HttpMethod.GET, USER).hasRole(ADMIN)
                        .requestMatchers(HttpMethod.GET, USER + "/search").hasRole(ADMIN)
                        // Booking
                        .requestMatchers(HttpMethod.GET, BOOKING).hasRole(ADMIN)
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }
    
    @Bean @Order(2)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/error").permitAll()
                .requestMatchers("/").permitAll()
                .requestMatchers("/login").permitAll()
                .requestMatchers("/flights/**").permitAll()
                .requestMatchers("/register").permitAll()
                .requestMatchers("/css/**").permitAll()
                .requestMatchers("/js/**").permitAll()
                .requestMatchers("/admin/**").hasRole(ADMIN)
                .anyRequest().authenticated())
                .formLogin(form -> form
                    .loginPage("/login")
                    .defaultSuccessUrl("/dashboard")
                    .failureUrl("/login?error")
                    .permitAll());
        return http.build();
    }
}
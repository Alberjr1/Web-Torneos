package practica1.artefacto.config;

import java.security.Principal;
import java.util.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public AuthenticationProvider authProvider() {
        return new AuthenticationProvider() {
            @Override
            public Authentication authenticate(Authentication auth) throws AuthenticationException {
                String user = auth.getName();
                String pwd  = auth.getCredentials().toString();
                List<GrantedAuthority> roles = new ArrayList<>();
                if ("admin".equals(user) && "password".equals(pwd)) {
                    roles.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                } else {
                    roles.add(new SimpleGrantedAuthority("ROLE_USER"));
                }
                // devolvemos un token autenticado con esas authorities
                return new UsernamePasswordAuthenticationToken(user, pwd, roles);
            }
            @Override
            public boolean supports(Class<?> authClass) {
                return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authClass);
            }
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
          .authenticationProvider(authProvider())
          .authorizeHttpRequests(auth -> auth
            .requestMatchers(HttpMethod.GET,
                "/login.html", "/styles.css", "/js/**", "/api/logos/**", 
                "/favicon.ico", "/404.html"
            ).permitAll()
            // Sólo ADMIN puede modificar la API
            .requestMatchers(HttpMethod.POST,   "/api/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.PUT,    "/api/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.PATCH,  "/api/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMIN")
            .anyRequest().authenticated()
          )
          .formLogin(form -> form
            .loginPage("/login.html")
            .loginProcessingUrl("/login")
            .defaultSuccessUrl("/index.html", true)
            .failureUrl("/login.html?error")
            .permitAll()
          )
          .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/login.html?logout")
            .permitAll()
          )
          .csrf(csrf -> csrf.disable())
        ;
        return http.build();
    }
}

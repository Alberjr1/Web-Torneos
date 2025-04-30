package practica1.artefacto.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    //Definimos un usuario en memoria
    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails user = User.withDefaultPasswordEncoder()
            .username("admin")
            .password("password")
            .roles("USER")
            .build();
        return new InMemoryUserDetailsManager(user);
    }

    //Configuramos qué rutas están abiertas y cuál es el login/logout
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
          
          .authorizeHttpRequests(auth -> auth
            .requestMatchers(
                "/login.html",                  
                "/styles.css", 
                "/favicon.ico", 
                "/api/logos/**", 
                "/js/**")
            .permitAll()
            .anyRequest().authenticated()
          )
          
          .formLogin(form -> form
            .loginPage("/login.html")            
            .loginProcessingUrl("/login")        
            .defaultSuccessUrl("/", true)
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

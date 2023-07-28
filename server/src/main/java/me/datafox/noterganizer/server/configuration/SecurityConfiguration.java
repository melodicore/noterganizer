package me.datafox.noterganizer.server.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Boot security configuration. Contains a password encoder,
 * web security filter chain and authentication configuration.
 *
 * @author datafox
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * Token for remember me functionality.
     */
    @Value("noterganizer.remember.token")
    private String token;

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.csrf(csrf -> {
                    try {
                        csrf.disable();
                    } catch(Exception e) {
                        throw new RuntimeException(e);
                    }
                }).authorizeHttpRequests(registry ->
                        registry.requestMatchers("/version", "/register").permitAll()
                                .anyRequest().authenticated()
                ).formLogin(login -> login.loginPage("/login")
                        .permitAll()
                ).rememberMe(remember ->
                        remember.key(token)
                                .rememberMeParameter("remember")
                                .rememberMeCookieName("REMEMBER")
                ).build();
    }

    @Autowired
    public void configureAuthentication(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }
}

package com.agatapietrzycka.ticketreservation.config;

import com.agatapietrzycka.ticketreservation.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final String secret;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final RestAuthenticationFailureHandler failureHandler;
    private final RestAuthenticationSuccessHandler successHandler;
    private final ObjectMapper objectMapper;

    public SecurityConfig(@Value("${jwt.secret}") String secret, UserService userService, PasswordEncoder passwordEncoder,
                          RestAuthenticationFailureHandler failureHandler,
                          RestAuthenticationSuccessHandler successHandler, ObjectMapper objectMapper) {
        this.secret = secret;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.failureHandler = failureHandler;
        this.successHandler = successHandler;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors()
                .and().csrf().disable()
                .authorizeRequests()
                .antMatchers("/ticketreservation/api/flight/allFlight").permitAll()
                .antMatchers("/ticketreservation/api/flight/findFlight").permitAll()
                .antMatchers("/swagger-ui.html").permitAll()
                .antMatchers("/v2/api-docs").permitAll()
                .antMatchers("/webjars/**").permitAll()
                .antMatchers("/swagger-resources/**").permitAll()
                .antMatchers("/ticketreservation/api/login").permitAll()
                .antMatchers("/ticketreservation/api/register/user").permitAll()
                .antMatchers("/ticketreservation/api/register/activate/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(authenticationFilter())
                .addFilter(new JwtAuthorizationFilter(authenticationManager(), userService, secret));
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }

    private JsonObjectAuthorizationFilter authenticationFilter() throws Exception {
        JsonObjectAuthorizationFilter authorizationFilter = new JsonObjectAuthorizationFilter(objectMapper);
        authorizationFilter.setAuthenticationSuccessHandler(successHandler);
        authorizationFilter.setAuthenticationFailureHandler(failureHandler);
        authorizationFilter.setAuthenticationManager(authenticationManager());
        authorizationFilter.setFilterProcessesUrl("/ticketreservation/api/login");
        return authorizationFilter;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(userService);
        return provider;
    }

}

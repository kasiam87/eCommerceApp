package com.udacity.ecommerce.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.context.annotation.Bean;

import static com.udacity.ecommerce.constants.SecurityConstants.SIGN_UP_URL;

@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {
    private static final Logger log = LoggerFactory.getLogger(WebSecurity.class);

    private UserDetailsServiceImpl userDetailsService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public WebSecurity(UserDetailsServiceImpl userDetailsService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userDetailsService = userDetailsService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        try {
            http.cors().and().csrf().disable().authorizeRequests()
                    .antMatchers(HttpMethod.POST, SIGN_UP_URL).permitAll()
                    .anyRequest().authenticated()
                    .and()
                    .addFilter(new AuthenticationFilter(authenticationManager()))
                    .addFilter(new AuthorizationFilter(authenticationManager()))
                    // this disables session creation on Spring Security
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        } catch (Exception e) {
            log.error("EXCEPTION: Configuration failed.");
            throw e;
        }
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        try {
            auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
        } catch (Exception e) {
            log.error("EXCEPTION: Configuration failed.");
            throw e;
        }
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }
}

package com.me.chengspringboot.security;

import com.me.chengspringboot.repositories.UserRepository;
import com.me.chengspringboot.services.UserService;
import com.me.chengspringboot.utilities.PasswordEncoderUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    protected SecurityFilterChain configure(HttpSecurity http, UserDetailsService userDetailsService) throws Exception {
        http
                /*.addFilterAfter(new JWTVerifyFilter(userDetailsService), UsernamePasswordAuthenticationFilter.class)*/
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/user/**").permitAll()
                        .requestMatchers("/folder/**").permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable());

        return http.build();
    }

/*    @Bean
    public AuthenticationProvider authProvider(
            UserDetailsService userService,
            PasswordEncoder encoder) {
        var dao = new DaoAuthenticationProvider();

        dao.setUserDetailsService(userService);
        dao.setPasswordEncoder(encoder);

        return dao;
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoderUtil encoder, UserRepository userRepository) {
        return (UserDetailsService) new UserService(userRepository, encoder);
    }

    @Bean
    public PasswordEncoder encoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }*/
}

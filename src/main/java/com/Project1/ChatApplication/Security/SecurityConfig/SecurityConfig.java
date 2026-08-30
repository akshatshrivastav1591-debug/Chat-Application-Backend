package com.Project1.ChatApplication.Security.SecurityConfig;

import com.Project1.ChatApplication.Security.Jwt.JwtFilterClass;
import com.Project1.ChatApplication.Security.SecurityService.UserSecurityService;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    UserSecurityService User;
    @Autowired
    JwtFilterClass jwtFilter;

    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;



    @Bean
    public AuthenticationProvider authProvider(){
        DaoAuthenticationProvider provider=new DaoAuthenticationProvider(User);
        provider.setPasswordEncoder(new BCryptPasswordEncoder(12));

        return  provider;
    }

    @Bean
public AuthenticationManager authenticationManager(AuthenticationConfiguration configur){
   return configur.getAuthenticationManager();
}

@Bean
public SecurityFilterChain securityFilterChain (HttpSecurity http){
        http.csrf(customizer->customizer.disable());
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
        http.authorizeHttpRequests(request->request.requestMatchers("/register","/authenticatingJwtToken","/login","/forgotpassword/generateotp","/forgotpassword/validatingotp","/chat/**","/app/**","/topic/**")
                .permitAll()
                .anyRequest()
                .authenticated());
//        http.oauth2Login(customizer->customizer.failureHandler(new SimpleUrlAuthenticationFailureHandler("/error")).successHandler(outh2SuccessorHandeler));

        http.sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);


    return http.build();
}
    @Bean
    public BCryptPasswordEncoder PasswordEncoder(){
        return  new BCryptPasswordEncoder(12);
    }



    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of(allowedOrigins.split(",")));
        config.setAllowedMethods(List.of("*"));
        config.setAllowCredentials(true);
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Set-Cookie"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
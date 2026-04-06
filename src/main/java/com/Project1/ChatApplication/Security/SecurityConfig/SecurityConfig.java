package com.Project1.ChatApplication.Security.SecurityConfig;

import com.Project1.ChatApplication.Security.Jwt.JwtFilterClass;
import com.Project1.ChatApplication.Security.SecurityService.UserSecurityService;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import org.springframework.beans.factory.annotation.Autowired;

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

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    UserSecurityService User;
    @Autowired
    JwtFilterClass jwtFilter;





    @Bean
    public AuthenticationProvider authProvider(){
        DaoAuthenticationProvider provider=new DaoAuthenticationProvider(User);
        provider.setPasswordEncoder(new BCryptPasswordEncoder(12));
        System.out.println("Authentication Provider Method is Working");
        return  provider;
    }

    @Bean
public AuthenticationManager authenticationManager(AuthenticationConfiguration configur){
   return configur.getAuthenticationManager();
}

@Bean
public SecurityFilterChain securityFilterChain (HttpSecurity http){
        http.csrf(customizer->customizer.disable());
        http.cors(cors->{});
        http.authorizeHttpRequests(request->request.requestMatchers("/register","/login","/forgotpassword/generateotp","/forgotpassword/validatingotp")
                .permitAll()
                .anyRequest()
                .authenticated());
//        http.oauth2Login(customizer->customizer.failureHandler(new SimpleUrlAuthenticationFailureHandler("/error")).successHandler(outh2SuccessorHandeler));

        http.sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        http.httpBasic(Customizer.withDefaults());
    System.out.println("Security filter chain method is working:");
    return http.build();
}
    @Bean
    public BCryptPasswordEncoder PasswordEncoder(){
        return  new BCryptPasswordEncoder(12);
    }
//    @Bean
//    public PhoneNumberUtil phoneNumberUtil(){
//        return  PhoneNumberUtil.getInstance();
//    }

@Bean
    public  String generateOtp(){
        return null;
}
}
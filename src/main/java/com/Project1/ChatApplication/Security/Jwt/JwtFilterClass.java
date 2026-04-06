package com.Project1.ChatApplication.Security.Jwt;

import com.Project1.ChatApplication.Security.SecurityService.UserSecurityService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtFilterClass extends OncePerRequestFilter {
    @Autowired
   private  JwtFilterServiceClass jwt;
    @Autowired
    ApplicationContext context;

    private String  extractTokensFromCookies(HttpServletRequest request){
        if(request.getCookies()==null) return  null;
        for(Cookie cookie: request.getCookies()){
            if("jwt".equals(cookie.getName())){
                return  cookie.getValue();
            }
        }
        return null;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        String authHeader=request.getHeader("Authorization");


        String token=extractTokensFromCookies(request);
        String userName=null;
        if (token!=null){
            userName=jwt.extractUserName(token);
        }
//        if(authHeader!=null && authHeader.startsWith("Bearer ")){
//            token=authHeader.substring(7);
//            userName=jwt.extractUserName(token);
//        }
        if(userName!=null && SecurityContextHolder.getContext().getAuthentication()==null){
            UserDetails userdetails=context.getBean(UserSecurityService.class).loadUserByUsername(userName);
            if(jwt.validateToken(token,userdetails)){
                Map<String,Object> userDetails=new HashMap<>();
                String userId= jwt.extractUserId(token);
                boolean isFirstLogin= jwt.extractIsFirstLogin(token);
               userDetails.put("userID",userId);
               userDetails.put("isFirstLogin",isFirstLogin);
               userDetails.put("requestDetails",new WebAuthenticationDetailsSource().buildDetails(request));
                UsernamePasswordAuthenticationToken authToken=new UsernamePasswordAuthenticationToken(userdetails,null,userdetails.getAuthorities());
                authToken.setDetails(userDetails);

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
filterChain.doFilter(request,response);
    }
}

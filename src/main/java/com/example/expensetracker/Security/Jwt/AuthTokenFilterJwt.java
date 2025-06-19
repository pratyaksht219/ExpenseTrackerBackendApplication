package com.example.expensetracker.Security.Jwt;

import com.example.expensetracker.Security.Sevices.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
public class AuthTokenFilterJwt extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

    // This filter is executed for every request to the server.
    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilterJwt.class);
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        logger.debug("AuthTokenFilterJwt called for URI: {}", request.getRequestURI());
        String jwt = parseJwt(request);
        try{
            if(jwt == null){
                logger.debug("No Jwt token found in the authorization header");
            }else{
                logger.debug("JWT token received: {}", jwt);
            }
            boolean isJwtValid = jwtUtils.validateJwtToken(jwt);
            if(jwt!=null && isJwtValid){
                String username = jwtUtils.getUserNameFromJwtToken(jwt);
                logger.debug("User with username: {} logged in", username);

                UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentcation = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentcation.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentcation);
                logger.debug("Authentication set for user: {}", username);;
                logger.debug("User Roles: {}", userDetails.getAuthorities());
            }
        } catch (Exception e){
            logger.error("Cannot set user authentication: {}", e.getMessage());
        } finally {
            filterChain.doFilter(request, response); // Proceed with the filter chain
        }
    }

    private String parseJwt(HttpServletRequest request) {
        logger.debug("Parsing JWT from request cookie");
        String jwt = jwtUtils.getJwtFromCookie(request);
        logger.debug("Extracted JWT: {}", jwt);
        return jwt;
    }
}

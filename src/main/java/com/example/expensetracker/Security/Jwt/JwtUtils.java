package com.example.expensetracker.Security.Jwt;

import com.example.expensetracker.Security.Sevices.UserDetailsImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
// Utility class for handling JWT operations
public class JwtUtils {
    @Value("${spring.app.jwtExpirationMs}")
    private int jwtExpirationMs; // JWT expiration time in milliseconds
    @Value("${spring.app.jwtSecret}")
    private String jwtSecret;
    @Value("${spring.app.jwtCookieName}")
    private String jwtCookie; // Cookie name for JWT

    //Created a logger object for logging the various steps onto the console, helps ease out debugging
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
    // PostConstruct method to initialize the JWT secret key
    @PostConstruct
    public void init() {
        logger.info("JWT Secret Key Loaded: {}", jwtSecret != null && !jwtSecret.isBlank());
    }

//    =========================================================================================
    //generate JWT cookie
    public ResponseCookie generateJwtCookie(UserDetailsImpl userPrincipal) {

        logger.debug("Generating JWT cookie for username: {}", userPrincipal);
        // Generate JWT token from user details
        String jwt = generateTokenFromUserName(userPrincipal);
        logger.debug("Generated JWT cookie: {}", jwt);
        return ResponseCookie.from(jwtCookie, jwt)
                .path("/api")
                .maxAge(24*60*60)
                .httpOnly(true)
                .build();
    }
//    =========================================================================================
    //generate token from username
    private String generateTokenFromUserName(UserDetailsImpl userPrincipal) {
        String userName = userPrincipal.getUsername();
        return Jwts.builder()
                .subject(userName)
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(getKey())
                .compact();
    }

//    ========================================================================================
    //get sign in key from application properties
    private Key getKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    //get JWT from cookie
    public String getJwtFromCookie(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, jwtCookie);//create a cookie object for the current servlet request

        if(cookie!=null){
            logger.debug("Found JWT cookie: {}", cookie.getValue());
            return cookie.getValue();//if cookie is found then return its value.
        }else{
            logger.debug("JWT cookie not found");
            return null; // If the cookie is not present, return null.
        }
    }
//    =======================================================================================
    //get username from JWT token
    public String getUserNameFromJwtToken(String token){
        logger.debug("Extracting username from JWT token:{}", token);
        return Jwts.parser()//build a parser to read the jwt
                .verifyWith((SecretKey)getKey())//verify the token with the secret key
                .build()
                .parseSignedClaims(token)//verify the token with the secret key
                .getPayload()//extract the payload/actual content of the token
                .getSubject(); // Finally extract the subject (username) from the JWT token
    }

//    ========================================================================================
    //validate JWT token
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser() // Start the parser
                    .verifyWith((SecretKey) getKey()) // Verify the JWT token with the secret key
                    .build() // Build the parser
                    .parseSignedClaims(authToken); // Parse the JWT and extract the claims (data inside the token)
            return true; // If no exceptions are thrown, the token is valid
        } catch(MalformedJwtException e){
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e){
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT token: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;// If an exception is thrown, the token is invalid
    }

    public ResponseCookie getCleanJwtCookie() {
        logger.debug("Creating clean JWT cookie to clear authentication");
        return ResponseCookie.from(jwtCookie, "")
                .path("/api")
                .maxAge(0)
                .httpOnly(true)
                .build(); // Create a cookie with an empty value and max age of 0 to clear it
    }
}

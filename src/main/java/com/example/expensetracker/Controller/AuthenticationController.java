package com.example.expensetracker.Controller;

import com.example.expensetracker.Entity.Approle;
import com.example.expensetracker.Entity.Role;
import com.example.expensetracker.Entity.User;
import com.example.expensetracker.Repository.RoleRepository;
import com.example.expensetracker.Repository.UserRepository;
import com.example.expensetracker.Security.Jwt.JwtUtils;
import com.example.expensetracker.Security.Requests.LoginRequest;
import com.example.expensetracker.Security.Requests.SignupRequest;
import com.example.expensetracker.Security.Responses.UserInfoResponse;
import com.example.expensetracker.Security.Sevices.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest){
        Authentication authentication = null;
        try{
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
            ));
        } catch (AuthenticationException e){
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("Error", "Invalid Credentials");
            errorMap.put("Status", HttpStatus.UNAUTHORIZED.toString());
        }
        if(authentication == null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        ResponseCookie cookie = jwtUtils.generateJwtCookie(userDetails);
        List<String> roles = userDetails.getAuthorities() != null
                ? userDetails.getAuthorities().stream()
                .filter(Objects::nonNull)
                .map(GrantedAuthority::getAuthority)
                .toList()
                : new ArrayList<>();

        UserInfoResponse loginResponse = new UserInfoResponse(
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                cookie,
                roles
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(loginResponse);
    }
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest){
        if(userRepository.existsByName(signUpRequest.getUsername())){
            return ResponseEntity.badRequest()
                    .body("Error: Username already exists!");
        }
        if(userRepository.existsByEmail(signUpRequest.getEmail())){
            return ResponseEntity.badRequest()
                    .body("Error: Email Already taken!");
        }

        User user = new User();
        user.setName(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

        List<Role> strRoles = signUpRequest.getRoles();
        Set<Role> roles = user.getRoles();

        if (strRoles == null) {
            Role userRole = roleRepository.findByRoleName(Approle.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                if(role != null){
                    switch (role.getRoleName()) {
                        case ROLE_ADMIN -> {
                            Role adminRole = roleRepository.findByRoleName(Approle.ROLE_ADMIN)
                                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                            roles.add(adminRole);
                        }
                        case ROLE_SELLER -> {
                            Role sellerRole = roleRepository.findByRoleName(Approle.ROLE_SELLER)
                                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                            roles.add(sellerRole);
                        }
                        default -> {
                            Role userRole = roleRepository.findByRoleName(Approle.ROLE_USER)
                                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                            roles.add(userRole);
                        }
                    }
                }
            });

        }
        user.setRoles(roles);
        userRepository.save(user);
        return new ResponseEntity<>("User registered successfully!", HttpStatus.CREATED);
    }
    @GetMapping("/user")
    public ResponseEntity<?> getCurrentUserDetails(Authentication authentication){
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Error: NO CURRENT USER AUTHENTICATED");
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return new ResponseEntity<>(userDetails, HttpStatus.OK);
    }
    @GetMapping("/username")
    public ResponseEntity<?> getCurrentUserName(Authentication authentication){
        if(authentication == null) {
            return new ResponseEntity<>("Error: No user is currently authenticated.", HttpStatus.UNAUTHORIZED);
        }
        String currentUsername  =  authentication.getName();
        return new ResponseEntity<>(currentUsername, HttpStatus.OK);
    }
//    @GetMapping("/user/roles")
//    public ResponseEntity<?> getCurrentUserRoles(Authentication authentication){
//        if(authentication == null) {
//            return new ResponseEntity<>("Error: No user is currently authenticated.", HttpStatus.UNAUTHORIZED);
//        }
//        List<Role> currentUserRoles = (List<Role>) authentication.getAuthorities();
//        return new ResponseEntity<>(currentUserRoles, HttpStatus.OK);
//    }
    @PostMapping("/signout")
    public ResponseEntity<?> signoutUser(){
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body("You have been successfully signed out");
    }

}

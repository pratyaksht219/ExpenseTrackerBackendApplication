package com.example.expensetracker.Security;

import com.example.expensetracker.Entity.Approle;
import com.example.expensetracker.Entity.User;
import com.example.expensetracker.Repository.RoleRepository;
import com.example.expensetracker.Repository.UserRepository;
import com.example.expensetracker.Security.Jwt.AuthEntryPointJwt;
import com.example.expensetracker.Security.Jwt.AuthTokenFilterJwt;
import com.example.expensetracker.Security.Sevices.UserDetailsServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.example.expensetracker.Entity.Role;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Set;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthTokenFilterJwt authenticationJwtTokenFilter(){
        return new AuthTokenFilterJwt();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        try{
            return authenticationConfiguration.getAuthenticationManager();
        } catch(Exception e) {
            throw(new RuntimeException("AuthenticationManager not found", e));
        }
    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        //disable csrf protection, not recommended for production environment.
        http.csrf(csrf -> csrf.disable());
        //set the exception handler for unauthorized access.(unAuthorized handler -> AuthEntryPointJwt.class
        http.exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(unauthorizedHandler));
        //disable session management, as we a re making user of the jwt authentication.(stateless)
        http.sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        // allow requests to the following endpoints without authentication.
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/test/**").permitAll()
                        .requestMatchers("/api/admin/**").permitAll()
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/error/**").permitAll()
                        .requestMatchers("/images/**").permitAll()
                        // any requests other than the above-mentioned should be authenticated.
                        .anyRequest().authenticated());

        //disable form-based authentication.
        http.formLogin(formLogin -> formLogin.disable());
        //disable basic authentication.
        http.httpBasic(httpBasic -> httpBasic.disable());

        //assign the authentication provider to the httpSecurity
        http.authenticationProvider(authenticationProvider());
        //add the authenticationJwtTokenFilter to the filter chain before the Spring security's default UsernamePasswordAuthenticationFilter.
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        /*
        This configures the HTTP response headers related to framing options, specifically the X-Frame-Options header.
            Purpose:- Controls whether your web pages can be embedded in frames/iframes of other websites.
                       Sets the X-Frame-Options header to "SAMEORIGIN", which allows the page to be displayed in frames on the same origin (domain)
                       but prevents it from being displayed in frames on other domains.
            Security Benefit:- - Helps prevent clickjacking attacks where malicious sites might embed your pages in hidden frames.
        */
        http.headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.sameOrigin()));

        //finally build the httpSecurityFilterChain object and return it.
        return http.build();
    }


//    This configuration is specifically designed to exclude Swagger UI and API documentation endpoints from security checks.
//    Swagger is a tool for documenting and testing APIs, and these paths need to be publicly accessible so that
//    developers can view API documentation without authentication.

//      By ignoring these paths, you ensure that:
//      Documentation is accessible without logging in
//      The Swagger UI interface works properly
//      API documentation endpoints don't require authentication
//       Web resources needed by Swagger UI (from webjars) are publicly available

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return (web) -> web.ignoring()
                .requestMatchers(
                        "/configuration/ui/**",
                        "/configuration/security/**",
                        "/v2/api-docs/**",
                        "/swagger-resources/**",
                        "/swagger-ui.html",
                        "/webjars/**"
                );
    }
    @Bean
    public CommandLineRunner initData(RoleRepository roleRepository, UserRepository userRepository){
        return args->{
            //Retrieve or create roles if they do not exist
            Role userRole = roleRepository.findByRoleName(Approle.ROLE_USER)
                    .orElseGet(()->{
                        Role newUserRole = new Role(Approle.ROLE_USER);
                        return roleRepository.save(newUserRole);
                    });

            Role adminRole = roleRepository.findByRoleName(Approle.ROLE_ADMIN)
                    .orElseGet(()->{
                        Role newAdminRole = new Role(Approle.ROLE_ADMIN);
                        return roleRepository.save(newAdminRole);
                    });

            Role sellerRole = roleRepository.findByRoleName(Approle.ROLE_SELLER)
                    .orElseGet(()->{
                        Role newSellerRole = new Role(Approle.ROLE_SELLER);
                        return roleRepository.save(newSellerRole);
                    });

            Set<Role> userRoles = Set.of(userRole);
            Set<Role> sellerRoles = Set.of(sellerRole);
            Set<Role> adminRoles = Set.of(userRole, adminRole, sellerRole);



//            CREATING DEFAULT USERS IF NOT EXISTING

//            Create a default user with user privileges
            if(!userRepository.existsByName("user1")){
                User user1 = new User("user1",
                        "user1@example.com",
                        passwordEncoder().encode("user1@123"));
                user1.setRoles(userRoles);
                userRepository.save(user1);
            }

//            Create a default user with admin privileges
            if(!userRepository.existsByName("admin1")){
                User admin1 = new User("admin1",
                        "admin1@example.com",
                        passwordEncoder().encode("admin1@123"));
                admin1.setRoles(adminRoles);
                userRepository.save(admin1);
            }

//            Create a default user with seller privileges

            if(!userRepository.existsByName("seller1")){
                User seller1 = new User("seller1",
                        "seller1@example.com",
                        passwordEncoder().encode("seller1@123"));
                seller1.setRoles(sellerRoles);
                userRepository.save(seller1);
            }
        };
    }
}

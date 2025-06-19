package com.example.expensetracker.Security.Sevices;


import com.example.expensetracker.Entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
public class UserDetailsImpl implements UserDetails {
    private static final Long serialVersionUID = 1L;

    private Long Id;
    private String username;
    private String email;
    @JsonIgnore
    private String password;
    private List<?extends GrantedAuthority> authorities; // Collection of roles and authorities that are assigned to the user


    //constructor to initialize the UserDetailsImpl object
    public UserDetailsImpl(Long Id, String username, String email, String password, Collection<? extends GrantedAuthority> authority) {
        this.Id = Id;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    // static method to build UserDetailsImpl from our own User entity
    public static UserDetailsImpl build(User user){
        List<? extends GrantedAuthority> authorities = user
                .getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleName().name()))
                .toList();

        return new UserDetailsImpl(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if(o==null || getClass() != o.getClass())
            return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(Id, user.getId());
    }


}

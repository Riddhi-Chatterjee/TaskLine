package com.example.taskline.security.util;

import com.example.taskline.entity.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
public class TaskLineDetails implements UserDetails {

    private String username;
    private String password;
    private boolean isVerified;
    private List<GrantedAuthority> authorities;

    public TaskLineDetails(User user) {
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.isVerified = user.isVerified();

        this.authorities = List.of(); //Empty list
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
        return isVerified;
    }
}

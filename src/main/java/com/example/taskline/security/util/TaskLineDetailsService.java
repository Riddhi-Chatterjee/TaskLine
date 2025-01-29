package com.example.taskline.security.util;

import com.example.taskline.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskLineDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        return userRepository.findByUsername(username)
                .map(TaskLineDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
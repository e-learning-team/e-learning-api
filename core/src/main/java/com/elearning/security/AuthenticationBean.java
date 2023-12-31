package com.elearning.security;

import com.elearning.entities.User;
import com.elearning.reprositories.IUserRepository;
import com.elearning.utils.enumAttribute.EnumRole;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Collection;

@Configuration
@RequiredArgsConstructor
public class AuthenticationBean {
    @Autowired
    IUserRepository userRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        return this::loadUserByUsername;
    }
    private UserDetails loadUserByUsername(String username) {
        User entity = userRepository.findByEmail(username);
        if (entity == null) {
            throw new UsernameNotFoundException("Email không tồn tại");
        }
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        entity.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role.name()));
        });

        return new SecurityUserDetail(
                entity.getId(),
                entity.getFullName(),
                entity.getEmail(),
                entity.getPassword(),
                authorities,
                entity.getIsDeleted()
        );
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}

package me.datafox.noterganizer.server.service.impl;

import me.datafox.noterganizer.server.model.AppUser;
import me.datafox.noterganizer.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * User details service implementation.
 *
 * @author datafox
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        return User.withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRoles().toArray(String[]::new))
                .build();
    }
}

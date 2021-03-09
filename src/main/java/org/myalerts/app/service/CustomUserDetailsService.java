package org.myalerts.app.service;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.myalerts.app.model.CustomUserDetails;
import org.myalerts.app.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return Optional.ofNullable(userRepository.getUserByUsername(username))
            .map(CustomUserDetails::new)
            .orElseThrow(() -> new UsernameNotFoundException("Could not find user"));
    }

}

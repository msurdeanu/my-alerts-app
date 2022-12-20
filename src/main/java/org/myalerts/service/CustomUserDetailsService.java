package org.myalerts.service;

import lombok.RequiredArgsConstructor;
import org.myalerts.domain.CustomUserDetails;
import org.myalerts.repository.UserRepository;
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
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
            .map(CustomUserDetails::new)
            .orElseThrow(() -> new UsernameNotFoundException("Could not find user with name = " + username));
    }

}

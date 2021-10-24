package org.myalerts.service;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.myalerts.model.CustomUserDetails;
import org.myalerts.model.User;
import org.myalerts.repository.UserRepository;

/**
 * @author Mihai Surdeanu
 * @since 1.0.0
 */
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        return Optional.ofNullable(userRepository.getUserByUsername(username))
            .map(CustomUserDetails::new)
            .orElseThrow(() -> new UsernameNotFoundException("Could not find user"));
    }

    public boolean registerUser(final User user) {
        user.setEnabled(true);
        user.setRole("ROLE_USER");
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        userRepository.save(user);
        return true;
    }

}

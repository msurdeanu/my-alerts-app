package org.myalerts;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Slf4j
public class SecuredPasswordGenerator {

    public static void main(String[] args) {
        log.info(encode("admin"));
    }

    private static String encode(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }

}

package org.myalerts;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class SecuredPasswordGenerator {

    public static void main(String[] args) {
        System.out.println(encode("admin"));
    }

    private static String encode(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }

}

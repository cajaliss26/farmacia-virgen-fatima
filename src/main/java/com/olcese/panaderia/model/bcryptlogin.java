package com.olcese.panaderia.model;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class bcryptlogin {
    public static void main(String[] args) {
        BCryptPasswordEncoder enc = new BCryptPasswordEncoder();

        System.out.println("farmacia1 -> " + enc.encode("farmacia1"));
        System.out.println("farmacia2 -> " + enc.encode("farmacia2"));
    }
}

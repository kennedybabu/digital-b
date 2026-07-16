package com.example.customerservice.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Fingerprints {

    private Fingerprints() {}

    public static String customerCreate(String firstName,
                                        String lastName,
                                        String email,
                                        String phone,
                                        String address) {
        String s = (firstName == null ? "" : firstName.trim().toLowerCase()) + "|" +
                (lastName == null ? "" : lastName.trim().toLowerCase()) + "|" +
                (email == null ? "" : email.trim().toLowerCase()) + "|" +
                (phone == null ? "" : phone.trim().toLowerCase()) + "|" +
                (address == null ? "" : address.trim().toLowerCase());
        return sha256(s);
    }

    private static String sha256(String s) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(s.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) hexString.append(String.format("%02x", b));
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}

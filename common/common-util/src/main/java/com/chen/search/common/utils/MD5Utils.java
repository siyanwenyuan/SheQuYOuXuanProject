package com.chen.search.common.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {
    public static String encrypt(String input) {
        String md5 = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(input.getBytes(), 0, input.length());
            md5 = new BigInteger(1, digest.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md5;
    }

    public static void main(String[] args) {
        String input = "Hello, world!";
        String encrypted = encrypt(input);
        System.out.println("Original: " + input);
        System.out.println("Encrypted: " + encrypted);
    }
}

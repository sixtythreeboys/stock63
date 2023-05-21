package com.sixtythree.stock63.domestic.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Aes256 {
    public static String alg = "AES/CBC/PKCS5Padding";
    private final String key;
    private final String iv;

    public Aes256(String key, String iv){
        this.iv = iv.trim();
        this.key = key;
    }

    public String encrypt(String text) throws Exception {
        Cipher cipher = Cipher.getInstance(alg);
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
        IvParameterSpec ivParamSpec = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParamSpec);

        byte[] encrypted = cipher.doFinal(text.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public String decrypt(String cipherText) throws Exception {
        System.out.println("1 ::: " + "8313213cafdf78b8".getBytes().length);
        System.out.println("1 ::: " + "8313213cafdf78b".getBytes().length);
        System.out.println("1 ::: " + "8313213cafdf78".getBytes().length);
        System.out.println("1 ::: " + "8313213cafdf7".getBytes().length);
        System.out.println("iv ---:::::::::: " + iv + ", " + iv.getBytes().length);
        System.out.println("KEY ---:::::::::: " + key);
        Cipher cipher = Cipher.getInstance(alg);
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
        IvParameterSpec ivParamSpec = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParamSpec);

        byte[] decodedBytes = Base64.getDecoder().decode(cipherText);
        byte[] decrypted = cipher.doFinal(decodedBytes);
        return new String(decrypted, StandardCharsets.UTF_8);
    }
}

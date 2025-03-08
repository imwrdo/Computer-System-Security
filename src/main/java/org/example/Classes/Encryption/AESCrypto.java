package org.example.Classes.Encryption;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

public class AESCrypto {
    private static final String ALGORITHM = "AES";
    private static final String FACTORYALGO = "PBKDF2WithHmacSHA256";
    private String type;
    private IvParameterSpec iv;

    public AESCrypto(String type) {
        this.type = type;
        this.iv = GenerateIv();
    }

    public SecretKey GetKey(String pin, Integer iterations, Integer keyLength, String charSet) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance(FACTORYALGO);
        KeySpec spec = new PBEKeySpec(pin.toCharArray(), pin.getBytes(charSet),
                iterations, keyLength);
        return new SecretKeySpec(factory.generateSecret(spec)
                .getEncoded(), ALGORITHM);
    }

    public IvParameterSpec GenerateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    public void RestoreIv(byte[] iv) {
        this.iv = new IvParameterSpec(iv);
    }

    public byte[] Encrypt(byte[] text, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(type);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] cipherText = cipher.doFinal(text);

        byte[] ivAndEncryptedData = new byte[iv.getIV().length + cipherText.length];
        System.arraycopy(iv.getIV(), 0, ivAndEncryptedData, 0, iv.getIV().length);
        System.arraycopy(cipherText, 0, ivAndEncryptedData, iv.getIV().length, cipherText.length);


        //return Base64.getEncoder().encodeToString(cipherText);
        //return Base64.getEncoder().encodeToString(ivAndEncryptedData);
        return ivAndEncryptedData;
    }

    public void SaveIv() throws Exception {
        try(FileOutputStream fos = new FileOutputStream("iv")) {
            byte[] ivBytes = new byte[16];
            System.arraycopy(iv.getIV(), 0, ivBytes, 0, 16);
            fos.write(ivBytes);
        }
    }

    public void ReadIv() throws Exception {
        try(FileInputStream fis = new FileInputStream("iv")) {
            byte[] ivBytes = new byte[16];
            fis.read(ivBytes);
            //iv = RestoreIv(ivBytes);
            fis.close();
        }
    }

    public byte[] Decrypt(byte[] complexText, SecretKey key) throws Exception {
        System.out.println(iv.getIV());
        //ReadIv();
        byte[] ivBytes = new byte[16];  // AES block size is 16 bytes
        System.arraycopy(complexText, 0, ivBytes, 0, ivBytes.length);
        RestoreIv(ivBytes);

        System.out.println(iv.getIV());

        // Extract the actual encrypted data (the ciphertext)
        byte[] cipherText = new byte[complexText.length - ivBytes.length];
        System.arraycopy(complexText, ivBytes.length, cipherText, 0, cipherText.length);

        // Initialize AES cipher for decryption with the extracted IV
        Cipher cipher = Cipher.getInstance(type);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        //byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(cipherText));
        byte[] plainText = cipher.doFinal(cipherText);
        return plainText;
        //return  Base64.getEncoder().encodeToString(plainText);
    }

}

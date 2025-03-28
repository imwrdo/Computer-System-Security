package org.example.encryption;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;

/**
 * AES Encryption/Decryption Utility with Secure IV Handling.
 */
public class AESCrypto {
    private static final String ALGORITHM = "AES";
    private static final String FACTORY_ALGORITHM = "PBKDF2WithHmacSHA256";
    private final String type;
    private IvParameterSpec iv;

    public AESCrypto(String type) {
        this.type = type;
        this.iv = generateIv();
    }

    /**
     * Generates a SecretKey using PBKDF2 with HMAC-SHA256.
     *
     * @param pin        The password/PIN used for key derivation.
     * @param iterations Number of PBKDF2 iterations.
     * @param keyLength  Length of the key in bits (e.g., 256 for AES-256).
     * @return Derived SecretKey.
     */
    public SecretKey getKey(String pin, Integer iterations, Integer keyLength, String charSet) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance(FACTORY_ALGORITHM);
        KeySpec spec = new PBEKeySpec(pin.toCharArray(), pin.getBytes(charSet),
                iterations, keyLength);
        return new SecretKeySpec(factory.generateSecret(spec)
                .getEncoded(), ALGORITHM);
    }

    /**
     * Generates a random IV.
     *
     * @return New IV as IvParameterSpec.
     */
    public IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    /**
     * Restores a IV
     */
    public void restoreIv(byte[] iv) {
        this.iv = new IvParameterSpec(iv);
    }

    /**
     * Encrypts data using AES with a unique IV.
     *
     * @param text The plaintext data.
     * @param key  The AES secret key.
     * @return IV + Encrypted data.
     */
    public byte[] encrypt(byte[] text, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(type);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] cipherText = cipher.doFinal(text);

        byte[] ivAndEncryptedData = new byte[iv.getIV().length + cipherText.length];
        System.arraycopy(iv.getIV(), 0, ivAndEncryptedData, 0, iv.getIV().length);
        System.arraycopy(cipherText, 0, ivAndEncryptedData, iv.getIV().length, cipherText.length);

        return ivAndEncryptedData;
    }

    /**
     * Decrypts AES-encrypted data.
     *
     * @param complexText The IV + Encrypted content.
     * @param key           The AES secret key.
     * @return Decrypted plaintext data.
     */
    public byte[] decrypt(byte[] complexText, SecretKey key) throws Exception {
        System.out.println(Arrays.toString(iv.getIV()));

        byte[] ivBytes = new byte[16];  // AES block size is 16 bytes
        System.arraycopy(complexText, 0, ivBytes, 0, ivBytes.length);
        restoreIv(ivBytes);

        System.out.println(Arrays.toString(iv.getIV()));

        // Extract the actual encrypted data (the ciphertext)
        byte[] cipherText = new byte[complexText.length - ivBytes.length];
        System.arraycopy(complexText, ivBytes.length, cipherText, 0, cipherText.length);

        // Initialize AES cipher for decryption with the extracted IV
        Cipher cipher = Cipher.getInstance(type);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);


        return cipher.doFinal(cipherText);

    }

}

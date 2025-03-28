package org.example.encryption;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

/**
 * RSA Encryption/Decryption Utility.
 * Used to generate RSA key pairs.
 */
public class RSACrypto {
    private static final String ALGORITHM = "RSA";
    /**
     * Generates an RSA key pair of the specified length.
     *
     * @param length The key length (e.g., 2048, 3072, 4096).
     * @return The generated RSA key pair.
     */
    public KeyPair getPair(Integer length) throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance(ALGORITHM);
        kpg.initialize(length);
        return kpg.generateKeyPair();
    }
}

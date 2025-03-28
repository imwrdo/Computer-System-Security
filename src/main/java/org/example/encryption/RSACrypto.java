package org.example.encryption;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

public class RSACrypto {
    private static final String ALGORITHM = "RSA";
    public KeyPair getPair(Integer length) throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance(ALGORITHM);
        kpg.initialize(length);
        return kpg.generateKeyPair();
    }
}

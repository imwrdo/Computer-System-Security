package org.example.Classes.Encryption;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

public class RSACrypto {
    public KeyPair GetPair(Integer length) throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(length);
        return kpg.generateKeyPair();
    }
}

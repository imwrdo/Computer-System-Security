package org.example.crypto;


import org.example.encryption.AESCrypto;
import org.example.encryption.SignatureManager;
import org.example.usbseeker.USBSeeker;

import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

public class PDFCryptoBody {
    private static final AESCrypto aesCry = new AESCrypto("AES/CFB/NoPadding");
    private static final SignatureManager signatureManager = new SignatureManager();
    private static final USBSeeker usbSeeker = new USBSeeker();
    private static final String FILE_NOT_FOUND_MESSAGE = "The PENDRIVE with the PRIVATE KEY wasn't found! Ensure that you have your pendrive inserted and have all the rights to read it!";
    private static final String INVALID_PIN_MESSAGE = "Wrong pin!";
    private static final String CHARSET = "UTF-8";
    private static final String RSA_ALGORITHM = "RSA";
    private final Integer keyLength;
    public PDFCryptoBody(Integer keyLength, String keyFileName) {
        this.keyLength = keyLength == null ? 4096 : keyLength;
    }

    public PrivateKey GetPrivateKey(String pin, String keyPath) throws Exception {
        try(FileInputStream keyStream = new FileInputStream(keyPath)) {
            byte[] privateRSABytes = aesCry.decrypt(keyStream.readAllBytes(),
                    aesCry.getKey(pin, 65536, 256, CHARSET));
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateRSABytes);
            return keyFactory.generatePrivate(keySpec);
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException(FILE_NOT_FOUND_MESSAGE, e);
        }
        catch (InvalidKeySpecException e) {
            throw new RuntimeException(INVALID_PIN_MESSAGE, e);
        }
    }

    public String ScanForKey(String keyFileName) {
        return usbSeeker.getKeyPath(keyFileName);
    }

    public boolean SignPDF(String pin, String origPath, String signedPath, String keyPath) throws Exception {
        PrivateKey privateRSA = GetPrivateKey(pin, keyPath);
        return signatureManager.signPDF(origPath, signedPath, privateRSA);
    }
}

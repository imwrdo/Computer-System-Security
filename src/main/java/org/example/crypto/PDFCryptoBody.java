package org.example.crypto;


import org.example.encryption.AESCrypto;
import org.example.encryption.SignatureManager;
import org.example.usbseeker.USBSeeker;

import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 * PDFCryptoBody handles the cryptographic operations for signing PDFs.
 * It retrieves the private RSA key (secured with AES) and signs a PDF document.
 */
public class PDFCryptoBody {

    // Cryptographic utilities
    private static final AESCrypto aesCry = new AESCrypto("AES/CFB/NoPadding");
    private static final SignatureManager signatureManager = new SignatureManager();
    private static final USBSeeker usbSeeker = new USBSeeker();

    // Error messages
    private static final String FILE_NOT_FOUND_MESSAGE = "The PENDRIVE with the PRIVATE KEY wasn't found! " +
            "Ensure that you have your pendrive inserted and have all the rights to read it!";
    private static final String INVALID_PIN_MESSAGE = "Wrong pin!";

    // Cryptographic constants
    private static final String CHARSET = "UTF-8";
    private static final String RSA_ALGORITHM = "RSA";

    // RSA key length
    private final Integer keyLength;

    /**
     * Constructor initializes the cryptographic body with a specified key length.
     * @param keyLength Length of the RSA key (default: 4096 bits if null).
     * @param keyFileName Name of the private key file (unused but included for future extensibility).
     */
    public PDFCryptoBody(Integer keyLength, String keyFileName) {
        this.keyLength = keyLength == null ? 4096 : keyLength;
    }

    /**
     * Retrieves and decrypts the private RSA key from a given key file path.
     * @param pin The user-provided PIN for decryption.
     * @param keyPath The file path of the encrypted private key.
     * @return The decrypted RSA private key.
     * @throws Exception If the key cannot be found or the PIN is incorrect.
     */
    public PrivateKey GetPrivateKey(String pin, String keyPath) throws Exception {
        try (FileInputStream keyStream = new FileInputStream(keyPath)) {
            byte[] encryptedKeyBytes = keyStream.readAllBytes();

            // Decrypt the private key using AES encryption
            byte[] privateRSABytes = aesCry.decrypt(encryptedKeyBytes,
                    aesCry.getKey(pin, 65536, 256, CHARSET));

            // Convert the byte array into a PrivateKey object
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

    /**
     * Scans connected USB drives for the private key file.
     * @param keyFileName The name of the private key file to search for.
     * @return The file path of the key if found, otherwise null.
     */
    public String ScanForKey(String keyFileName) {
        return usbSeeker.getKeyPath(keyFileName);
    }

    /**
     * Signs a PDF file using the retrieved RSA private key.
     * @param pin The user-provided PIN for key decryption.
     * @param origPath The path to the original PDF file.
     * @param signedPath The destination path for the signed PDF.
     * @param keyPath The file path of the encrypted private key.
     * @return True if signing is successful, otherwise false.
     * @throws Exception If an error occurs during signing.
     */
    public boolean SignPDF(String pin, String origPath, String signedPath, String keyPath) throws Exception {
        PrivateKey privateRSA = GetPrivateKey(pin, keyPath);
        return signatureManager.signPDF(origPath, signedPath, privateRSA);
    }
}

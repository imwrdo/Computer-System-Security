package org.example.crypto;


import org.example.encryption.AESCrypto;
import org.example.encryption.RSACrypto;
import javax.crypto.SecretKey;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.*;

/**
 * KeyGeneratorBody is responsible for generating RSA key pairs and encrypting private keys.
 * It supports AES encryption for securing private keys and provides file-writing functionality.
 */
public class KeyGeneratorBody {

    // Character encoding used in key derivation
    private static final String CHARSET = "UTF-8";

    // AES encryption handler using CFB mode with no padding
    private static final AESCrypto aesCry = new AESCrypto("AES/CFB/NoPadding");

    // RSA encryption handler
    private static final RSACrypto rasCry = new RSACrypto();

    // Default RSA key length (modifiable)
    private Integer keyLength = 4096;

    /**
     * Constructor to initialize the key generator with a specified key length.
     * @param keyLength Length of the RSA key pair (e.g., 2048, 4096 bits).
     */
    public KeyGeneratorBody(Integer keyLength) {
        this.keyLength = keyLength;
    }

    /**
     * Writes a byte array to a file.
     * @param key Byte array representing the key.
     * @param filename Destination file path.
     */
    private void writeToFile(byte[] key, String filename) {
        System.out.println(filename);
        try (OutputStream out = new FileOutputStream(filename)) {
            // Writes the key bytes to the specified file
            out.write(key);
        } catch (IOException e) {
            throw new RuntimeException("Error during the key generation process! " +
                    "Ensure that you've chosen the correct key locations and have the necessary " +
                    "permissions to write to these locations!", e);
        }
    }

    /**
     * Generates an RSA key pair, encrypts the private key, and writes both keys to specified locations.
     * @param pin User-provided PIN used for AES encryption of the private key.
     * @param privatePath Directory where the encrypted private key should be saved.
     * @param publicPath Directory where the public key should be saved.
     * @param keyFileName Name of the encrypted private key file.
     * @throws Exception If an error occurs during key generation or encryption.
     */
    public void generatePair(String pin, String privatePath, String publicPath, String keyFileName) throws Exception {
        // Generate AES key from the provided PIN
        SecretKey passwordKey = aesCry.getKey(pin, 65536, 256, CHARSET);

        // Generate RSA key pair with the specified key length
        KeyPair rsaPair = rasCry.getPair(keyLength);

        // Encrypt and save the private key
        writeToFile(aesCry.encrypt(rsaPair.getPrivate().getEncoded(), passwordKey),
                privatePath + "\\" + keyFileName);

        // Save the public key
        writeToFile(rsaPair.getPublic().getEncoded(), publicPath + "publicKey");

        // Indicate successful key generation
        System.out.println("Generated");
    }
}
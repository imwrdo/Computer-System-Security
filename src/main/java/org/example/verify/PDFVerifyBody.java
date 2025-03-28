package org.example.verify;

import org.example.encryption.SignatureManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;

/**
 * The PDFVerifyBody class is responsible for verifying the authenticity of PDF documents
 * by validating their digital signatures using a public RSA key. This class utilizes
 * a SignatureManager instance to handle the signature verification process.
 */
public class PDFVerifyBody {

    // SignatureManager instance to handle signature verification
    private static final SignatureManager signatureManager = new SignatureManager();

    /**
     * Retrieves a public RSA key from a given FileInputStream.
     * This method reads the public key bytes and generates the corresponding PublicKey object.
     *
     * @param keyStream The FileInputStream containing the public key.
     * @return The PublicKey generated from the input stream.
     * @throws Exception If any error occurs while processing the key.
     */
    public PublicKey getPublicKey(FileInputStream keyStream) throws Exception {
        // Read all bytes of the public key from the input stream
        byte[] publicRSABytes = keyStream.readAllBytes();

        // Create a KeyFactory for RSA algorithm
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        // Create a key specification from the byte array of the public key
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicRSABytes);

        // Generate and return the PublicKey from the key specification
        return keyFactory.generatePublic(keySpec);
    }

    /**
     * Verifies the authenticity of a PDF document by checking its digital signature.
     * The method retrieves the public key, verifies the PDF using the SignatureManager,
     * and prints a message indicating whether the verification was successful.
     *
     * @param file The PDF file to verify.
     * @return True if the signature verification is successful, false otherwise.
     * @throws Exception If an error occurs during verification (e.g., file not found, invalid key).
     */
    public boolean verifyPDF(File file) throws Exception {
        // Open the public key file stream
        try (FileInputStream keyStream = new FileInputStream("publicKey")) {
            // Retrieve the public key from the stream
            PublicKey publicRSA = getPublicKey(keyStream);

            // Call the signatureManager to verify the PDF with the public key
            boolean isVerified = signatureManager.verifyPDF(file, publicRSA);

            // Provide feedback based on the verification result
            if (isVerified) {
                System.out.println("Everything is OK!");  // Signature is valid
            } else {
                System.out.println("Something went wrong"); // Signature is invalid
            }

            // Return the result of the verification process
            return isVerified;
        }
        // Handle the case where the public key file is not found
        catch (FileNotFoundException e) {
            throw new RuntimeException("Error during the verification process! Ensure that" +
                    " the folder you try to save file into exists and you have " +
                    "all the rights needed to save files there!", e);
        }
    }
}

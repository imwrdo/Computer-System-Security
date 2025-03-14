package org.example.Classes.VeriPart;

import org.example.Classes.Encryption.SignatureManager;

import java.io.File;
import java.io.FileInputStream;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;

public class PDFVeriBody {
    private static SignatureManager signatureManager = new SignatureManager();

    public void CheckKeys(PrivateKey rsaPrivate, PublicKey rsaPublic) throws Exception {
        String data = "This is some data to be signed.";
        byte[] dataBytes = data.getBytes();

        // Initialize the Signature object with the appropriate algorithm
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(rsaPrivate);
        signature.update(dataBytes);

        // Generate the signature
        byte[] signatureBytes = signature.sign();

        Signature signature2 = Signature.getInstance("SHA256withRSA");
        signature2.initVerify(rsaPublic);
        signature2.update(dataBytes);

        System.out.println("The keys are right: " + signature2.verify(signatureBytes));

    }

    public PublicKey GetPublicKey(FileInputStream keyStream) throws Exception {
        byte[] publicRSABytes = keyStream.readAllBytes();
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicRSABytes);
        return keyFactory.generatePublic(keySpec);
    }

    public boolean VerifyPDF(File file) throws Exception {

        try(FileInputStream keyStream = new FileInputStream("publicKey")) {
            PublicKey publicRSA = GetPublicKey(keyStream);
            //System.out.println(publicRSA);
            boolean something = signatureManager.VerifyPDF(file, publicRSA);
            if(something)
                System.out.println("Everything is OK!");
            else
                System.out.println("Something went wrong");

            return something;
        }
        catch(java.io.FileNotFoundException e) {
            throw new RuntimeException("Error during the verification process! Ensure that" +
                    " the folder you try to save file into exists and you have " +
                    "all the rights needed to save files there!", e);
        }
        // Get the PublicKey from the byte array

    }
}

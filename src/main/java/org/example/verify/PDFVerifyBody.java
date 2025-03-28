package org.example.verify;

import org.example.encryption.SignatureManager;

import java.io.File;
import java.io.FileInputStream;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;

public class PDFVerifyBody {
    private static final SignatureManager signatureManager = new SignatureManager();


    public PublicKey getPublicKey(FileInputStream keyStream) throws Exception {
        byte[] publicRSABytes = keyStream.readAllBytes();
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicRSABytes);
        return keyFactory.generatePublic(keySpec);
    }

    public boolean verifyPDF(File file) throws Exception {

        try(FileInputStream keyStream = new FileInputStream("publicKey")) {
            PublicKey publicRSA = getPublicKey(keyStream);
            //System.out.println(publicRSA);
            boolean something = signatureManager.verifyPDF(file, publicRSA);
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

    }
}

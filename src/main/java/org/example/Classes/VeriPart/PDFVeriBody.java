package org.example.Classes.VeriPart;


import org.example.Classes.Encryption.AESCrypto;
import org.example.Classes.Encryption.RSACrypto;
import org.example.Classes.Encryption.SignatureManager;

import javax.crypto.SecretKey;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
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

    public PublicKey GetPublicKey() throws Exception {
        try(FileInputStream keyStream = new FileInputStream("publicKey")) {
            byte[] publicRSABytes = keyStream.readAllBytes();
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicRSABytes);
            return keyFactory.generatePublic(keySpec);
        }
    }

    public void VerifyPDF(File file) throws Exception {

        try(FileInputStream keyStream = new FileInputStream("publicKey")) {
            PublicKey publicRSA = GetPublicKey();
            //System.out.println(publicRSA);
            if(signatureManager.VerifyPDF(file, publicRSA))
                System.out.println("Everything is OK!");
            else
                System.out.println("Something went wrong");
        }
        // Get the PublicKey from the byte array

    }
}

package org.example.Classes.CryptoPart;


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

public class CryptoMainBody {

    private Integer keyLength = 4096;
    private static AESCrypto aesCry = new AESCrypto("AES/CFB/NoPadding");
    private static RSACrypto rasCry = new RSACrypto();
    private static SignatureManager signatureManager = new SignatureManager();

    public CryptoMainBody(Integer keyLength) {
        this.keyLength = keyLength;
    };

    private void WriteToFile(byte[] key, String filename) throws Exception {
        FileOutputStream out = new FileOutputStream(filename);
        //System.out.println(Base64.getEncoder().encodeToString(key));
        out.write(key);
        out.close();
    }

    public void GeneratePair(String pin) throws Exception {
        SecretKey passwordKey = aesCry.GetKey(pin, 65536, 256, "UTF-8");
        KeyPair rsaPair = rasCry.GetPair(keyLength);
        //System.out.println(rsaPair.getPrivate());
        WriteToFile(aesCry.Encrypt(rsaPair.getPrivate().getEncoded(), passwordKey), "privateKey");
        //WriteToFile(Base64.getEncoder().encodeToString(rsaPair.getPublic().getEncoded()), "publicKey");
        //System.out.println(Arrays.toString(rsaPair.getPublic().getEncoded()));
        WriteToFile(rsaPair.getPublic().getEncoded(), "publicKey");
        //System.out.println(rsaPair.getPublic());
        System.out.println("Generated");

        //CheckKeys(rsaPair.getPrivate(), rsaPair.getPublic());
    }

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

    public PrivateKey GetPrivateKey(String pin) throws Exception {
        try(FileInputStream keyStream = new FileInputStream("privateKey")) {
            byte[] privateRSABytes = aesCry.Decrypt(keyStream.readAllBytes(),
                    aesCry.GetKey(pin, 65536, 256, "UTF-8"));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateRSABytes);
            return keyFactory.generatePrivate(keySpec);
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

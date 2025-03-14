package org.example.Classes.CryptoPart;


import org.example.Classes.Encryption.AESCrypto;
import org.example.Classes.Encryption.RSACrypto;
import org.example.Classes.Encryption.SignatureManager;
import org.example.Classes.USBSeeker.USBSeeker;

import javax.crypto.SecretKey;
import java.io.*;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class PDFCryptoBody {

    private Integer keyLength = 4096;
    private static AESCrypto aesCry = new AESCrypto("AES/CFB/NoPadding");
    private static RSACrypto rasCry = new RSACrypto();
    private static SignatureManager signatureManager = new SignatureManager();
    private static USBSeeker usbSeeker = new USBSeeker();

    public PDFCryptoBody(Integer keyLength, String keyFileName) {
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
        //System.out.println(Arrays.toString(rsaPair.getPublic().getEncoded()));
        WriteToFile(rsaPair.getPublic().getEncoded(), "publicKey");
        //System.out.println(rsaPair.getPublic());
        System.out.println("Generated");

        //CheckKeys(rsaPair.getPrivate(), rsaPair.getPublic());
    }


    public PrivateKey GetPrivateKey(String pin, String keyPath) throws Exception {
        try(FileInputStream keyStream = new FileInputStream(keyPath)) {
            byte[] privateRSABytes = aesCry.Decrypt(keyStream.readAllBytes(),
                    aesCry.GetKey(pin, 65536, 256, "UTF-8"));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateRSABytes);
            PrivateKey ret = keyFactory.generatePrivate(keySpec);
            return ret;
        }
        catch (java.io.FileNotFoundException e) {
            throw new RuntimeException("The PENDRIVE with the PRIVATE KEY wasn't found! Ensure that " +
                    "you have your pendrive inserted and have all the rights to read it!", e);
        }
        catch (java.security.spec.InvalidKeySpecException e) {
            throw new RuntimeException("Wrong pin!", e);
        }
    }

    public String ScanForKey(String keyFileName) {
        return usbSeeker.GetKeyPath(keyFileName);
    }

    public boolean SignPDF(String pin, String origPath, String signedPath, String keyPath) throws Exception {
        PrivateKey privateRSA = GetPrivateKey(pin, keyPath);
        return signatureManager.SignPDF(origPath, signedPath, privateRSA);
    }
}

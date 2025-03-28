package org.example.crypto;


import org.example.encryption.AESCrypto;
import org.example.encryption.RSACrypto;
import javax.crypto.SecretKey;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.*;

public class KeyGeneratorBody {
    private static final String CHARSET = "UTF-8";
    private static final AESCrypto aesCry = new AESCrypto("AES/CFB/NoPadding");
    private static final RSACrypto rasCry = new RSACrypto();

    private Integer keyLength = 4096;

    public KeyGeneratorBody(Integer keyLength) {
        this.keyLength = keyLength;
    };

    private void writeToFile(byte[] key, String filename) {
        try(OutputStream out = new FileOutputStream(filename)){
            //System.out.println(Base64.getEncoder().encodeToString(key));
            out.write(key);
        }
        catch(IOException e) {
            throw new RuntimeException("Error during the key generation process! " +
                    "Ensure that you've chosen the correct key locations and have the rights" +
                    " to write to this locations!", e);
        }
    }

    public void generatePair(String pin, String privatePath, String publicPath, String keyFileName) throws Exception {
        SecretKey passwordKey = aesCry.getKey(pin, 65536, 256, CHARSET);
        KeyPair rsaPair = rasCry.getPair(keyLength);
        writeToFile(aesCry.encrypt(rsaPair.getPrivate().getEncoded(), passwordKey),
                privatePath + "\\" + keyFileName);
        writeToFile(rsaPair.getPublic().getEncoded(), publicPath + "\\publicKey");
        System.out.println("Generated");
    }

}

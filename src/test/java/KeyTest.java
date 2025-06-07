import org.example.encryption.AESCrypto;
import org.example.encryption.RSACrypto;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.Mockito;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.util.Random;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


public class KeyTest {
    private static RSACrypto rsa;
    private static String aesParams = "AES/CFB/NoPadding";
    @Mock
    private static AESCrypto aes;
    private static Random rnd;
    @BeforeAll
    static void setUp(){
        rsa = new RSACrypto();
        aes = Mockito.spy(new AESCrypto(aesParams));
        rnd = new Random();
    }



    @Test
    void CreationTest() {
            Assertions.assertDoesNotThrow(() -> rsa.getPair(4096));
    }
    @Test
    void KeyPairTest(){
        try {
            KeyPair keys = rsa.getPair(4096);
            String plainText = TestDataManager.GenerateRandomString(rnd.nextInt(1000, 20000));
            Cipher cipher = Cipher.getInstance("RSA");

            cipher.init(Cipher.ENCRYPT_MODE, keys.getPrivate());
            byte[] encrypted = cipher.doFinal(plainText.getBytes());

            cipher.init(Cipher.DECRYPT_MODE, keys.getPublic());
            Assertions.assertArrayEquals(plainText.getBytes(), cipher.doFinal(encrypted));
        }
        catch(Exception e) {}
    }
    @Test
    void KeyPairMixTest() throws Exception{
        try {
            KeyPair keys = rsa.getPair(4096);
            KeyPair keys2 = rsa.getPair(4096);

            String plainText = TestDataManager.GenerateRandomString(400);
            Cipher cipher = Cipher.getInstance("RSA");
            Cipher cipher2 = Cipher.getInstance("RSA");

            cipher.init(Cipher.ENCRYPT_MODE, keys.getPrivate());
            cipher2.init(Cipher.ENCRYPT_MODE, keys2.getPrivate());
            byte[] encrypted = cipher.doFinal(plainText.getBytes());
            byte[] encrypted2 = cipher2.doFinal(plainText.getBytes());

            cipher.init(Cipher.DECRYPT_MODE, keys2.getPublic());
            cipher2.init(Cipher.DECRYPT_MODE, keys.getPublic());
            Assertions.assertAll(

                    () -> Assertions.assertThrows(Exception.class, () -> cipher.doFinal(encrypted)),
                    () -> Assertions.assertThrows(Exception.class, () -> cipher2.doFinal(encrypted2))
            );
        }
        catch(Exception e) {
            throw e;
        }
    }
    @Test
    void PrivateEncryptionTest() {
        try{
            int keyLength = 256;
            int aesIterations = rnd.nextInt(65536);
            String charset = "UTF-8";

            KeyPair keys = rsa.getPair(4096);
            String pin = TestDataManager.GenerateRandomString(8);

            SecretKey aesSecret = aes.getKey(pin, aesIterations, keyLength, charset);
            byte[] enctyptedPrivate = aes.encrypt(keys.getPrivate().getEncoded(), aesSecret);
            byte[] IV = new byte[16];
            System.arraycopy(enctyptedPrivate, 0, IV, 0, IV.length);

            byte[] decryptedPrivate = aes.decrypt(enctyptedPrivate, aesSecret);
            Assertions.assertArrayEquals(keys.getPrivate().getEncoded(), decryptedPrivate);
            verify(aes, times(1)).restoreIv(IV);
        }
        catch(Exception e) {}
    }
}

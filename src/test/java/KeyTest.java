import org.example.encryption.AESCrypto;
import org.example.encryption.RSACrypto;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Mockito;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Random;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


public class KeyTest {
    private static String aesParams = "AES/CFB/NoPadding";
    @Mock
    private static AESCrypto aes;
    private static Random rnd;
    @BeforeAll
    static void setUp(){
        aes = Mockito.spy(new AESCrypto(aesParams));
        rnd = new Random(12345);
    }


    /**
     * Tests for RSA key generation.
     * Must end successfully to pass the test
     */
    @Test
    void CreationTest() {
            Assertions.assertDoesNotThrow(() -> TestDataManager.GetKeys(false));
    }

    /**
     * Tests for generated RSA key pair compliance.
     * Must end successfully to pass the test
     */
    @Test
    void KeyPairTest() {
        try {
            KeyPair keys = TestDataManager.GetKeys(false);
            String plainText = TestDataManager.GenerateRandomString(501);
            Cipher cipher = Cipher.getInstance("RSA");

            cipher.init(Cipher.ENCRYPT_MODE, keys.getPrivate());
            byte[] encrypted = cipher.doFinal(plainText.getBytes());

            cipher.init(Cipher.DECRYPT_MODE, keys.getPublic());
            Assertions.assertArrayEquals(plainText.getBytes(), cipher.doFinal(encrypted));
        } catch (Exception e) {
            Assertions.fail(e);
        }
    }

    /**
     * Tests for the incompliance of private and public keys from different RSA key pairs.
     * Must throw an error to pass the test
     */
    @Test
    void MixedKeyPairTest() {
        try {
            KeyPair keys = TestDataManager.GenerateSeededKeyPair(4096, 12345);
            KeyPair keys2 = TestDataManager.GenerateSeededKeyPair(4096, 67890);

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
        } catch (Exception e) {
            Assertions.fail(e);
        }
    }

    /**
     * Tests for signature and verification with corrupted keys (only one key in a pair is correct) several times.
     * Must throw an error in every case to pass the test
     */
    @ParameterizedTest
    @ValueSource(ints = {114, 3256, 68734, 762446, 2352376})
    void CorruptedKeyPairTest(int rndSeed) {
        try {
            KeyPair keys = TestDataManager.GetKeys(false);
            KeyPair keys2 = TestDataManager.GetKeys(false);

            byte[] privateBytes = keys.getPrivate().getEncoded();
            byte[] publicBytes2 = keys2.getPublic().getEncoded();

            Random rnd2 = new Random(rndSeed);
            int bait = rnd2.nextInt(privateBytes.length), bait2 = rnd2.nextInt(publicBytes2.length);
            System.out.println("Chosen bytes for Key Corruption: " + bait + "(private) | " + bait2 + "(public)");
            privateBytes[bait] ^= 1;
            publicBytes2[bait2] ^= 1;

            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(privateBytes);
            PKCS8EncodedKeySpec keySpec2 = new PKCS8EncodedKeySpec(publicBytes2);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            Assertions.assertAll(
                    () -> Assertions.assertThrows(InvalidKeySpecException.class,
                            () -> keyFactory.generatePrivate(keySpec)),
                    () -> Assertions.assertThrows(InvalidKeySpecException.class,
                            () -> keyFactory.generatePublic(keySpec2))
            );
        } catch (Exception e) {
            Assertions.fail(e);
        }
    }

    /**
     * Tests for RSA key verification of a corrupted cypher several times.
     * Must throw error in every case to pass the test
     */
    @ParameterizedTest
    @ValueSource(ints = {114, 3256, 68734, 762446, 2352376})
    void KeyPairWithBrokenCipherTest(Integer rndSeed) {
        try {
            KeyPair keys = TestDataManager.GetKeys(false);
            Random rnd2 = new Random(rndSeed);

            String plainText = TestDataManager.GenerateRandomString(400);
            Cipher cipher = Cipher.getInstance("RSA");

            cipher.init(Cipher.ENCRYPT_MODE, keys.getPrivate());
            byte[] encrypted = cipher.doFinal(plainText.getBytes());

            Integer bait = rnd.nextInt(rnd2.nextInt(400));
            System.out.println("Byte chosen for cypher corruption: " + bait);
            encrypted[bait] ^= 1;

            cipher.init(Cipher.DECRYPT_MODE, keys.getPublic());
            Assertions.assertThrows(BadPaddingException.class, () -> cipher.doFinal(encrypted));
        } catch (Exception e) {
            Assertions.fail(e);
        }
    }

    /**
     * Tests for RSA private key encryption and decryption.
     * Must end successfully to pass the test
     */
    @Test
    void PrivateEncryptionTest() {
        try {
            int keyLength = 256;
            int aesIterations = rnd.nextInt(65536);
            String charset = "UTF-8";

            KeyPair keys = TestDataManager.GetKeys(false);
            String pin = TestDataManager.GenerateRandomString(8);

            SecretKey aesSecret = aes.getKey(pin, aesIterations, keyLength, charset);
            byte[] enctyptedPrivate = aes.encrypt(keys.getPrivate().getEncoded(), aesSecret);
            byte[] IV = new byte[16];
            System.arraycopy(enctyptedPrivate, 0, IV, 0, IV.length);

            byte[] decryptedPrivate = aes.decrypt(enctyptedPrivate, aesSecret);
            Assertions.assertArrayEquals(keys.getPrivate().getEncoded(), decryptedPrivate);
            verify(aes, times(1)).restoreIv(IV);
        } catch (Exception e) {
            Assertions.fail(e);
        }
    }

}

import org.example.encryption.RSACrypto;
import org.example.encryption.SignatureManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.spec.InvalidKeySpecException;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class SignTest {
    @Mock
    private static SignatureManager sigManager;
    private static RSACrypto rsa;

    @BeforeAll
    static void setUp() {
        rsa = new RSACrypto();
    }

    @BeforeEach
    void RenewSignatureManager() {
        sigManager = Mockito.spy(new SignatureManager());
    }

    /**
     * Tests signing without the source ot destination file.
     * Must both cases must throw an error to pass the test
     */
    @Test
    void SignWithoutFilesTest() {
        try {
            KeyPair keys = rsa.getPair(4096);

            Assertions.assertAll(
                    () -> Assertions.assertThrows(RuntimeException.class,
                            () -> sigManager.signPDF(null,
                                    "SomeSignedName.pdf", keys.getPrivate())),
                    () -> Assertions.assertThrows(RuntimeException.class,
                            () -> sigManager.signPDF(TestDataManager.GetPDF("PDF"),
                                    null, keys.getPrivate()))
            );
            verify(sigManager, times(0)).
                    changeOneByte(new File(TestDataManager.GetPathToSignTo()));
        } catch (Exception e) {
            Assertions.fail(e);
        }
    }

    /**
     * Tests for signing the nonexistent file.
     * Must throw an error to pass the test
     */
    @Test
    void SignNonexistentFilesTest() {
        try {
            KeyPair keys = rsa.getPair(4096);

            Assertions.assertThrows(RuntimeException.class,
                    () -> sigManager.signPDF("Wrongpath.pdf",
                            "Wrongpath2.pdf", keys.getPrivate()));

            verify(sigManager, times(0)).
                    changeOneByte(new File(TestDataManager.GetPathToSignTo()));
        } catch (Exception e) {
            Assertions.fail(e);
        }
    }

    /**
     * Tests for signing file with wrong.
     * Must throw an error to pass the test
     */
    @Test
    void SignWrongFileExtensionTest() {
        try {
            KeyPair keys = rsa.getPair(4096);

            Assertions.assertThrows(RuntimeException.class,
                    () -> sigManager.signPDF(TestDataManager.GetWrongExtension(),
                            "Wrongpath2.pdf", keys.getPrivate()));

            verify(sigManager, times(0)).
                    changeOneByte(new File(TestDataManager.GetPathToSignTo()));
        } catch (Exception e) {
            Assertions.fail(e);
        }
    }

    /**
     * Tests for signing without the RSA private key.
     * Must throw an error to pass the test
     */
    @Test
    void SignWithoutKeyTest() {
        try {
            KeyPair keys = rsa.getPair(4096);

            Assertions.assertThrows(RuntimeException.class,
                    () -> sigManager.signPDF(TestDataManager.GetPDF("PDF"),
                            TestDataManager.GetPathToSignTo(), null));

            verify(sigManager, times(0)).
                    changeOneByte(new File(TestDataManager.GetPathToSignTo()));
        } catch (Exception e) {
            Assertions.fail(e);
        }
    }

    /**
     * Tests for signing the document using broken private key.
     * Must throw an error to pass the test
     */
    @Test
    void SignWithBrokenKeyTest() {
        try {
            Assertions.assertThrows(InvalidKeySpecException.class,
                    () -> TestDataManager.GetKeys(true));
        } catch (Exception e) {
            Assertions.fail(e);
        }
    }

    /**
     * Tests for correct procedure of signature creation.
     * Must end successfully to pass the test
     */
    @Test
    void CorrectSignTest() {
        try {
            KeyPair keys = rsa.getPair(4096);


            Assertions.assertAll(
                    () -> Assertions.assertTrue(sigManager.signPDF(TestDataManager.GetPDF("PDF"),
                            TestDataManager.GetPathToSignTo(), keys.getPrivate())),
                    () -> Assertions.assertTrue(Files.exists(Path.of(TestDataManager.GetPathToSignTo())))
            );
            verify(sigManager, times(1)).
                    changeOneByte(new File(TestDataManager.GetPathToSignTo()));
<<<<<<< Updated upstream


        } catch (Exception e) {
=======
        }
        catch (Exception e) {
>>>>>>> Stashed changes
            Assertions.fail(e);
        }
    }
}

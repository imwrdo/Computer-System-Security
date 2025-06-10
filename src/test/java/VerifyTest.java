import org.example.encryption.SignatureManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.File;
import java.security.spec.InvalidKeySpecException;

public class VerifyTest {
    @Mock
    private static SignatureManager sigManager;
    @BeforeEach
    void smallSetUp() {
        sigManager = Mockito.spy(new SignatureManager());
    }

    /**
     * Tests for verification without the chosen document.
     * Must throw an error to pass the test
     */
    @Test
    void VerifyWithoutFile() {
        Assertions.assertThrows(NullPointerException.class,
                () -> sigManager.verifyPDF(null, TestDataManager.GetKeys(false).getPublic()));
    }

    /**
     * Tests for verification without an RSA public key.
     * Must throw an error to pass the test
     */
    @Test
    void VerifyWithoutKey() {
        Assertions.assertThrows(RuntimeException.class,
                () -> sigManager.verifyPDF(new File(TestDataManager.GetPDF("signed")), null));
    }

    /**
     * Tests for verification of an unsigned document.
     * Must throw an error to pass the test
     */
    @Test
    void VerifyUnsigned() {
        Assertions.assertThrows(RuntimeException.class,
                () -> sigManager.verifyPDF(new File(TestDataManager.GetPDF("PDF")), null));
    }

    /**
     * Tests for verification of a file with wrong extension.
     * Must throw an error to pass the test
     */
    @Test
    void VerifyWrongExtension(){
        Assertions.assertThrows(RuntimeException.class,
                () -> sigManager.verifyPDF(new File(TestDataManager.GetWrongExtension()),
                        TestDataManager.GetKeys(false).getPublic()));
    }

    /**
     * Tests for verification of signed document with corrupted public key and public key from another pair.
     * Must throw an error in the first case and return false in the second to pass the test
     */
    @Test
    void VerifyWithBrokenAndOtherKeysTest() {
        try {
            boolean verifiedWithInappropriateKey = sigManager.verifyPDF(
                    new File(TestDataManager.GetPDF("signed")),
                    TestDataManager.GenerateSeededKeyPair(4096, 4753).getPublic());
            Assertions.assertAll(
                    () -> Assertions.assertThrows(InvalidKeySpecException.class, () ->
                            sigManager.verifyPDF(new File(TestDataManager.GetPDF("signed")),
                                TestDataManager.GetKeys(true).getPublic())),
                    () -> Assertions.assertFalse(verifiedWithInappropriateKey)
            );
        } catch (Exception e) {
            Assertions.fail(e);
        }
    }

    /**
     * Tests for verification of signed and corrupted documents using the correct public key.
     * Must return true for the rightly signed document and false for the corrupted one to pass the test
     */
    @Test
    void VerigySignedAndCorruptedTest() {
        Assertions.assertAll(
                () -> Assertions.assertTrue(sigManager.verifyPDF(new File(TestDataManager.GetPDF("signed")),
                        TestDataManager.GetKeys(false).getPublic())),
                () -> Assertions.assertFalse(sigManager.verifyPDF(
                        new File(TestDataManager.GetPDF("corrupted")),
                        TestDataManager.GetKeys(false).getPublic()))
        );
    }
}

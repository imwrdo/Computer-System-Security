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

    @Test
    void VerifyWithoutFile() {
        Assertions.assertThrows(NullPointerException.class,
                () -> sigManager.verifyPDF(null, TestDataManager.GetKeys(false).getPublic()));
    }
    @Test
    void VerifyWithoutKey() {
        Assertions.assertThrows(RuntimeException.class,
                () -> sigManager.verifyPDF(new File(TestDataManager.GetPDF("signed")), null));
    }
    @Test
    void VerifyUnsigned() {
        Assertions.assertThrows(RuntimeException.class,
                () -> sigManager.verifyPDF(new File(TestDataManager.GetPDF("PDF")), null));
    }
    @Test
    void VerifyWithBrokenKeyTest() {
        try {
            Assertions.assertThrows(InvalidKeySpecException.class, () ->
                    sigManager.verifyPDF(new File(TestDataManager.GetPDF("signed")),
                        TestDataManager.GetKeys(true).getPublic())
            );
        }
        catch (Exception e) {}
    }
    @Test
    void TestSignedAndCorrupted() throws Exception {
        Assertions.assertAll(
                () -> Assertions.assertTrue(sigManager.verifyPDF(new File(TestDataManager.GetPDF("signed")),
                        TestDataManager.GetKeys(false).getPublic())),
                () -> Assertions.assertFalse(sigManager.verifyPDF(
                        new File(TestDataManager.GetPDF("corrupted")),
                        TestDataManager.GetKeys(false).getPublic()))
        );
    }
}

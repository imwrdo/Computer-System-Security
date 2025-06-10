import org.apache.pdfbox.pdmodel.PDDocument;
import org.example.encryption.SignatureManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.File;
import java.lang.reflect.Method;

public class HashingTest {
    @Mock
    private static SignatureManager sigManager;

    @BeforeEach
    void RenewSignatureManager() {
        sigManager = Mockito.spy(new SignatureManager());
    }

    /**
     * Gets the private method of a SignatureManager and makes it public.
     */
    Method GetHashMethod() throws Exception {
        Method hashMethod = sigManager.getClass().getDeclaredMethod("getChosenHash", PDDocument.class);
        hashMethod.setAccessible(true);

        return hashMethod;
    }

    /**
     * Returns the hash of the base PDF or corrupted document
     */
    byte[] GetHash(boolean broken) throws Exception {
        try (PDDocument document = PDDocument.load(
                new File(TestDataManager.GetPDF(broken ? "corrupted" : "PDF")))) {
            Method hashMethod = GetHashMethod();
            return (byte[]) hashMethod.invoke(sigManager, document);
        }
    }

    /**
     * Tests for error-free execution of hasing function.
     * Must end successfully to pass the test
     */
    @Test
    void HashDoesNotThrowTest() {
        try {
            Assertions.assertDoesNotThrow(() -> GetHash(false));
        } catch (Exception e) {
            Assertions.fail(e);
        }
    }

    /**
     * Tests the hashing function is deterministic.
     * Must end successfully to pass the test
     */
    @Test
    void HashSameTwiceTest() {
        try {

            byte[] hash1 = GetHash(false);
            byte[] hash2 = GetHash(false);

            Assertions.assertArrayEquals(hash1, hash2);
        } catch (Exception e) {
            Assertions.fail(e);
        }
    }

    /**
     * Tests for Hashing function resistance to repeats.
     * Must end successfully to pass the test
     */
    @Test
    void TwoDocsNotEqualHashTest() {
        try {

            byte[] hash1 = GetHash(false);
            byte[] hash2 = GetHash(true);

            Assertions.assertNotEquals(hash1, hash2);
        } catch (Exception e) {
            Assertions.fail(e);
        }
    }


}

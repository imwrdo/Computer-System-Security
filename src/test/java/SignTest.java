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
    @Test
    void SignWithoutFilesTest() {
        try {
            KeyPair keys = rsa.getPair(4096);

            Assertions.assertThrows(RuntimeException.class,
                    () -> sigManager.signPDF(null, null, keys.getPrivate()));
            verify(sigManager, times(0)).
                    changeOneByte(new File(TestDataManager.GetPathToSignTo()));
        }
        catch (Exception e) {}
    }

    @Test
    void SignWrongFilesTest() {
        try {
            KeyPair keys = rsa.getPair(4096);

            Assertions.assertThrows(RuntimeException.class,
                    () -> sigManager.signPDF("Wrongpath.pdf",
                            "Wrongoath2.pdf", keys.getPrivate()));

            verify(sigManager, times(0)).
                    changeOneByte(new File(TestDataManager.GetPathToSignTo()));
        }
        catch (Exception e) {}
    }

    @Test
    void SignWithoutKeyTest() {
        try {
            KeyPair keys = rsa.getPair(4096);

            Assertions.assertThrows(RuntimeException.class,
                    () -> sigManager.signPDF(TestDataManager.GetPDF("PDF"),
                            TestDataManager.GetPathToSignTo(), null));

            verify(sigManager, times(0)).
                    changeOneByte(new File(TestDataManager.GetPathToSignTo()));
        }
        catch (Exception e) {}
    }

    @Test
    void SignWithBrokenKeyTest() {
        try {
            KeyPair keys = TestDataManager.GetKeys(true);

            Assertions.assertThrows(RuntimeException.class,
                    () -> sigManager.signPDF(TestDataManager.GetPDF("PDF"),
                            TestDataManager.GetPathToSignTo(), keys.getPrivate()));

            verify(sigManager, times(0)).
                    changeOneByte(new File(TestDataManager.GetPathToSignTo()));
        }
        catch (Exception e) {}
    }

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


        }
        catch (Exception e) {}
    }
}

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.example.encryption.SignatureManager;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Random;

/**
 * Handling all the file creation and key generation logic in tests
 */
public class TestDataManager {
    //Main folder for test input and output data (generating automatically when tests start)
    private static final String dataFolderPath = "Test_Data";
    //Input and output files for unit tests (generating automatically)
    private static final String inDataPath = dataFolderPath + "/In";
    private static final String outDataPath = dataFolderPath + "/Out";


    //File paths of correct key pair
    private static final String publicPath = inDataPath + "/publicKey";
    private static final String privatePath = inDataPath + "/privateKey";

    //File paths of key pair with one changed bit each key
    private static final String publicPathBroken = inDataPath + "/publicKeyBroken";
    private static final String privatePathBroken = inDataPath + "/privateKeyBroken";

    //File paths of unsigned pdf, signed pdf and corrupted (changed body) signed pdf documents
    private static final String pdfPath = inDataPath + "/doc.pdf";
    private static final String signedPath = inDataPath + "/encrypted.pdf";
    private static final String corruptedPath = inDataPath + "/corrupted.pdf";

    //File path for generating file with wrong extension
    private static final String wrongExtension = inDataPath + "/someFile.txt";

    //File path for creating signed document in SignTest
    private static final String signedWrite = outDataPath + "/doc_encrypted.pdf";

    //Seeded random and seed for key generation
    private static final Random rnd = new Random(12345);
    private static final Integer chosenRSASeed = 98563;

    /**
     * Generates (if not already present and complete) all the needed file structure for tests.
     */
    private synchronized static void CheckTestFolders() throws Exception{
        if(!Files.exists(Path.of(dataFolderPath)))
            Files.createDirectory(Path.of(dataFolderPath));
        if(!Files.exists(Path.of(inDataPath)))
            Files.createDirectory(Path.of(inDataPath));
        if(!Files.exists(Path.of(outDataPath)))
            Files.createDirectory(Path.of(outDataPath));
    }

    /**
     * Generates a string of random symbols that can be represented in PDF (Times New Roman).
     *
     * @param length Length of the returned string
     * @return random string of a chosen length.
     */
    public static String GenerateRandomString(int length) {
        StringBuilder pin = new StringBuilder();
        for (int i = 0; i < length; i++)
            pin.append((char) rnd.nextInt(32, 127));
        return pin.toString();
    }

    /**
     * Generates an RSA key pair of the specified length using seeded key generator.
     *
     * @param length  The key length (e.g., 2048, 3072, 4096).
     * @param rndSeed The chosen seed for the key generator
     * @return The generated RSA key pair.
     */
    public static KeyPair GenerateSeededKeyPair(Integer length, Integer rndSeed) throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");

        SecureRandom seededRnd = SecureRandom.getInstance("SHA1PRNG");
        seededRnd.setSeed(rndSeed);

        kpg.initialize(length, seededRnd);
        return kpg.generateKeyPair();
    }

    /**
     * If not existed before - generates correct and broken seeded RSA keys of 4096-bit length.
     */
<<<<<<< Updated upstream
    private synchronized static void GenerateTestKeys() throws Exception {
        if (!Files.exists(Path.of(publicPath)) || !Files.exists(Path.of(privatePath))) {
=======
    private synchronized static void GenerateTestKeys() throws Exception{

        if(!Files.exists(Path.of(publicPath)) || !Files.exists(Path.of(privatePath))) {
>>>>>>> Stashed changes
            KeyPair keys = GenerateSeededKeyPair(4096, chosenRSASeed);

            try (FileOutputStream publicStream = new FileOutputStream(publicPath);
                 FileOutputStream privateStream = new FileOutputStream(privatePath)) {
                publicStream.write(keys.getPublic().getEncoded());
                privateStream.write(keys.getPrivate().getEncoded());
            }

            try (FileOutputStream publicStreamBroken = new FileOutputStream(publicPathBroken);
                 FileOutputStream privateStreamBroken = new FileOutputStream(privatePathBroken)) {
                byte[] publicBroken = keys.getPublic().getEncoded();
                byte[] privateBroken = keys.getPrivate().getEncoded();

                publicBroken[0] ^= 1;
                privateBroken[0] ^= 1;

                publicStreamBroken.write(publicBroken);
                privateStreamBroken.write(privateBroken);
            }
        }
    }

    /**
     * If not existed before - generates a random texted PDF for the SignTest.
     */
    private synchronized static void GenerateSTestPDF() throws Exception {
        if (!Files.exists(Path.of(pdfPath))) {
            try (PDDocument document = new PDDocument()) {
                PDPage page = new PDPage(PDRectangle.A4);
                document.addPage(page);

                try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                    contentStream.beginText();
                    contentStream.setFont(PDType1Font.TIMES_ROMAN, 11);
                    contentStream.newLineAtOffset(100, 750);
                    contentStream.showText(GenerateRandomString(rnd.nextInt(100000, 500000)));
                    contentStream.endText();
                }

                document.save(pdfPath);
            }
        }
    }

    /**
     * If not existed before - generates signed and corrupted versions of PDF generated by the method above.
     */
    private synchronized static void GenerateVTestPDF() throws Exception {
        if (!Files.exists(Path.of(signedPath)) || !Files.exists(Path.of(corruptedPath))) {
            if (!Files.exists(Path.of(pdfPath)))
                GenerateSTestPDF();
            SignatureManager sigManager = new SignatureManager();
            sigManager.signPDF(pdfPath, signedPath, GetKeys(false).getPrivate());
            Files.move(
                    Path.of(signedPath.replace(".pdf", "_corrupted.pdf")),
                    Path.of(signedPath.replace("encrypted.pdf",
                            "corrupted.pdf")),
                    StandardCopyOption.REPLACE_EXISTING
            );
        }
    }

    /**
     * If not existed before - generates a file *.txt.
     */
    private synchronized static void GenerateWrongExtension() throws Exception {
        if (!Files.exists(Path.of(wrongExtension)))
            try (FileOutputStream outFile = new FileOutputStream(wrongExtension)) {
                outFile.write(GenerateRandomString(5000).getBytes());
            }
    }

    /**
     * Returns the file path to the file of the wrong extension.
     * Generates the new file if there is no such existing.
     */
<<<<<<< Updated upstream
    public static String GetWrongExtension() throws Exception {
        if (!Files.exists(Path.of(wrongExtension)))
=======
    public static String GetWrongExtension() throws Exception{
        CheckTestFolders();
        if(!Files.exists(Path.of(wrongExtension)))
>>>>>>> Stashed changes
            GenerateWrongExtension();
        return wrongExtension;
    }

    /**
     * Returns the generated earlier RSA key pair.
     * Generates the new keys if there is no such existing.
     *
     * @param broken Whether to return the correct or broken keys.
     * @return The key pair needed.
     */
    public static KeyPair GetKeys(boolean broken) throws Exception {
<<<<<<< Updated upstream
        if (!Files.exists(Path.of(publicPath)) ||
=======
        CheckTestFolders();
        if(!Files.exists(Path.of(publicPath)) ||
>>>>>>> Stashed changes
                !Files.exists(Path.of(privatePath)) || !Files.exists(Path.of(publicPathBroken)) ||
                !Files.exists(Path.of(privatePathBroken))) {
            GenerateTestKeys();
        }
        String pubPath = (broken ? publicPathBroken : publicPath);
        String privPath = (broken ? privatePathBroken : privatePath);

        try (FileInputStream publicStream = new FileInputStream(pubPath);
             FileInputStream privateStream = new FileInputStream(privPath)) {
            byte[] publicBytes = publicStream.readAllBytes();
            byte[] privateBytes = privateStream.readAllBytes();

            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
            PKCS8EncodedKeySpec keySpec2 = new PKCS8EncodedKeySpec(privateBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            return new KeyPair(keyFactory.generatePublic(keySpec), keyFactory.generatePrivate(keySpec2));
        }
    }

    /**
     * Returns the path to the PDF file of desired type.
     *
     * @param type The type of the desired document (usigned, signed and corrupted).
     * @return The PDF document needed.
     */
    public static String GetPDF(String type) throws Exception {
<<<<<<< Updated upstream
        switch (type) {
            case "PDF":
                if (!Files.exists(Path.of(pdfPath)))
                    GenerateSTestPDF();

                return pdfPath;
            case "signed":
                if (!Files.exists(Path.of(signedPath)))
                    GenerateVTestPDF();
                return signedPath;
            case "corrupted":
=======
        CheckTestFolders();
        return switch (type) {
            case "PDF" -> {
                if (!Files.exists(Path.of(pdfPath)))
                    GenerateSTestPDF();

                yield pdfPath;
            }
            case "signed" -> {
                if (!Files.exists(Path.of(signedPath)))
                    GenerateVTestPDF();
                yield signedPath;
            }
            case "corrupted" -> {
>>>>>>> Stashed changes
                if (!Files.exists(Path.of(corruptedPath)))
                    GenerateVTestPDF();
                yield corruptedPath;
            }
            default -> type;
        };
    }

    /**
     * Returns the designated path to write signed document to.
     */
    public static String GetPathToSignTo() {
        return signedWrite;
    }
}

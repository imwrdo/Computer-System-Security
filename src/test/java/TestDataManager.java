import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.example.encryption.RSACrypto;
import org.example.encryption.SignatureManager;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Random;

public class TestDataManager {
    private static final String publicPath = "testInData/publicKey";
    private static final String privatePath = "testInData/privateKey";
    private static final String publicPathBroken = "testInData/publicKeyBroken";
    private static final String privatePathBroken = "testInData/privateKeyBroken";
    private static final String pdfPath = "testInData/doc.pdf";
    private static final String signedPath = "testInData/encrypted.pdf";
    private static final String corruptedPath = "testInData/corrupted.pdf";
    private static final String signedWrite = "testOutData/doc_encrypted.pdf";


    private static final Random rnd = new Random();

    public static String GenerateRandomString(int length) {
        StringBuilder pin = new StringBuilder();
        for(int i = 0; i < length; i++)
            pin.append((char) rnd.nextInt(32, 127));
        return pin.toString();
    }

    private synchronized static void GenerateTestKeys() throws Exception{
        if(!Files.exists(Path.of(publicPath)) || !Files.exists(Path.of(privatePath))) {
            KeyPair keys = (new RSACrypto()).getPair(4096);

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
    private synchronized static void GenerateSTestPDF() throws Exception {
        if(!Files.exists(Path.of(pdfPath))) {
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
    private synchronized static void GenerateVTestPDF() throws Exception {
        if(!Files.exists(Path.of(signedPath)) || !Files.exists(Path.of(corruptedPath))) {
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
    public static KeyPair GetKeys(boolean broken) throws Exception {
        if(!Files.exists(Path.of(publicPath)) ||
                !Files.exists(Path.of(privatePath)) || !Files.exists(Path.of(publicPathBroken)) ||
                !Files.exists(Path.of(privatePathBroken))) {
            GenerateTestKeys();
        }
        String pubPath = (broken? publicPathBroken : publicPath);
        String privPath = (broken? privatePathBroken : privatePath);

        try (FileInputStream publicStream = new FileInputStream(pubPath);
             FileInputStream privateStream = new FileInputStream(privPath))
        {
            byte[] publicBytes = publicStream.readAllBytes();
            byte[] privateBytes = privateStream.readAllBytes();

            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
            PKCS8EncodedKeySpec keySpec2 = new PKCS8EncodedKeySpec(privateBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            return new KeyPair(keyFactory.generatePublic(keySpec), keyFactory.generatePrivate(keySpec2));
        }
    }
    public static String GetPDF(String type) throws Exception {
        switch(type) {
            case "PDF":
                if (!Files.exists(Path.of(pdfPath)))
                    GenerateSTestPDF();

                return pdfPath;
            case "signed":
                if(!Files.exists(Path.of(signedPath)))
                    GenerateVTestPDF();
                return signedPath;
            case "corrupted":
                if(!Files.exists(Path.of(corruptedPath)))
                    GenerateVTestPDF();
                return corruptedPath;
        }
        return type;
    }

    public static String GetPathToSignTo(){
        return signedWrite;
    }
}

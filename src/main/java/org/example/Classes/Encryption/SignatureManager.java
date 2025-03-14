package org.example.Classes.Encryption;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureInterface;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureOptions;

import java.io.*;
import java.security.*;
import java.util.*;

public class SignatureManager implements SignatureInterface {
    private PrivateKey privateKey;
    private PDDocument doc;

    public byte[] GetChosenHash(PDDocument document) throws Exception {
        PDPageTree pages = document.getPages();
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        for(PDPage page: pages) {
            byte[] contentBytes = (page.getContents().readAllBytes());
            digest.update(contentBytes);
        }

        return digest.digest();
    }

    public boolean SignPDF(String origPath, String signedPath, PrivateKey privateRSA) throws Exception {
        privateKey = privateRSA;
        File filePDF = new File(origPath);
        String outName = signedPath;
        try (PDDocument document = PDDocument.load(filePDF); OutputStream out = new FileOutputStream(outName)) {
            this.doc = document;
            PDSignature signature = new PDSignature();
            signature.setFilter(PDSignature.FILTER_ADOBE_PPKLITE);
            signature.setSubFilter(PDSignature.SUBFILTER_ADBE_PKCS7_DETACHED);
            signature.setName("Private Key Holder");
            signature.setLocation("Gdansk University of Technology, Gdsnsk");
            signature.setReason("Project needs");

            // the signing date, needed for valid signature
            signature.setSignDate(Calendar.getInstance());
            SignatureOptions opts = new SignatureOptions();
            opts.setPreferredSignatureSize(SignatureOptions.DEFAULT_SIGNATURE_SIZE);
            try{
                document.addSignature(signature, this, opts);
            }
            catch(java.io.IOException e) {
                throw new RuntimeException("Error during the signing process! The program" +
                        " couldn't create signature correctly. Please, " +
                        "report this problem to the producer of application!", e);
            }

            // Save the signed document to a file
            document.saveIncremental(out);

            document.close();
            System.out.println("Signed!");

            KillOneByte(new File(outName));

            return true;
        }
        catch(java.io.IOException e) {
            throw new RuntimeException("\"Error during the signing process! Ensure that the folder" +
                    "you've chosen to read document from exists and you " +
                    "have the necessary rights to read the file!", e);
        }
        catch(SecurityException e) {
            throw new RuntimeException("\"Error during the signing process! Ensure that the folder" +
                    "you've chosen to save signed file into exists and you " +
                    "have the necessary rights to create files there!", e);
        }
    }


    @Override
    public byte[] sign(InputStream content) throws IOException {
        try {
            // Step 1: Hash the content (e.g., using SHA-256)
            byte[] documentHash = GetChosenHash(this.doc);

            // Step 2: Sign the hash with your private key
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            this.privateKey = null;
            this.doc = null;
            signature.update(documentHash); // Update the signature with the hashed content

            byte[] tmp = signature.sign();

            //System.out.println(Arrays.toString(documentHash));

            return tmp; // Return the signed hash (digital signature)
        }
        catch(java.security.InvalidKeyException e) {
            throw new RuntimeException("Incorrect PRIVATE key file! Ensure that your PRIVATE key file" +
                    " is chosen correctly and exists!", e);
        }
        catch(java.security.SignatureException e) {
            throw new RuntimeException("Error during the signing process! The program" +
                    " couldn't create signature correctly. Please, " +
                    "report this problem to the producer of application!", e);
        }
        catch (Exception e) {
            throw new RuntimeException("Unresolved error", e);
        }
    }

    public boolean VerifyPDF(File filePDF, PublicKey publicRSA) throws Exception {
        try(PDDocument document = PDDocument.load(filePDF)) {

            // Extract the signature
            try {
                PDSignature signature = document.getLastSignatureDictionary();
                byte[] signedData = signature.getSignedContent(new FileInputStream(filePDF));
                // Step 2: Extract the cryptographic signature itself (actual signature bytes)
                byte[] signatureBytes = signature.getContents();

                // Use the public key to verify the signature
                Signature sig = Signature.getInstance("SHA256withRSA");
                sig.initVerify(publicRSA);

                byte[] documentHash = GetChosenHash(document);

                sig.update(documentHash);
                boolean isVerified = sig.verify(Arrays.copyOfRange(signatureBytes, 0, 512));

                document.close();

                return isVerified;
            }
            catch(java.io.IOException e) {
                throw new RuntimeException("Error during the verification process! Probably" +
                        "that document wasn't signed!", e);
            }
            catch(java.security.InvalidKeyException e) {
                throw new RuntimeException("Incorrect PUBLIC key file! Ensure that your PUBLIC key file" +
                        " is chosen correctly and exists", e);
            }
            catch(java.security.SignatureException e) {
                throw new RuntimeException("Error during the verification process! The program" +
                        " couldn't create signature correctly. Please, " +
                        "report this problem to the producer of application", e);
            }
        }
        catch(java.io.IOException e) {
            throw new RuntimeException("Cannot load chosen file! Ensure that the" +
                    "file was chosen correctly and exists", e);
        }
    }

    public void KillOneByte(File filePDF) throws Exception{
        String corrFile = filePDF.getAbsolutePath().substring(0, filePDF.getAbsolutePath().lastIndexOf(".")) + "_corrupted.pdf";
        try(PDDocument document = PDDocument.load(filePDF); FileOutputStream corrPDF = new FileOutputStream(corrFile)) {
            Random rnd = new Random();
            PDPageTree pages = document.getPages();
            int pageNumber = rnd.nextInt(pages.getCount());
            PDStream pdStream = pages.get(pageNumber).getContentStreams().next();

            byte[] pageBytes = pdStream.toByteArray();
            pageBytes[rnd.nextInt(pageBytes.length)] ^= (byte)(1<<rnd.nextInt(8));
            PDStream modifiedStream = new PDStream(document, new java.io.ByteArrayInputStream(pageBytes));
            pages.get(pageNumber).setContents(modifiedStream);

            document.save(corrPDF);
        }
    }
}

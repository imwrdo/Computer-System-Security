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

        return digest.digest() ;
    }

    public void SignPDF(String origPath, String signedPath, PrivateKey privateRSA, PublicKey publicRSA) throws Exception {
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
            document.addSignature(signature, this, opts);

            // Save the signed document to a file
            document.saveIncremental(new FileOutputStream(outName));

            document.close();
            System.out.println("Signed!");
        }
        KillOneByte(new File(outName));
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
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Error signing document", e);
        }
    }

    public boolean VerifyPDF(File filePDF, PublicKey publicRSA) throws Exception {
        PDDocument document = PDDocument.load(filePDF);

        // Extract the signature
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

        //System.out.println(Arrays.toString(documentHash));

        //System.out.println(isVerified);

        /*System.out.println("Signature " + (1) + ": ");
        System.out.println("Name: " + signature.getName());
        System.out.println("Location: " + signature.getLocation());
        System.out.println("Reason: " + signature.getReason());

        // Extract raw signature content (byte array)
        byte[] signatureContent = signature.getContents();

        // Debug: Print out the raw signature content in hexadecimal form
        System.out.println("Raw signature content (hex): ");
        for (byte b : signatureContent) {
            System.out.format("%02x ", b);
        }
        System.out.println();*/

        return isVerified;
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

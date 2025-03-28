package org.example;

import org.example.verify.PDFVerifyWindow;
import javax.swing.*;

public class VerificationAppMain {
    public static void main(String[] args) {
        System.out.println("Signature Verification App Start");
        PDFVerifyWindow vw = new PDFVerifyWindow();
        vw.setVisible(true);
        vw.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }
}
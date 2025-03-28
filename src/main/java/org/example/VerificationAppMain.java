package org.example;

import org.example.verify.PDFVerifyWindow;
import javax.swing.*;

/**
 * Main class for the Signature Verification application.
 */
public class VerificationAppMain {

    /**
     * Main method to launch the Signature Verification application.
     * @param args Command-line arguments (not used in this application).
     */
    public static void main(String[] args) {
        // Print message indicating the start of the application
        System.out.println("Signature Verification App Start");

        // Create an instance of PDFVerifyWindow
        PDFVerifyWindow vw = new PDFVerifyWindow();

        // Make the verification window visible
        vw.setVisible(true);

        // Set the default close operation to dispose the window when closed
        vw.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }
}
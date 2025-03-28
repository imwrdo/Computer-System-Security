package org.example;

import org.example.crypto.CryptoMainWindow;
import javax.swing.*;


/**
 * Main class for the Sign PDF application.
 */
public class SignAppMain {

    /**
     * Main method to start the application.
     * @param args Command-line arguments (not used in this application).
     */
    public static void main(String[] args) {
        // Print message indicating the start of the application
        System.out.println("Sign PDF App Start");

        // Create an instance of CryptoMainWindow
        CryptoMainWindow cryptoWindow = new CryptoMainWindow();

        // Set the visibility of the window to true
        cryptoWindow.setVisible(true);

        // Set the default close operation to dispose the window when closed
        cryptoWindow.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }
}
